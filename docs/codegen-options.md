# Codegen Options

| Option                                          | Data Type                                          | Default value                                 | Description |
| :---------------------------------------------: | :------------------------------------------------: | :-------------------------------------------: | ----------- |
| `graphqlSchemaPaths`                            | List(String)                                       | (falls back to `graphqlSchemas`)              | GraphQL schema locations. You can supply multiple paths to GraphQL schemas. To include many schemas from a folder hierarchy, use the `graphqlSchemas` block instead. |
| `graphqlSchemas`                                | *See [graphqlSchemas](#option-graphqlschemas)*     | All `.graphqls`/`.graphql` files in resources | Block to define the input GraphQL schemas, when exact paths are too cumbersome. See table below for a list of options. |
| `outputDir`                                     | String                                             | None                                          | The output target directory into which code will be generated. |
| `jsonConfigurationFile`                         | String                                             | Empty                                         | Path to an external mapping configuration. |
| `packageName`                                   | String                                             | Empty                                         | Java package for generated classes. |
| `apiPackageName`                                | String                                             | Empty                                         | Java package for generated api classes (Query, Mutation, Subscription). |
| `modelPackageName`                              | String                                             | Empty                                         | Java package for generated model classes (type, input, interface, enum, union). |
| `generateBuilder`                               | Boolean                                            | True                                          | Specifies whether generated model classes should have builder. |
| `generateApis`                                  | Boolean                                            | True                                          | Specifies whether api classes should be generated as well as model classes. |
| `generateAsyncApi`                              | Boolean                                            | False                                         | If true, then wrap type into `java.util.concurrent.CompletableFuture` or `subscriptionReturnType` |
| `generateDataFetchingEnvironmentArgumentInApis` | Boolean                                            | False                                         | If true, then `graphql.schema.DataFetchingEnvironment env` will be added as a last argument to all methods of root type resolvers and field resolvers. |
| `generateEqualsAndHashCode`                     | Boolean                                            | False                                         | Specifies whether generated model classes should have equals and hashCode methods defined. |
| `generateToString`                              | Boolean                                            | False                                         | Specifies whether generated model classes should have toString method defined. |
| `apiNamePrefix`                                 | String                                             | Empty                                         | Sets the prefix for GraphQL api classes (query, mutation, subscription). |
| `apiNameSuffix`                                 | String                                             | Resolver                                      | Sets the suffix for GraphQL api classes (query, mutation, subscription). |
| `modelNamePrefix`                               | String                                             | Empty                                         | Sets the prefix for GraphQL model classes (type, input, interface, enum, union). |
| `modelNameSuffix`                               | String                                             | Empty                                         | Sets the suffix for GraphQL model classes (type, input, interface, enum, union). |
| `modelValidationAnnotation`                     | String                                             | @javax.validation.<br>constraints.NotNull     | Annotation for mandatory (NonNull) fields. Can be null/empty. |
| `customTypesMapping`                            | Map(String,String)                                 | Empty                                         | Can be used to supply custom mappings for scalars. <br/> Supports:<br/> * Map of (GraphqlObjectName.fieldName) to (JavaType) <br/> * Map of (GraphqlType) to (JavaType) |
| `customAnnotationsMapping`                      | Map(String,String)                                 | Empty                                         | Can be used to supply custom annotations (serializers) for scalars. <br/> Supports:<br/> * Map of (GraphqlObjectName.fieldName) to (JavaAnnotation) <br/> * Map of (GraphqlType) to (JavaAnnotation) |
| `fieldsWithResolvers`                           | Set(String)                                        | Empty                                         | Fields that require Resolvers should be defined here in format: `TypeName.fieldName` or `TypeName`. |
| `fieldsWithoutResolvers`                        | Set(String)                                        | Empty                                         | Fields that DO NOT require Resolvers should be defined here in format: `TypeName.fieldName` or `TypeName`. Can be used in conjunction with `generateExtensionFieldsResolvers` option. |
| `generateParameterizedFieldsResolvers`          | Boolean                                            | True                                          | If true, then generate separate `Resolver` interface for parametrized fields. If false, then add field to the type definition and ignore field parameters. |
| `generateExtensionFieldsResolvers`              | Boolean                                            | False                                         | Specifies whether all fields in extensions (`extend type` and `extend interface`) should be present in Resolver interface instead of the type class itself. |
| `subscriptionReturnType`                        | String                                             | Empty                                         | Return type for subscription methods. For example: `org.reactivestreams.Publisher`, `io.reactivex.Observable`, etc. |
| `generateClient`                                | Boolean                                            | False                                         | Specifies whether client-side classes should be generated for each query, mutation and subscription. This includes: `Request` classes (contain input data), `ResponseProjection` classes for each type (contain response fields) and `Response` classes (contain response data). |
| `requestSuffix`                                 | String                                             | Request                                       | Sets the suffix for `Request` classes. |
| `responseSuffix`                                | String                                             | Response                                      | Sets the suffix for `Response` classes. |
| `responseProjectionSuffix`                      | String                                             | ResponseProjection                            | Sets the suffix for `ResponseProjection` classes. |
| `parametrizedInputSuffix`                       | String                                             | ParametrizedInput                             | Sets the suffix for `ParametrizedInput` classes. |
| `parentInterfaces`                              | *See [parentInterfaces](#option-parentinterfaces)* | Empty                                         | Block to define parent interfaces for generated interfaces (query / mutation / subscription / type resolver) |


### Option `graphqlSchemas`

When exact paths to GraphQL schemas are too cumbersome to provide in the `graphqlSchemaPaths`, use the `graphqlSchemas` block.
The parameters inside that block are the following:

| Key inside `graphqlSchemas` | Data Type    | Default value      | Description |
| --------------------------- | ------------ | ------------------ | ----------- |
| `rootDir`                   | String       | Main resources dir | The root directory from which to start searching for schema files. |
| `recursive`                 | Boolean      | `true`             | Whether to recursively look into sub directories. |
| `includePattern`            | String       | `.*\.graphqls?`    | A Java regex that file names must match to be included. It should be a regex as defined by the [Pattern](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html) JDK class. It will be used to match only the file name without path. |
| `excludedFiles`             | Set<String>  | (empty set)        | A set of files to exclude, even if they match the include pattern. These paths should be either absolute or relative to the provided `rootDir`. |


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



#### External mapping configuration

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