## Using dynamic proxy to implement client-side calls to GraphQL server

This is an example of using dynamic proxy to implement client-side call server, so in addition to generating code, 
you almost do not need to write any code, only need to write a little code to call the server directly (This is a very small part of the method call code).

However, since we have simplified the client-side logic as much as possible, this method is more used when exposed as an SDK, 
so that the users do not need to choose the fields on projection. 
This will also have a bad effect, we can only use all fields of the projection on the returned structure by default. 
Moreover, we also need set to default the depth of nested queries so that the proxy can end smoothly. 
All in all, the scenarios used are limited. Currently, `.. on` and `alias` are not supported.

Here is only to provide a way, the specific implementation for reference only, not verified by production environment!

> Because reflection is heavily used, don't consider this approach if you need high performance.
  
1. First of all, we need to implement dynamic proxy, and be able to automatically add parameters to request and select return fields on projection.
   
```java

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * dynamic proxy for create request
 * <p>
 * this is a experimental implement
 *
 * @author liguobin@growingio.com
 * @version 1.0, 2020/7/28
 */
final public class DynamicProxy implements InvocationHandler, ExecutionGraphql {

    /**
     * this is graphql request need that what response fields.
     */
    private GraphQLResponseProjection projection;

    /**
     * this graphql request that need request params. (if have)
     */
    private GraphQLOperationRequest request;

    /**
     * should limit max depth when invoke method on projection.
     */
    private int maxDepth;

    DynamicProxy(GraphQLResponseProjection projection, GraphQLOperationRequest request, int maxDepth) {
        this.projection = projection;
        this.request = request;
        this.maxDepth = maxDepth;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        System.out.println("before Invoking");
        if (Object.class.equals(method.getDeclaringClass())) {
            try {
                System.out.println("invoking by resolver implement");
                return method.invoke(this, args);
            } catch (Throwable t) {
                t.printStackTrace();
                return null;
            }
        } else {
            System.out.println("invoking by dynamic proxy");
            return proxyInvoke(method, args);
        }
    }

    /**
     * proxy invoke
     * <p>
     * when handle projection, we use reflect to invoke method directly
     * but when handle request(need set parameters), we use reflect to get field which is a internal implementation of set method
     *
     * @param parentProjection
     * @param parentMethod
     * @param currentDepth
     */
    private void invokeOnProjection(GraphQLResponseProjection parentProjection, Method parentMethod, int currentDepth) {
        try {
            //if this method have no parameter, eg: name()
            //invoke method directly
            if (parentMethod.getParameterCount() == 0) {
                System.out.println("method <" + parentMethod.getName() + ">");
                parentMethod.invoke(parentProjection, null);
                return;
            }

            //if this method have parameters, eg: name(String alias) or friends(CharacterResponseProjection subProjection), friends(CharacterResponseProjection subProjection, String alias),
            //we only handle do not have any alias, like: friends(CharacterResponseProjection subProjection)
            for (Class<?> parameterClazz : parentMethod.getParameterTypes()) {
                //handler the one parameter method
                if (parentMethod.getParameterCount() == 1) {
                    if (GraphQLResponseProjection.class.isAssignableFrom(parameterClazz)) {
                        currentDepth++;
                        GraphQLResponseProjection subProjection = (GraphQLResponseProjection) parameterClazz.newInstance();
                        //at now,not support `..on`
                        List<Method> methods = Arrays.stream(parameterClazz.getDeclaredMethods()).filter(m -> !m.getName().startsWith("on")).collect(Collectors.toList());
                        for (Method subProjectionMethod : methods) {
                            //if this method have no parameter
                            if (subProjectionMethod.getParameterCount() == 0) {
                                String t = "->";
                                for (int i = 0; i < currentDepth; i++) {
                                    t = t.concat("->");
                                }
                                System.out.println(t + " method <" + subProjectionMethod.getName() + ">");
                                subProjectionMethod.invoke(subProjection, null);
                            } else if (subProjectionMethod.getParameterCount() == 1 && GraphQLResponseProjection.class.isAssignableFrom(subProjectionMethod.getParameterTypes()[0])) {
                                //if this method have one parameter and type is GraphQLResponseProjection sub class
                                //recursive continuation call
                                if (currentDepth < maxDepth) {
                                    invokeOnProjection(subProjection, subProjectionMethod, currentDepth);
                                }
                            } else {
                                //TODO getParameterCount == 2, (GraphQLResponseProjection sub and String alias)
                            }
                        }
                        parentMethod.invoke(parentProjection, subProjection);
                    }
                } else {
                    //TODO getParameterCount == 2, (GraphQLResponseProjection sub and String alias)
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }


    /**
     * execute graphql request that need jvm compile parameter `-parameters` and Java8 or above
     * you should clean class, and build again after set -parameters
     * in sbt like this: javacOptions += "-parameters"
     *
     * @param method
     * @param args
     * @return
     */
    private Object proxyInvoke(Method method, Object[] args) {
        int i = 0;
        Field field = null;
        List<GraphQLResponseField> fields = null;
        String entityClazzName;
        //handle List
        Type type = method.getGenericReturnType();
        //it is more rigorous to judge whether it is a List
        if (type instanceof ParameterizedType) {
            Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
            entityClazzName = parameterizedType[0].getTypeName();
        } else {
            entityClazzName = type.getTypeName();
        }
        List<Parameter> parameters = Arrays.stream(method.getParameters()).collect(Collectors.toList());

        //if this method have no parameter, then do not need invoke on request instance
        //other wise, we need append parameters to request field which use hashmap store params
        if (!parameters.isEmpty()) {
           for (Parameter parameter : parameters) {
               Object argsCopy = args[i++];
               request.getInput().put(parameter.getName(), argsCopy);
           }
        }
        try {
            field = projection.getClass().getSuperclass().getDeclaredField("fields");
            field.setAccessible(true);
            fields = (List<GraphQLResponseField>) field.get(projection);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (field != null) {
                field.setAccessible(false);
            }
        }

        //if fields not null, use it directly, because user want to select fields
        if (projection != null && (fields == null || fields.isEmpty())) {
            for (Method m : projection.getClass().getDeclaredMethods()) {
                invokeOnProjection(projection, m, 1);
            }
        }

        return executeByHttp(entityClazzName, request, projection);// request and projection for creating GraphQLRequest, entityClazzName for deserialize
    } 
}
```

