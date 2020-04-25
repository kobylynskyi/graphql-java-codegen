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
addSbtPlugin("io.github.kobylynskyi" % "sbt-graphql-java-codegen" % "1.6.0-NOT-YET-RELEASED")
```

### Plugin Options

| Option                                          | Data Type          | Default value                                 | Description |
| :---------------------------------------------: | :----------------: | :-------------------------------------------: | ----------- |
| `graphqlSchemaPaths`                            | List(String)       | (falls back to `graphqlSchemas`)              | GraphQL schema locations. You can supply multiple paths to GraphQL schemas. To include many schemas from a folder hierarchy, use the `graphqlSchemas` block instead. |
| `graphqlApiPackageName`                         | String             | Empty                                         | Java package for generated api classes (Query, Mutation, Subscription). |
| `graphqlModelPackageName`                       | String             | Empty                                         | Java package for generated model classes (type, input, interface, enum, union). |
| `graphqlModelNamePrefix`                        | String             | Empty                                         | Sets the prefix for GraphQL model classes (type, input, interface, enum, union). |
| `graphqlModelNameSuffix`                        | String             | Empty                                         | Sets the suffix for GraphQL model classes (type, input, interface, enum, union). |

### Different configurations for graphql schemas

Currently, if you want to have different configuration for different `.graphqls` files (e.g.: different javaPackage, outputDir, etc.), then you will need to create an SBT sub-project for each one.
