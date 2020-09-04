package io.github.dreamylost

import java.io.IOException
import java.util
import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.core.`type`.TypeReference
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest
import okhttp3._
import org.json.JSONObject

import scala.concurrent.{ ExecutionContext, Future, Promise }

object OkHttp {

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

  /**
   *
   * @param request
   * @param valueType must get from invoker, ClassTag is invalid
   * @param ec
   * @tparam T
   * @return
   */
  def executeRequest[T](request: GraphQLRequest, valueType: TypeReference[T])(implicit ec: ExecutionContext): Future[T] = {
    val rb = new Request.Builder().url(url).addHeader("Accept", "application/json; charset=utf-8").
      post(RequestBody.create(request.toHttpJsonBody, json))
    val promise = Promise[T]

    println("Graphql query " + request.toHttpJsonBody)
    OkHttp.client.newCall(rb.build()).enqueue(new Callback {

      override def onFailure(call: Call, e: IOException): Unit = {
        promise.failure(e)
      }

      override def onResponse(call: Call, response: Response): Unit = {
        if (response.isSuccessful) {
          //          val clazz = implicitly[ClassTag[T]].runtimeClass
          val jsonObject = new JSONObject(response.body().string())
          val res = jsonObject.getJSONObject("data").get(request.getRequest.getOperationName)
          val result = Jackson.mapper.readValue(res.toString, valueType)
          promise.success(result.asInstanceOf[T])

        } else {
          Future.successful()
        }

      }
    })
    promise.future
  }
}
