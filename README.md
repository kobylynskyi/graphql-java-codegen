# GraphQL Codegen #

![Build](https://github.com/kobylynskyi/graphql-java-codegen/workflows/Build/badge.svg)
[![Discussions](https://img.shields.io/badge/github-discussions-green)](https://github.com/kobylynskyi/graphql-java-codegen/discussions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=kobylynskyi_graphql-java-codegen&metric=alert_status)](https://sonarcloud.io/dashboard?id=kobylynskyi_graphql-java-codegen) 
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=kobylynskyi_graphql-java-codegen&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=kobylynskyi_graphql-java-codegen)
[![SonarCloud Bugs](https://sonarcloud.io/api/project_badges/measure?project=kobylynskyi_graphql-java-codegen&metric=bugs)](https://sonarcloud.io/component_measures/metric/reliability_rating/list?id=kobylynskyi_graphql-java-codegen)
[![SonarCloud Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=kobylynskyi_graphql-java-codegen&metric=vulnerabilities)](https://sonarcloud.io/component_measures/metric/security_rating/list?id=kobylynskyi_graphql-java-codegen)


GraphQL Java Codegen makes it easy to make your Java application to follow a schema-first approach whether it is a server or client application.

Following classes can be generated based on your GraphQL schema:
* Interfaces for GraphQL queries, mutations and subscriptions.
* Interfaces for GraphQL unions.
* POJO classes for GraphQL types and inputs.
* Enum classes for GraphQL enums.
* Interfaces for GraphQL type fields (e.g. for parametrized fields) aka "Resolvers".
* Client Request classes for GraphQL queries, mutations and subscriptions.


## Features
* Generate classes in Java, Kotlin or Scala.
* Recursive schemas lookup by file name pattern.
* Generate code based on GraphQL schema or GraphQL Query Introspection Result.
* Generate POJOs with or without: Builder pattern, immutable fields, `toString()`, `equals()` and `hashCode()`, etc.
* Flexible API interfaces naming conventions (based on schema file name, folder name, etc.)
* Custom java package names for model and API classes.
* Custom prefix/suffix for model, API, type resolver, request, response classes.
* Custom annotations for generated classes (e.g.: validation annotations for generated model classes or specific type fields, annotations for GraphQL directives, etc.)
* Relay support.
* Ability to define codegen configuration via external json file.


**For the full list of codegen configs please refer to: [Codegen Options](docs/codegen-options.md)**


## Supported plugins

* Gradle plugin: [graphql-java-codegen-gradle-plugin](plugins/gradle)
* Maven plugin: [grapqhl-java-codegen-maven-plugin](plugins/maven) 
* SBT plugin: [grapqhl-java-codegen-sbt-plugin](plugins/sbt) 


## Contributing

Please see [CONTRIBUTING.md](CONTRIBUTING.md).


## Inspired by

[swagger-codegen](https://github.com/swagger-api/swagger-codegen)