2. Then we need to be able to proxy any resolver implementation and enable users to call resolver methods through proxy objects.

```java

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

import java.lang.reflect.Proxy;

/**
 * invoker for proxy
 *
 * @author liguobin@growingio.com
 * @version 1.0, 2020/7/28
 */
final public class ResolverImplClient {

    private Class<?> resolver;
    private GraphQLResponseProjection projection;
    private GraphQLOperationRequest request;
    private int maxDepth;

    /**
     * create proxy object, so we do not need write resolver interface' impl
     * but we need use all response entity'fields on projection
     *
     * @return
     */
    private Object getResolver() {
        DynamicProxy invocationHandler = new DynamicProxy(projection, request, maxDepth);
        return Proxy.newProxyInstance(resolver.getClassLoader(), new Class[]{resolver}, invocationHandler);
    }

    private void setResolver(Class<?> resolver) {
        this.resolver = resolver;
    }

    private void setRequest(GraphQLOperationRequest request) {
        this.request = request;
    }

    private void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    private void setProjection(GraphQLResponseProjection projection) {
        this.projection = projection;
    }


    public static class ResolverImplClientBuilder {
        private GraphQLResponseProjection projection;
        private GraphQLOperationRequest request;
        private int maxDepth = 3;

        private ResolverImplClientBuilder() {

        }

        /**
         * must set request which maybe contains come input parameters
         *
         * @param request
         * @return
         */
        public ResolverImplClientBuilder setRequest(GraphQLOperationRequest request) {
            this.request = request;
            return this;
        }

        /**
         * default query max depth is 3, so children fields will query until depth is 3
         *
         * @param maxDepth
         * @return
         */
        public ResolverImplClientBuilder setMaxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        /**
         * must set response projection, then the server can know which fields can be return
         *
         * @param projection
         * @return
         */
        public ResolverImplClientBuilder setProjection(GraphQLResponseProjection projection) {
            this.projection = projection;
            return this;
        }

        /**
         * should be invoked at the first to create a builder
         *
         * @return
         */
        public static ResolverImplClientBuilder newBuilder() {
            return new ResolverImplClientBuilder();
        }

        /**
         * Resolver is generic type, if we do not want to cast to real resolver on the user side, we need set resolver when invoker builder method,
         * although this is not in line with the conventional builder model
         *
         * @return R resolver which need for invoke graphql
         */
        @SuppressWarnings(value = "unchecked")
        public <R> R build(Class<R> resolver) {
            ResolverImplClient invoke = new ResolverImplClient();
            assert (projection != null);
            assert (resolver != null);
            assert (request != null);
            invoke.setProjection(projection);
            invoke.setResolver(resolver);
            invoke.setMaxDepth(maxDepth);
            invoke.setRequest(request);
            return (R) invoke.getResolver();
        }
    }

}
```

