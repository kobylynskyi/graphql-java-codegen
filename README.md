# GraphQL Codegen #

[![CircleCI](https://img.shields.io/circleci/build/github/kobylynskyi/graphql-java-codegen)](https://circleci.com/gh/kobylynskyi/graphql-java-codegen/tree/master)
[![Codecov](https://img.shields.io/codecov/c/github/kobylynskyi/graphql-java-codegen)](https://codecov.io/gh/kobylynskyi/graphql-java-codegen)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Plugins

Please refer to:
* Gradle plugin: [graphql-java-codegen-gradle-plugin](https://github.com/kobylynskyi/graphql-java-codegen-gradle-plugin)
* Maven plugin: [grapqhl-java-codegen-maven-plugin](https://github.com/kobylynskyi/graphql-java-codegen-maven-plugin) 


## Supported Options

| Key                       | Data Type          | Default value                             | Description |
| ------------------------- | ------------------ | ----------------------------------------- | ----------- |
| graphqlSchemaPaths        | List(String)       | None                                      | GraphQL schema locations. You can supply multiple paths to GraphQL schemas. |
| packageName               | String             | Empty                                     | Java package for generated classes. |
| outputDir                 | String             | None                                      | The output target directory into which code will be generated. |
| apiPackage                | String             | Empty                                     | Java package for generated api classes (Query, Mutation, Subscription). |
| modelPackage              | String             | Empty                                     | Java package for generated model classes (type, input, interface, enum, union). |
| generateApis              | Boolean            | True                                      | Specifies whether api classes should be generated as well as model classes. |
| customTypesMapping        | Map(String,String) | Empty                                     | Can be used to supply custom mappings for scalars. <br/> Supports:<br/> * Map of (GraphqlObjectName.fieldName) to (JavaType) <br/> * Map of (GraphqlType) to (JavaType) |
| customAnnotationsMapping  | Map(String,String) | Empty                                     | Can be used to supply custom annotations (serializers) for scalars. <br/> Supports:<br/> * Map of (GraphqlObjectName.fieldName) to (JavaType) <br/> * Map of (GraphqlType) to (JavaType) |
| modelValidationAnnotation | String             | @javax.validation.<br>constraints.NotNull | Annotation for mandatory (NonNull) fields. Can be null/empty. |
| modelNamePrefix           | String             | Empty                                     | Sets the prefix for GraphQL model classes (type, input, interface, enum, union). |
| modelNameSuffix           | String             | Empty                                     | Sets the suffix for GraphQL model classes (type, input, interface, enum, union). |
| subscriptionReturnType    | String             | Empty                                     | Return type for subscription methods. For example: `org.reactivestreams.Publisher`, `io.reactivex.Observable`, etc. |
| generateEqualsAndHashCode | Boolean            | False                                     | Specifies whether generated model classes should have equals and hashCode methods defined. |
| generateToString          | Boolean            | False                                     | Specifies whether generated model classes should have toString method defined. |
| jsonConfigurationFile     | String             | Empty                                     | Path to an external mapping configuration. |

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

## Inspired by
[swagger-codegen](https://github.com/swagger-api/swagger-codegen)

