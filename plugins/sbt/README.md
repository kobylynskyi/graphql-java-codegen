# SBT GraphQL Java codegen plugin #

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This document describes the SBT plugin for graphql-java-codegen.

This plugin does not yet have all the features of the [Maven plugin](../maven/README.md) and [Gradle plugin](../gradle/README.md), but please feel free to send a PR to add missing options.

### Description

The GraphQL Java codegen library used by this plugin is able to generate the following classes based on your GraphQL schema:
* Interfaces for GraphQL queries, mutations and subscriptions
* Interfaces for GraphQL unions
* POJO classes for GraphQL types/inputs
* Enum classes for GraphQL enums
* Interface Resolvers for GraphQL type fields
* Client Request classes for GraphQL queries, mutations and subscriptions


### Plugin Setup and Configuration

```
addSbtPlugin("io.github.kobylynskyi" % "sbt-graphql-java-codegen" % "1.8.2-NOT-YET-RELEASED")
```

### Plugin Options

| Option                                          | Data Type          | Default value                                 | Description |
| :---------------------------------------------: | :----------------: | :-------------------------------------------: | ----------- |
| `graphqlSchemaPaths`                            | List(String)       | (falls back to `graphqlSchemas`)              | GraphQL schema locations. You can supply multiple paths to GraphQL schemas. To include many schemas from a folder hierarchy, use the `graphqlSchemas` block instead. |
| `graphqlApiPackageName`                         | String             | Empty                                         | Java package for generated api classes (Query, Mutation, Subscription). |
| `graphqlModelPackageName`                       | String             | Empty                                         | Java package for generated model classes (type, input, interface, enum, union). |
| `graphqlGenerateBuilder`                        | Boolean            | True                                          | Specifies whether generated model classes should have builder. |
| `graphqlGenerateApis`                           | Boolean            | True                                          | Specifies whether api classes should be generated as well as model classes. |
| `graphqlGenerateAsyncApi`                       | Boolean            | False                                         | If true, then wrap type into `java.util.concurrent.CompletableFuture` or `subscriptionReturnType` |
| `graphqlGenerateDataFetchingEnvArgInApis`       | Boolean            | False                                         | If true, then `graphql.schema.DataFetchingEnvironment env` will be added as a last argument to all methods of root type resolvers and field resolvers. |
| `graphqlGenerateEqualsAndHashCode`              | Boolean            | False                                         | Specifies whether generated model classes should have equals and hashCode methods defined. |
| `graphqlGenerateToString`                       | Boolean            | False                                         | Specifies whether generated model classes should have toString method defined. |
| `graphqlModelNamePrefix`                        | String             | Empty                                         | Sets the prefix for GraphQL model classes (type, input, interface, enum, union). |
| `graphqlModelNameSuffix`                        | String             | Empty                                         | Sets the suffix for GraphQL model classes (type, input, interface, enum, union). |
| `graphqlModelValidationAnnotation`              | String             | @javax.validation.<br>constraints.NotNull     | Annotation for mandatory (NonNull) fields. Can be None/empty. |
| `graphqlGenerateParameterizedFieldsResolvers`   | Boolean            | True                                          | If true, then generate separate `Resolver` interface for parametrized fields. If false, then add field to the type definition and ignore field parameters. |
| `graphqlGenerateExtensionFieldsResolvers`       | Boolean            | False                                         | Specifies whether all fields in extensions (<code>extend type</code> and <code>extend interface</code>) should be present in Resolver interface instead of the type class itself. |
| `graphqlGenerateRequests`                       | Boolean            | False                                         | Specifies whether client-side classes should be generated for each query, mutation and subscription. This includes: `Request` class (contains input data) and `ResponseProjection` class (contains response fields). |
| `graphqlRequestSuffix`                          | String             | Request                                       | Sets the suffix for `Request` classes. |
| `graphqlResponseProjectionSuffix`               | String             | ResponseProjection                            | Sets the suffix for `ResponseProjection` classes. |


### Different configurations for graphql schemas

Currently, if you want to have different configuration for different `.graphqls` files (e.g.: different javaPackage, outputDir, etc.), then you will need to create an SBT sub-project for each one.