3. Then, we use `sbt graphqlcodegen` to generate the client and API.

```
Finished processing 1 schema(s) in 788 ms
[success] HumanDO.java
[success] HumanResponseProjection.java
[success] ProductDO.javaCompile / graphqlCodegen 0s
[success] ProductResponseProjection.java
[success] DroidDO.java
[success] DroidResponseProjection.java
[success] QueryResolver.java
[success] HeroQueryResolver.java
[success] HumanQueryResolver.java
[success] HumansQueryResolver.java
[success] DroidQueryResolver.java
[success] ProductsQueryResolver.java
[success] ProductByIdQueryResolver.java
[success] ProductsByIdsQueryResolver.java
[success] HeroQueryRequest.java
[success] HeroQueryResponse.java
[success] HumanQueryRequest.java
[success] HumanQueryResponse.java
[success] HumansQueryRequest.java
[success] HumansQueryResponse.java
[success] DroidQueryRequest.java
[success] DroidQueryResponse.java
[success] ProductsQueryRequest.java
[success] ProductsQueryResponse.java
[success] ProductByIdQueryRequest.java
[success] ProductByIdQueryResponse.java
[success] ProductsByIdsQueryRequest.java
[success] ProductsByIdsQueryResponse.java
[success] MutationResolver.java
[success] CreateMutationResolver.java
[success] CreateMutationRequest.java
```

4. Finally, we can use it in Java or Scala.

we do not need write any other code, just use the generated class to call.

```java

import io.github.dreamylost.api.HumanQueryResolver;
import io.github.dreamylost.api.HumansQueryResolver;
import io.github.dreamylost.model.HumanDO;
import io.github.dreamylost.model.HumanQueryRequest;
import io.github.dreamylost.model.HumanResponseProjection;
import io.github.dreamylost.model.HumansQueryRequest;

import java.util.List;

/**
 * @author liguobin@growingio.com
 * @version 1.0, 2020/7/29
 */
public class HumanResolverJavaApp {

    public static void main(String[] args) {

        ResolverImplClient.ResolverImplClientBuilder humanInvokerBuilder = ResolverImplClient.ResolverImplClientBuilder.newBuilder().
                setProjection(new HumanResponseProjection());
        //Set your own request and resolver for each request
        try {
            System.out.println("======human========");
            HumanDO humanDO = humanInvokerBuilder.setRequest(new HumanQueryRequest()).build(HumanQueryResolver.class).human("1001");
            assert humanDO.getEmail() == "dreamylost@outlook.com";
            System.out.println(humanDO);

            System.out.println("======humans========");
            List<HumanDO> humanDOs = humanInvokerBuilder.setRequest(new HumansQueryRequest()).build(HumansQueryResolver.class).humans();
            assert humanDOs.size() == 4;
            System.out.println(humanDOs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

```scala

import io.github.dreamylost.api.{ HumanQueryResolver, HumansQueryResolver }
import io.github.dreamylost.model.{ HumanQueryRequest, HumanResponseProjection, HumansQueryRequest }

