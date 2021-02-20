# Codegen Options

| Option                                                | Data Type                                                             | Default value                                          | Description |
| :---------------------------------------------------: | :-------------------------------------------------------------------: | :----------------------------------------------------: | ----------- |
| `graphqlSchemaPaths`                                  | List(String)                                                          | (falls back to `graphqlSchemas`)                       | GraphQL schema locations. You can supply multiple paths to GraphQL schemas. To include many schemas from a folder hierarchy, use the `graphqlSchemas` block instead. |
| `graphqlSchemas`                                      | *See<br>[graphqlSchemas](#option-graphqlschemas)*                     | All<br>`.graphqls`/`.graphql`<br>files in<br>resources | Block to define the input GraphQL schemas, when exact paths are too cumbersome. See table below for a list of options. *See [graphqlSchemas](#option-graphqlschemas)* |
| `graphqlQueryIntrospectionResu`<br>`ltPath`           | String                                                                | None                                                   | Path to GraphQL Introspection Query result in json format (with root object `__schema` or `data.__schema`). Sample: [sample-introspection-query-result.json](../src/test/resources/introspection-result/sample-introspection-query-result.json)|
| `outputDir`                                           | String                                                                | None                                                   | The output target directory into which code will be generated. |
| `jsonConfigurationFile`                               | String                                                                | Empty                                                  | Path to an external mapping configuration. |
| `packageName`                                         | String                                                                | Empty                                                  | Java package for generated classes. |
| `apiPackageName`                                      | String                                                                | Empty                                                  | Java package for generated api classes (Query, Mutation, Subscription). |
| `modelPackageName`                                    | String                                                                | Empty                                                  | Java package for generated model classes (type, input, interface, enum, union). |
| `generateBuilder`                                     | Boolean                                                               | True                                                   | Specifies whether generated model classes should have builder. |
| `generateApis`                                        | Boolean                                                               | True                                                   | Specifies whether api classes should be generated as well as model classes. |
| `generateDataFetchingEnvironme`<br>`ntArgumentInApis` | Boolean                                                               | False                                                  | If true, then `graphql.schema.DataFetchingEnvironment env` will be added as a last argument to all methods of root type resolvers and field resolvers. |
| `generateEqualsAndHashCode`                           | Boolean                                                               | False                                                  | Specifies whether generated model classes should have equals and hashCode methods defined. |
| `generateParameterizedFieldsResolvers`                | Boolean                                                               | True                                                   | Specifies whether separate `Resolver` interface for parametrized fields should be generated. If `false`, then add parametrized field to the type definition and ignore field parameters. If `true` then separate `Resolver` interface for parametrized fields will be generated. |
| `generateImmutableModels`                             | Boolean                                                               | False                                                  | Specifies whether generated model classes should be immutable. |
| `generateToString`                                    | Boolean                                                               | False                                                  | Specifies whether generated model classes should have toString method defined. |
| `addGeneratedAnnotation`                              | Boolean                                                               | True                                                   | Specifies whether generated classes should have `@Generated` annotation. |
| `apiNamePrefix`                                       | String                                                                | Empty                                                  | Sets the prefix for GraphQL api classes (query, mutation, subscription). |
| `apiNameSuffix`                                       | String                                                                | `Resolver`                                             | Sets the suffix for GraphQL api classes (query, mutation, subscription). |
| `apiInterfaceStrategy`                                | *See<br>[ApiInterfaceStrategy](#option-apiinterfacestrategy)*         | `INTERFACE_PER_OPERATION`                              | *See [ApiInterfaceStrategy](#option-apiinterfacestrategy)* |
| `apiRootInterfaceStrategy`                            | *See<br>[ApiRootInterfaceStrategy](#option-apirootinterfacestrategy)* | `SINGLE_INTERFACE`                                     | *See [ApiRootInterfaceStrategy](#option-apirootinterfacestrategy)* |
| `apiNamePrefixStrategy`                               | *See<br>[ApiNamePrefixStrategy](#option-apinameprefixstrategy)*       | `CONSTANT`                                             | *See [ApiNamePrefixStrategy](#option-apinameprefixstrategy)* |
| `modelNamePrefix`                                     | String                                                                | Empty                                                  | Sets the prefix for GraphQL model classes (type, input, interface, enum, union). |
| `modelNameSuffix`                                     | String                                                                | Empty                                                  | Sets the suffix for GraphQL model classes (type, input, interface, enum, union). |
| `modelValidationAnnotation`                           | String                                                                | `@javax.validation.`<br>`constraints.NotNull`          | Annotation for mandatory (NonNull) fields. Can be null/empty. |
| `typeResolverPrefix`                                  | String                                                                | Empty                                                  | Sets the prefix for GraphQL type resolver classes. |
| `typeResolverSuffix`                                  | String                                                                | `Resolver`                                             | Sets the suffix for GraphQL type resolver classes. |
| `customTypesMapping`                                  | Map(String,String)                                                    | Empty                                                  | *See [CustomTypesMapping](#option-customtypesmapping)*  |
| `customAnnotationsMapping`                            | Map(String,String[])                                                  | Empty                                                  | *See [CustomAnnotationsMapping](#option-customannotationsmapping)*  |
| `directiveAnnotationsMapping`                         | Map(String,String[])                                                  | Empty                                                  | *See [DirectiveAnnotationsMapping](#option-directiveannotationsmapping)* |
| `fieldsWithResolvers`                                 | Set(String)                                                           | Empty                                                  | Fields that require Resolvers should be defined here in format: `TypeName.fieldName` or `TypeName` or `@directive`. E.g.: `Person`, `Person.friends`, `@customResolver`. |
| `fieldsWithoutResolvers`                              | Set(String)                                                           | Empty                                                  | Fields that DO NOT require Resolvers should be defined here in format: `TypeName.fieldName` or `TypeName` or `@directive`. Can be used in conjunction with `generateExtensionFieldsResolvers` option. E.g.: `Person`, `Person.friends`, `@noResolver`. |
| `generateParameterizedFieldsR`<br>`esolvers`          | Boolean                                                               | True                                                   | If true, then generate separate `Resolver` interface for parametrized fields. If false, then add field to the type definition and ignore field parameters. |
| `generateExtensionFieldsResol`<br>`vers`              | Boolean                                                               | False                                                  | Specifies whether all fields in extensions (`extend type` and `extend interface`) should be present in Resolver interface instead of the type class itself. |
| `generateModelsForRootTypes`                          | Boolean                                                               | False                                                  | Specifies whether model classes should be generated for `type Query`, `type Subscription`, `type Mutation`. |
| `useOptionalForNullableReturn`<br>`Types`             | Boolean                                                               | False                                                  | Specifies whether nullable return types of api methods should be wrapped into [`java.util.Optional<>`](https://docs.oracle.com/javase/8/docs/api/index.html?java/util/Optional.html). Lists will not be wrapped. |
| `generateApisWithThrowsExcept`<br>`ion`               | Boolean                                                               | True                                                   | Specifies whether api interface methods should have `throws Exception` in signature. |
| `apiReturnType`                                       | String                                                                | Empty                                                  | Return type for api methods (query/mutation). For example: `reactor.core.publisher.Mono`, etc. |
| `apiReturnListType`                                   | String                                                                | Empty                                                  | Return type for api methods (query/mutation) having list type. For example: `reactor.core.publisher.Flux`, etc. By default is empty, so `apiReturnType` will be used. |
| `subscriptionReturnType`                              | String                                                                | Empty                                                  | Return type for subscription methods. For example: `org.reactivestreams.Publisher`, `io.reactivex.Observable`, etc. |
| `relayConfig`                                         | *See<br>[RelayConfig](#option-relayconfig)*                           | `@connection(for: ...)`                                | *See [RelayConfig](#option-relayconfig)* |
| `generateClient`                                      | Boolean                                                               | False                                                  | Specifies whether client-side classes should be generated for each query, mutation and subscription. This includes: `Request` classes (contain input data), `ResponseProjection` classes for each type (contain response fields) and `Response` classes (contain response data). |
| `requestSuffix`                                       | String                                                                | Request                                                | Sets the suffix for `Request` classes. |
| `responseSuffix`                                      | String                                                                | Response                                               | Sets the suffix for `Response` classes. |
| `responseProjectionSuffix`                            | String                                                                | ResponseProjection                                     | Sets the suffix for `ResponseProjection` classes. |
| `parametrizedInputSuffix`                             | String                                                                | ParametrizedInput                                      | Sets the suffix for `ParametrizedInput` classes. |
| `parentInterfaces`                                    | *See<br>[parentInterfaces](#option-parentinterfaces)*                 | Empty                                                  | Block to define parent interfaces for generated interfaces (query / mutation / subscription / type resolver). *See [parentInterfaces](#option-parentinterfaces)* |
| `responseProjectionMaxDepth`                          | Integer                                                               | 3                                                      | Sets max depth when use `all$()` which for facilitating the construction of projection automatically, the fields on all projections are provided when it be invoked. This is a global configuration, of course, you can use `all$(max)` to set for each method. For self recursive types, too big depth may result in a large number of returned data!|
| `generatedLanguage`                                   | Enum                                                                  | GeneratedLanguage.JAVA                                 | Choose which language you want to generate, Java,Scala,Kotlin were supported. Note that due to language features, there are slight differences in default values between languages.|
| `generateModelOpenClasses`                            | Boolean                                                               | false                                                  | The class type of the generated model. If true, generate normal classes, else generate data classes. It only support in kotlin(```data class```) and scala(```case class```). Maybe we will consider to support Java ```record``` in the future.|

### Option `graphqlSchemas`

When exact paths to GraphQL schemas are too cumbersome to provide in the `graphqlSchemaPaths`, use the `graphqlSchemas` block.
The parameters inside that block are the following:

| Key inside `graphqlSchemas` | Data Type    | Default value      | Description |
| --------------------------- | ------------ | ------------------ | ----------- |
| `rootDir`                   | String       | Main resources dir | The root directory from which to start searching for schema files. |
| `recursive`                 | Boolean      | `true`             | Whether to recursively look into sub directories. |
| `includePattern`            | String       | `.*\.graphqls?`    | A Java regex that file names must match to be included. It should be a regex as defined by the [Pattern](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html) JDK class. It will be used to match only the file name without path. |
| `excludedFiles`             | Set<String>  | (empty set)        | A set of files to exclude, even if they match the include pattern. These paths should be either absolute or relative to the provided `rootDir`. |


### Option `ApiInterfaceStrategy`

Defines how to generate interfaces (resolvers) for each operation: `Query`/`Mutation`/`Subscription`.
Provides ability to skip generation of separate interface class for each operation in favor of having a single "root" interface (see *[ApiRootInterfaceStrategy](#option-apirootinterfacestrategy)* and *[ApiNamePrefixStrategy](#option-apinameprefixstrategy)*)

| Value                                   | Description |
| --------------------------------------- | ----------- |
| `INTERFACE_PER_OPERATION` **(default)** | Generate separate interface classes for each GraphQL operation. |
| `DO_NOT_GENERATE`                       | Do not generate separate interfaces classes for GraphQL operation. |


### Option `ApiRootInterfaceStrategy`

Defines how root interface (`QueryResolver` / `MutationResolver` / `SubscriptionResolver` will be generated (in addition to separate interfaces for each query/mutation/subscription)

| Value                            | Description |
| -------------------------------- | ----------- |
| `INTERFACE_PER_SCHEMA`           | Generate multiple super-interfaces for each graphql file. <br>Takes into account `apiNamePrefixStrategy`. <br>E.g.: `OrderServiceQueryResolver.java`, `ProductServiceQueryResolver.java`, etc. |
| `SINGLE_INTERFACE` **(default)** | Generate a single `QueryResolver.java`, `MutationResolver.java`, `SubscriptionResolver.java` for all graphql schema files. |
| `DO_NOT_GENERATE`                | Do not generate super interface for GraphQL operations. |


### Option `ApiNamePrefixStrategy`

Defines which prefix to use for API interfaces.

| Value                    | Description |
| ------------------------ | ----------- |
| `FILE_NAME_AS_PREFIX`    | Will take GraphQL file name as a prefix for all generated API interfaces + value of `apiNamePrefix` config option. <br>E.g.:<br> * following schemas: *resources/schemas/order-service.graphql*, *resources/schemas/product-service.graphql*<br> * will result in: `OrderServiceQueryResolver.java`, `ProductServiceQueryResolver.java`, etc |
| `FOLDER_NAME_AS_PREFIX`  | Will take parent folder name as a prefix for all generated API interfaces + value of `apiNamePrefix` config option. E.g.:<br> * following schemas: *resources/order-service/schema1.graphql*, *resources/order-service/schema2.graphql*<br> * will result in: `OrderServiceQueryResolver.java`, `OrderServiceGetOrderByIdQueryResolver.java`, etc |
| `CONSTANT` **(default)** | Will take only the value of `apiNamePrefix` config option. |


### Option `parentInterfaces`

Following options can be defined if you want generated resolvers to extend certain interfaces.
Can be handy if you are using [graphql-java-tools](https://github.com/graphql-java-kickstart/graphql-java-tools) and want your resolver classes to extend only interfaces generated by this plugin.

**Note:** if you want to include a GraphQL type name into the interface name, then use `{{TYPE}}` placeholder. E.g.: `graphql.kickstart.tools.GraphQLResolver<{{TYPE}}>`

| Key inside `parentInterfaces` | Data Type | Default value | Description |
| ----------------------------- | --------- | ------------- | ----------- |
| `queryResolver`               | String    | Empty         | Interface that will be added as "extend" to all generated api Query interfaces. |
| `mutationResolver`            | String    | Empty         | Interface that will be added as "extend" to all generated api Mutation interfaces. |
| `subscriptionResolver`        | String    | Empty         | Interface that will be added as "extend" to all generated api Subscription interfaces. |
| `resolver`                    | String    | Empty         | Interface that will be added as "extend" to all generated TypeResolver interfaces. |


### Option `customTypesMapping`

Can be used to supply custom mappings for scalars. 

Supports following formats:
* Map of (GraphQLObjectName.fieldName) to (JavaType). E.g.: `Event.dateTime = java.util.Date` 
* Map of (GraphQLType) to (JavaType). E.g.: `EpochMillis = java.time.LocalDateTime`


### Option `customAnnotationsMapping`

Can be used to supply custom annotations (serializers) for scalars.
`@` in front of the annotation class is optional. 

Supports following formats:
* Map of (GraphQLObjectName.fieldName) to (JavaAnnotation). E.g.: `Event.dateTime = @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.DateDeserializer.class)`
* Map of (GraphQLType) to (JavaAnnotation). E.g.: `EpochMillis = @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.EpochMillisDeserializer.class)`


### Option `directiveAnnotationsMapping`

Can be used to supply custom annotations for directives in a following format: 
Map of (GraphQL.directiveName) to (JavaAnnotation). E.g.: `auth = @org.springframework.security.access.annotation.Secured({{roles}})`.
`@` in front of the annotation class is optional.

**Note:** In order to supply the value of directive argument to annotation, use placeholder `{{directiveArgument}}`. 
You can also use one of the formatters for directive argument value: `{{val?toString}}`, `{{val?toArray}}`, `{{val?toArrayOfStrings}}`.



### Option `relayConfig`

Can be used to supply a custom configuration for Relay support.
For reference see: https://www.graphql-java-kickstart.com/tools/relay/

| Key inside `relayConfig` | Data Type | Default value              | Description |
| ------------------------ | --------- | -------------------------- | ----------- |
| `directiveName`          | String    | `connection`               | Directive name used for marking a field. |
| `directiveArgumentName`  | String    | `for`                      | Directive argument name that contains a GraphQL type name. |
| `connectionType`         | String    | `graphql.relay.Connection` | Generic Connection type. |

For example, the following schema:
```
type Query { users(first: Int, after: String): UserConnection @connection(for: "User") }
```
will result in generating the interface with the following method:
```
graphql.relay.Connection<User> users(Integer first, String after) throws Exception;
```



### External mapping configuration

Provide a path to external file via property `jsonConfigurationFile`
Sample content of the file:

```json
{
  "generateApis": true,
  "packageName": "com.kobylynskyi.graphql.testconfigjson",
  "customTypesMapping": {
    "Price.amount": "java.math.BigDecimal"
  }
}
```
