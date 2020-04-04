# GraphQL Codegen Gradle plugin #

[![CircleCI](https://img.shields.io/circleci/build/github/kobylynskyi/graphql-java-codegen)](https://circleci.com/gh/kobylynskyi/graphql-java-codegen/tree/master)
[![Gradle Plugins](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/io/github/kobylynskyi/graphql/codegen/graphql-codegen-gradle-plugin/maven-metadata.xml.svg?label=gradle)](https://plugins.gradle.org/plugin/io.github.kobylynskyi.graphql.codegen)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This document describes the gradle plugin for graphql-java-codegen.

### Description

This Gradle plugin is able to generate the following classes based on your GraphQL schema:
* Interfaces for GraphQL queries, mutations and subscriptions
* Interfaces for GraphQL unions
* POJO classes for GraphQL types
* Enum classes for each GraphQL enum
* Interface Resolvers for GraphQL type fields 

### Plugin Setup

```groovy
plugins {
  id "io.github.kobylynskyi.graphql.codegen" version "1.5.0"
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
    classpath "io.github.kobylynskyi.graphql.codegen:graphql-codegen-gradle-plugin:1.5.0"
  }
}

apply plugin: "io.github.kobylynskyi.graphql.codegen"
```

### Plugin Configuration

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

#### Plugin Options

| Key                                  | Data Type          | Default value                             | Description |
| ------------------------------------ | ------------------ | ----------------------------------------- | ----------- |
| graphqlSchemaPaths                   | List(String)       | (falls back to `graphqlSchemas`)          | GraphQL schema locations. You can supply multiple paths to GraphQL schemas. To include many schemas from a folder hierarchy, use the `graphqlSchemas` block instead. |
| graphqlSchemas                       | (see table below)  | All `.graphqls` files in resources        | Block to define the input GraphQL schemas, when exact paths are too cumbersome. See table below for a list of options. |
| packageName                          | String             | Empty                                     | Java package for generated classes. |
| outputDir                            | String             | None                                      | The output target directory into which code will be generated. |
| apiPackage                           | String             | Empty                                     | Java package for generated api classes (Query, Mutation, Subscription). |
| modelPackage                         | String             | Empty                                     | Java package for generated model classes (type, input, interface, enum, union). |
| generateApis                         | Boolean            | True                                      | Specifies whether api classes should be generated as well as model classes. |
| customTypesMapping                   | Map(String,String) | Empty                                     | Can be used to supply custom mappings for scalars. <br/> Supports:<br/> * Map of (GraphqlObjectName.fieldName) to (JavaType) <br/> * Map of (GraphqlType) to (JavaType) |
| customAnnotationsMapping             | Map(String,String) | Empty                                     | Can be used to supply custom annotations (serializers) for scalars. <br/> Supports:<br/> * Map of (GraphqlObjectName.fieldName) to (JavaType) <br/> * Map of (GraphqlType) to (JavaType) |
| modelValidationAnnotation            | String             | @javax.validation.<br>constraints.NotNull | Annotation for mandatory (NonNull) fields. Can be null/empty. |
| modelNamePrefix                      | String             | Empty                                     | Sets the prefix for GraphQL model classes (type, input, interface, enum, union). |
| modelNameSuffix                      | String             | Empty                                     | Sets the suffix for GraphQL model classes (type, input, interface, enum, union). |
| subscriptionReturnType               | String             | Empty                                     | Return type for subscription methods. For example: `org.reactivestreams.Publisher`, `io.reactivex.Observable`, etc. |
| generateEqualsAndHashCode            | Boolean            | False                                     | Specifies whether generated model classes should have equals and hashCode methods defined. |
| generateToString                     | Boolean            | False                                     | Specifies whether generated model classes should have toString method defined. |
| generateAsyncApi                     | Boolean            | False                                     | If true, then wrap type into `java.util.concurrent.CompletableFuture` or `subscriptionReturnType` |
| generateParameterizedFieldsResolvers | Boolean            | True                                      | If true, then generate separate `Resolver` interface for parametrized fields. If false, then add field to the type definition and ignore field parameters. |
| fieldsWithResolvers                  | Set(String)        | Empty                                     | Fields that require Resolvers should be defined here in format: `TypeName.fieldName`. |
| jsonConfigurationFile                | String             | Empty                                     | Path to an external mapping configuration. |

When exact paths to GraphQL schemas are too cumbersome to provide in the `graphqlSchemaPaths`, use the `graphqlSchemas { ... }` block.
The parameters inside that block are the following:

| Key               | Data Type    | Default value      | Description |
| ----------------- | ------------ | ------------------ | ----------- |
| `rootDir`         | String       | Main resources dir | The root directory from which to start searching for schema files. |
| `recursive`       | Boolean      | `true`             | Whether to recursively look into sub directories. |
| `includePattern`  | String       | `.*\.graphqls`     | A Java regex that file names must match to be included. It should be a regex as defined by the [Pattern](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html) JDK class. It will be used to match only the file name without path. |
| `excludedFiles`   | Set<String>  | (empty set)        | A set of files to exclude, even if they match the include pattern. These paths should be either absolute or relative to the provided `rootDir`. |

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

### Different configuration for each graphql schema

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

* `gradlew clean graphqlCodegenService1 build`
* `gradlew clean graphqlCodegenService2 build`
* `gradlew clean graphqlCodegenService1 graphqlCodegenService2 build`


### Convert generated Java classes to Kotlin classes

Navigate in IntelijIdea to the `./build/generated/graphql/` folder and press `Cmd+Alt+Shift+K`
Access to classes from your code as normal Kotlin classes.


### Example

[graphql-codegen-gradle-plugin-example](graphql-codegen-gradle-plugin-example)


### Inspired by

[swagger-codegen](https://github.com/swagger-api/swagger-codegen)