import scala.collection.JavaConverters._

/**
 * use invoke by proxy in scala
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/7/28
 */
object HumanResolverScalaApp extends App {

  //For a model, the projection and maximum depth fields can be common
  val humanInvokerBuilder = ResolverImplClient.ResolverImplClientBuilder.newBuilder().
    setProjection(new HumanResponseProjection())

  //Set your own request and resolver for each request
  println("======human========")
  val human = humanInvokerBuilder.setRequest(new HumanQueryRequest).build(classOf[HumanQueryResolver]).human("1001")
  println(human)

  println("======humans========")
  val humans = humanInvokerBuilder.setRequest(new HumansQueryRequest).build(classOf[HumansQueryResolver]).humans().asScala
  for (human <- humans) {
    println("->" + human)
  }
}
```

Here are some auxiliary codes, It depends on the different users.
                               
```java
public interface ExecutionGraphql {

    default Object executeByHttp(String entityClazzName, GraphQLOperationRequest request, GraphQLResponseProjection projection) {
        GraphQLRequest graphQLRequest = new GraphQLRequest(request, projection);
        Future retFuture;
        Object ret = null;
        try {
            retFuture = OkHttp.createExecuteRequest(graphQLRequest, entityClazzName);

            ret = Await.result(retFuture, Duration.Inf());
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
```

```scala
object OkHttp extends DataDeserializer {

  var url = "http://localhost:8080/graphql"
  val defaultCharset = "utf8"
  val json = MediaType.parse("application/json; charset=utf-8")

  private lazy val defaultTimeout: Long = TimeUnit.MINUTES.toMillis(1)
  lazy val client: OkHttpClient = buildClient(defaultTimeout, defaultTimeout, defaultTimeout)

  def buildClient(readTimeout: Long, writeTimeout: Long, connectTimeout: Long): OkHttpClient = {
    new OkHttpClient.Builder()
      .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
      .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
      .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
      .protocols(util.Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2))
      .build()
  }

  def buildRequest[T](request: GraphQLRequest) = {
    val httpRequestBody = request.toHttpJsonBody
    println(s"graphql request body: < $httpRequestBody >")
    val rb = new Request.Builder().url(url).addHeader("Accept", "application/json; charset=utf-8").
      post(RequestBody.create(httpRequestBody, json))
    val promise = Promise[T]
    rb -> promise
  }

  /**
   * for java api and proxy
   *
   * @param request
   * @param entityClazzName
   * @return
   */
  def createExecuteRequest(request: GraphQLRequest, entityClazzName: String): Future[Any] = {
    val (rb, promise) = buildRequest[Any](request)
    OkHttp.client.newCall(rb.build()).enqueue(new Callback {

      override def onFailure(call: Call, e: IOException): Unit = {
        promise.failure(e)
      }

      override def onResponse(call: Call, response: Response): Unit = {
        if (response.isSuccessful) {
          val bytes = response.body().bytes()
          val jsonStr = new String(bytes, defaultCharset)
          val jsonObject = new JSONObject(jsonStr)
          val data = jsonObject.getJSONObject("data").get(request.getRequest.getOperationName)
          promise.success(deserialize(data, entityClazzName))

        } else {
          Future.successful()
        }

      }
    })
    promise.future
  }
}
```

```scala
trait DataDeserializer {

  /**
   * if data is array, entityClazzName is a parameterized type.
   * because can not deserializer from Collection directly, generic type is lost
   *
   * @param data
   * @param entityClazzName
   */
  def deserialize(data: AnyRef, entityClazzName: String) = {
    val result = new java.util.ArrayList[Any]()
    val targetReturnClazz = Class.forName(entityClazzName)
    data match {
      case array: JSONArray =>
        for (i <- 0 until array.length()) {
          val e = Jackson.mapper.readValue(array.get(i).asInstanceOf[JSONObject].toString, targetReturnClazz)
          result.add(e)
        }
        result
      case _ => Jackson.mapper.readValue(data.asInstanceOf[JSONObject].toString, targetReturnClazz)
    }
  }

}
```
   


