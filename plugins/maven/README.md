# GraphQL Codegen Maven plugin #

![Build](https://github.com/kobylynskyi/graphql-java-codegen/workflows/Build/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.kobylynskyi/graphql-codegen-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.kobylynskyi/graphql-codegen-maven-plugin)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This document describes the maven plugin for graphql-java-codegen.

### Description

This Maven plugin is able to generate the following classes based on your GraphQL schema:
* Interfaces for GraphQL queries, mutations and subscriptions
* Interfaces for GraphQL unions
* POJO classes for GraphQL types/inputs
* Enum classes for GraphQL enums
* Interface Resolvers for GraphQL type fields
* Client Request classes for GraphQL queries, mutations and subscriptions


### Plugin Setup and Configuration

```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>io.github.kobylynskyi</groupId>
            <artifactId>graphql-codegen-maven-plugin</artifactId>
            <version>1.7.1</version>
            <executions>
                <execution>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                    <configuration>
                        <graphqlSchemaPaths>${project.basedir}/src/main/resources/schema.graphqls</graphqlSchemaPaths>
                        <outputDir>${project.build.directory}/generated-sources/graphql</outputDir>
                        <packageName>io.github.kobylynskyi.bikeshop.graphql.model</packageName>
                        <customTypesMapping>
                            <DateTime>java.util.Date</DateTime>
                            <Price.amount>java.math.BigDecimal</Price.amount>
                        </customTypesMapping>
                        <customAnnotationsMapping>
                            <EpochMillis>com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.EpochMillisScalarDeserializer.class</EpochMillis>
                        </customAnnotationsMapping>
                        <modelNameSuffix>TO</modelNameSuffix>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        ...
    </plugins>
</build>
```

You can run the plugin manually with `mvn generate-sources`. It will be run automatically as part of the Maven lifecycle when compiling your code

### Plugin Options

| Option                                          | Data Type          | Default value                                 | Description |
| :---------------------------------------------: | :----------------: | :-------------------------------------------: | ----------- |
| `graphqlSchemaPaths`                            | List(String)       | (falls back to `graphqlSchemas`)              | GraphQL schema locations. You can supply multiple paths to GraphQL schemas. To include many schemas from a folder hierarchy, use the `graphqlSchemas` block instead. |
| `graphqlSchemas`                                | *See table below*  | All `.graphqls`/`.graphql` files in resources | Block to define the input GraphQL schemas, when exact paths are too cumbersome. See table below for a list of options. |
| `outputDir`                                     | String             | None                                          | The output target directory into which code will be generated. |
| `jsonConfigurationFile`                         | String             | Empty                                         | Path to an external mapping configuration. |
| `packageName`                                   | String             | Empty                                         | Java package for generated classes. |
| `apiPackageName`                                | String             | Empty                                         | Java package for generated api classes (Query, Mutation, Subscription). |
| `modelPackageName`                              | String             | Empty                                         | Java package for generated model classes (type, input, interface, enum, union). |
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


When exact paths to GraphQL schemas are too cumbersome to provide in the `graphqlSchemaPaths`, use the `<graphqlSchemas></graphqlSchemas>` block.
The parameters inside that block are the following:

| Key inside `graphqlSchemas` | Data Type    | Default value      | Description |
| --------------------------- | ------------ | ------------------ | ----------- |
| `rootDir`                   | String       | Main resources dir | The root directory from which to start searching for schema files. |
| `recursive`                 | Boolean      | `true`             | Whether to recursively look into sub directories. |
| `includePattern`            | String       | `.*\.graphqls?`    | A Java regex that file names must match to be included. It should be a regex as defined by the [Pattern](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html) JDK class. It will be used to match only the file name without path. |
| `excludedFiles`             | Set<String>  | (empty set)        | A set of files to exclude, even if they match the include pattern. These paths should be either absolute or relative to the provided `rootDir`. |



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
  * [Plugin configuration in pom.xml](example-server/pom.xml)
  * [GraphQL Resolver classes that implement generated interfaces](example-server/src/main/java/io/github/kobylynskyi/product/graphql/resolvers)
* GraphQL **client** code generation: [example-client](example-client)
  * [Plugin configuration in pom.xml](example-client/pom.xml)
  * [Building GraphQL request and parsing response using Spring RestTemplate](example-client/src/main/java/io/github/kobylynskyi/order/external/ProductServiceGraphQLClient.java)
  * [Building GraphQL request and parsing response using RestAssured](example-client/src/test/java/io/github/kobylynskyi/order/service/CreateProductIntegrationTest.java)


### Different configurations for graphql schemas

If you want to have different configuration for different `.graphqls` files (e.g.: different javaPackage, outputDir, etc.), then you will need to define separate executions for each set of schemas. E.g.:

```xml
<executions>
    <execution>
        <id>graphqlCodegenService1</id>
        <goals>
            <goal>generate</goal>
        </goals>
        <configuration>
            <graphqlSchemaPaths>${project.basedir}/src/main/resources/schema1.graphqls</graphqlSchemaPaths>
            <outputDir>${project.build.directory}/generated-sources/graphql1</outputDir>
        </configuration>
    </execution>
    <execution>
        <id>graphqlCodegenService2</id>
        <goals>
            <goal>generate</goal>
        </goals>
        <configuration>
            <graphqlSchemaPaths>${project.basedir}/src/main/resources/schema2.graphqls</graphqlSchemaPaths>
            <outputDir>${project.build.directory}/generated-sources/graphql2</outputDir>
        </configuration>
    </execution>
</executions>
```


### Inspired by
[swagger-codegen](https://github.com/swagger-api/swagger-codegen)

