# GraphQL Codegen Maven plugin #

[![CircleCI](https://img.shields.io/circleci/build/github/kobylynskyi/graphql-java-codegen)](https://circleci.com/gh/kobylynskyi/graphql-java-codegen/tree/master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.kobylynskyi/graphql-codegen-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.kobylynskyi/graphql-codegen-maven-plugin)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This document describes the maven plugin for graphql-java-codegen.

### Description

This Maven plugin is able to generate the following classes based on your GraphQL schema:
* Interfaces for GraphQL queries, mutations and subscriptions
* Interfaces for GraphQL unions
* POJO classes for GraphQL types
* Enum classes for each GraphQL enum
* Interface Resolvers for GraphQL type fields 

### Plugin Setup and Configuration

```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>io.github.kobylynskyi</groupId>
            <artifactId>graphql-codegen-maven-plugin</artifactId>
            <version>1.4.2</version>
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


#### Plugin Options

| Key                                  | Data Type          | Default value                             | Description |
| ------------------------------------ | ------------------ | ----------------------------------------- | ----------- |
| graphqlSchemaPaths                   | List(String)       | None                                      | GraphQL schema locations. You can supply multiple paths to GraphQL schemas. |
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
| fieldsResolvers                      | Set(String)        | Empty                                     | Fields that require Resolvers should be defined here in format: `TypeName.fieldName`. |
| jsonConfigurationFile                | String             | Empty                                     | Path to an external mapping configuration. |


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

### Example

[example project](example)


### Inspired by
[swagger-codegen](https://github.com/swagger-api/swagger-codegen)

