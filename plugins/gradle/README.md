# GraphQL Codegen Gradle plugin #

![Build](https://github.com/kobylynskyi/graphql-java-codegen/workflows/Build/badge.svg)
[![Gradle Plugins](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/io/github/kobylynskyi/graphql-java-codegen-gradle-plugin/maven-metadata.xml.svg?label=gradle)](https://plugins.gradle.org/plugin/io.github.kobylynskyi.graphql.codegen)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

* [Plugin Setup](#plugin-setup)
* [Plugin Options](#plugin-options)
* [Sample Plugin Configuration](#sample-plugin-configuration)
* [Examples](#examples)
  * [GraphQL **server** code generation](#graphql-server-code-generation)
  * [GraphQL **client** code generation](#graphql-client-code-generation)
* [Different configurations for graphql schemas](#different-configurations-for-graphql-schemas)
* [Convert generated Java classes to Kotlin classes](#convert-generated-java-classes-to-kotlin-classes)


### Plugin Setup

```groovy
plugins {
  id "io.github.kobylynskyi.graphql.codegen" version "4.1.3"
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
    classpath "io.github.kobylynskyi.graphql.codegen:graphql-codegen-gradle-plugin:4.1.3"
  }
}

apply plugin: "io.github.kobylynskyi.graphql.codegen"
```

### Plugin Options

Please refer to [Codegen Options](../../docs/codegen-options.md)

### Sample Plugin Configuration

#### build.gradle:

```groovy
graphqlCodegen {
    // all config options: 
    // https://github.com/kobylynskyi/graphql-java-codegen/blob/master/docs/codegen-options.md
    graphqlSchemas.includePattern = "schema\\.graphqls"
    outputDir = new File("$buildDir/generated")
    packageName = "com.example.graphql.model"
    customTypesMapping = [
        DateTime: "org.joda.time.DateTime",
        Price.amount: "java.math.BigDecimal"
    ]
    customAnnotationsMapping = [
        DateTime: ["@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.EpochMillisScalarDeserializer.class)"]
    ]
    modelNameSuffix = "TO"
}

// Automatically generate GraphQL code on project build:
compileJava.dependsOn 'graphqlCodegen'

// Add generated sources to your project source sets:
sourceSets.main.java.srcDir "$buildDir/generated"
```

You can also refer to build.gradle files in example projects: [example-client/build.gradle](example-client/build.gradle), [example-server/build.gradle](example-server/build.gradle)

#### build.gradle.kts:

```kotlin
tasks.named<GraphQLCodegenGradleTask>("graphqlCodegen") {
    // all config options: 
    // https://github.com/kobylynskyi/graphql-java-codegen/blob/master/docs/codegen-options.md
    graphqlSchemaPaths = listOf("$projectDir/src/main/resources/graphql/schema.graphqls")
    outputDir = File("$buildDir/generated")
    packageName = "com.example.graphql.model"
    customTypesMapping = mutableMapOf(Pair("EpochMillis", "java.time.LocalDateTime"))
    customAnnotationsMapping = mutableMapOf(Pair("EpochMillis", listOf("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.EpochMillisScalarDeserializer.class)")))
}

// Automatically generate GraphQL code on project build:
sourceSets {
    getByName("main").java.srcDirs("$buildDir/generated")
}

// Add generated sources to your project source sets:
tasks.named<JavaCompile>("compileJava") {
    dependsOn("graphqlCodegen")
}
```


### Examples

#### GraphQL **server** code generation

[example-server](example-server):
  * [Plugin configuration in build.gradle](example-server/build.gradle)
  * [GraphQL Resolver classes that implement generated interfaces](example-server/src/main/java/io/github/kobylynskyi/product/graphql/resolvers)

#### GraphQL **client** code generation 

[example-client](example-client):
  * [Plugin configuration in build.gradle](example-client/build.gradle)
  * [Building GraphQL request and parsing response using Spring RestTemplate](example-client/src/main/java/io/github/kobylynskyi/order/external/product/ProductServiceGraphQLClient.java)
  * [Building GraphQL request and parsing response using RestAssured](example-client/src/test/java/io/github/kobylynskyi/order/service/CreateProductIntegrationTest.java)


### Different configurations for graphql schemas

If you want to have different configuration for different `.graphqls` files (e.g.: different javaPackage, outputDir, etc.), then you will need to create separate gradle tasks for each set of schemas. E.g.:

```groovy
task graphqlCodegenService1(type: GraphqlCodegenGradleTask) {
    graphqlSchemas.includePattern = "schema1\\.graphqls"
    outputDir = new File("$buildDir/generated/example1")
}

task graphqlCodegenService2(type: GraphqlCodegenGradleTask) {
    graphqlSchemas.includePattern = "schema2\\.graphqls"
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

