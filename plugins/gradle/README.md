# GraphQL Codegen Gradle plugin #

[![CircleCI](https://img.shields.io/circleci/build/github/kobylynskyi/graphql-java-codegen)](https://circleci.com/gh/kobylynskyi/graphql-java-codegen/tree/master)
[![Gradle Plugins](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/io/github/kobylynskyi/graphql-codegen-gradle-plugin/maven-metadata.xml.svg?label=gradle)](https://plugins.gradle.org/plugin/io.github.kobylynskyi.graphql.codegen)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This document describes the gradle plugin for graphql-java-codegen.

### Description

This Gradle plugin is able to generate the following classes based on your GraphQL schema:
* Interfaces for GraphQL queries, mutations and subscriptions
* Interfaces for GraphQL unions
* POJO classes for GraphQL types/inputs
* Enum classes for GraphQL enums
* Interface Resolvers for GraphQL type fields
* Client Request classes for GraphQL queries, mutations and subscriptions


### Plugin Setup

```groovy
plugins {
  id "io.github.kobylynskyi.graphql.codegen" version "1.6.0"
}
```

Using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):

```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "io.github.kobylynskyi.graphql.codegen:graphql-codegen-gradle-plugin:1.6.0"
  }
}

apply plugin: "io.github.kobylynskyi.graphql.codegen"
```

### Plugin Options

| Option                                          | Data Type          | Default value                                 | Description |
| :---------------------------------------------: | :----------------: | :-------------------------------------------: | ----------- |
| `graphqlSchemaPaths`                            | List(String)       | (falls back to `graphqlSchemas`)              | GraphQL schema locations. You can supply multiple paths to GraphQL schemas. To include many schemas from a folder hierarchy, use the `graphqlSchemas` block instead. |
| `graphqlSchemas`                                | *See table below*  | All `.graphqls`/`.graphql` files in resources | Block to define the input GraphQL schemas, when exact paths are too cumbersome. See table below for a list of options. |
| `outputDir`                                     | String             | None                                          | The output target directory into which code will be generated. |
| `jsonConfigurationFile`                         | String             | Empty                                         | Path to an external mapping configuration. |
| `packageName`                                   | String             | Empty                                         | Java package for generated classes. |
| `apiPackageName`                                | String             | Empty                                         | Java package for generated api classes (Query, Mutation, Subscription). |
| `modelPackageName                               | String             | Empty                                         | Java package for generated model classes (type, input, interface, enum, union). |
| `generateBuilder`                               | Boolean            | True                                          | Specifies whether generated model classes should have builder. |
| `generateApis`                                  | Boolean            | True                                          | Specifies whether api classes should be generated as well as model classes. |
| `generateAsyncApi`                              | Boolean            | False                                         | If true, then wrap type into `java.util.concurrent.CompletableFuture` or `subscriptionReturnType` |
| `generateDataFetchingEnvironmentArgumentInApis` | Boolean            | False                                         | If true, then `graphql.schema.DataFetchingEnvironment env` will be added as a last argument to all methods of root type resolvers and field resolvers. |
| `generateEqualsAndHashCode`                     | Boolean            | False                                         | Specifies whether generated model classes should have equals and hashCode methods defined. |
| `generateToString`                              | Boolean            | False                                         | Specifies whether generated model classes should have toString method defined. |
| `modelNamePrefix`                               | String             | Empty                                         | Sets the prefix for GraphQL model classes (type, input, interface, enum, union). |
| `modelNameSuffix`                               | String             | Empty                                         | Sets the suffix for GraphQL model classes (type, input, interface, enum, union). |
| `modelValidationAnnotation`                     | String             | @javax.validation.<br>constraints.NotNull     | Annotation for mandatory (NonNull) fields. Can be null/empty. |
| `customTypesMapping`                            | Map(String,String) | Empty                                         | Can be used to supply custom mappings for scalars. <br/> Supports:<br/> * Map of (GraphqlObjectName.fieldName) to (JavaType) <br/> * Map of (GraphqlType) to (JavaType) |
| `customAnnotationsMapping`                      | Map(String,String) | Empty                                         | Can be used to supply custom annotations (serializers) for scalars. <br/> Supports:<br/> * Map of (GraphqlObjectName.fieldName) to (JavaAnnotation) <br/> * Map of (GraphqlType) to (JavaAnnotation) |
| `fieldsWithResolvers`                           | Set(String)        | Empty                                         | Fields that require Resolvers should be defined here in format: `TypeName.fieldName` or `TypeName`. |
| `fieldsWithoutResolvers`                        | Set(String)        | Empty                                         | Fields that DO NOT require Resolvers should be defined here in format: `TypeName.fieldName` or `TypeName`. Can be used in conjunction with `generateExtensionFieldsResolvers` option. |
| `generateParameterizedFieldsResolvers`          | Boolean            | True                                          | If true, then generate separate `Resolver` interface for parametrized fields. If false, then add field to the type definition and ignore field parameters. |
| `generateExtensionFieldsResolvers`              | Boolean            | False                                         | Specifies whether all fields in extensions (<code>extend type</code> and <code>extend interface</code>) should be present in Resolver interface instead of the type class itself. |
| `subscriptionReturnType`                        | String             | Empty                                         | Return type for subscription methods. For example: `org.reactivestreams.Publisher`, `io.reactivex.Observable`, etc. |
| `generateRequests`                              | Boolean            | False                                         | Specifies whether client-side classes should be generated for each query, mutation and subscription. This includes: `Request` class (contains input data) and `ResponseProjection` class (contains response fields). |
| `requestSuffix`                                 | String             | Request                                       | Sets the suffix for `Request` classes. |
| `responseProjectionSuffix`                      | String             | ResponseProjection                            | Sets the suffix for `ResponseProjection` classes. |


When exact paths to GraphQL schemas are too cumbersome to provide in the `graphqlSchemaPaths`, use the `graphqlSchemas { ... }` block.
The parameters inside that block are the following:

| Key inside `graphqlSchemas` | Data Type    | Default value      | Description |
| --------------------------- | ------------ | ------------------ | ----------- |
| `rootDir`                   | String       | Main resources dir | The root directory from which to start searching for schema files. |
| `recursive`                 | Boolean      | `true`             | Whether to recursively look into sub directories. |
| `includePattern`            | String       | `.*\.graphqls?`    | A Java regex that file names must match to be included. It should be a regex as defined by the [Pattern](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html) JDK class. It will be used to match only the file name without path. |
| `excludedFiles`             | Set<String>  | (empty set)        | A set of files to exclude, even if they match the include pattern. These paths should be either absolute or relative to the provided `rootDir`. |



### Sample Plugin Configuration

#### build.gradle:

```groovy
graphqlCodegen {
    graphqlSchemaPaths = [
        "$projectDir/src/main/resources/schema.graphqls".toString()
    ]
    outputDir = "$buildDir/generated/graphql"
    packageName = "com.example.graphql.model"
    customTypesMapping = [
        DateTime: "org.joda.time.DateTime",
        Price.amount: "java.math.BigDecimal"
    ]
    customAnnotationsMapping = [
        DateTime: "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.EpochMillisScalarDeserializer.class"
    ]
    modelNameSuffix = "TO"
}

// Automatically generate GraphQL code on project build:
compileJava.dependsOn 'graphqlCodegen'

// Add generated sources to your project source sets:
sourceSets.main.java.srcDir "$buildDir/generated"
```

#### build.gradle.kts:

```groovy
tasks.named<GraphqlCodegenGradleTask>("graphqlCodegen") {
    graphqlSchemaPaths = listOf("$projectDir/src/main/resources/graphql/schema.graphqls".toString())
    outputDir = File("$buildDir/generated/graphql")
    packageName = "com.example.graphql.model"
    customTypesMapping = mutableMapOf(Pair("EpochMillis", "java.time.LocalDateTime"))
    customAnnotationsMapping = mutableMapOf(Pair("EpochMillis", "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.EpochMillisScalarDeserializer.class"))
}

// Automatically generate GraphQL code on project build:
sourceSets {
    getByName("main").java.srcDirs("$buildDir/generated/graphql")
}

// Add generated sources to your project source sets:
val check: DefaultTask by tasks
val graphqlCodegen: DefaultTask by tasks
check.dependsOn(graphqlCodegen)    
```


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


### Examples

* GraphQL **server** code generation: [example-server](example-server)
  * [Plugin configuration in build.gradle](example-server/build.gradle)
  * [GraphQL Resolver classes that implement generated interfaces](example-server/src/main/java/io/github/kobylynskyi/product/graphql/resolvers)
* GraphQL **client** code generation: [example-client](example-client)
  * [Plugin configuration in build.gradle](example-client/build.gradle)
  * [Building GraphQL request and parsing response using Spring RestTemplate](example-client/src/main/java/io/github/kobylynskyi/order/external/ProductServiceGraphQLClient.java)
  * [Building GraphQL request and parsing response using RestAssured](example-client/src/test/java/io/github/kobylynskyi/order/service/CreateProductIntegrationTest.java)


### Different configurations for graphql schemas

If you want to have different configuration for different `.graphqls` files (e.g.: different javaPackage, outputDir, etc.), then you will need to create separate gradle tasks for each set of schemas. E.g.:

```groovy
task graphqlCodegenService1(type: GraphqlCodegenGradleTask) {
    graphqlSchemaPaths = ["$projectDir/src/main/resources/schema1.graphqls".toString()]
    outputDir = new File("$buildDir/generated/example1")
}

task graphqlCodegenService2(type: GraphqlCodegenGradleTask) {
    graphqlSchemaPaths = ["$projectDir/src/main/resources/schema2.graphqls".toString()]
    outputDir = new File("$buildDir/generated/example2")
}
```

Later on you can call each task separately or together:

* `gradle clean graphqlCodegenService1 build`
* `gradle clean graphqlCodegenService2 build`
* `gradle clean graphqlCodegenService1 graphqlCodegenService2 build`


### Convert generated Java classes to Kotlin classes

1. Navigate in IntelliJ IDEA to the `./build/generated/graphql/` folder and press `Cmd+Alt+Shift+K`
2. Access generated classes as normal Kotlin classes.


### Inspired by

[swagger-codegen](https://github.com/swagger-api/swagger-codegen)

