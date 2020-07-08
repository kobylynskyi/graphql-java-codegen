# GraphQL Codegen #

![Build](https://github.com/kobylynskyi/graphql-java-codegen/workflows/Build/badge.svg)
[![Gitter](https://badges.gitter.im/graphql-java-codegen/community.svg)](https://gitter.im/graphql-java-codegen/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=kobylynskyi_graphql-java-codegen&metric=alert_status)](https://sonarcloud.io/dashboard?id=kobylynskyi_graphql-java-codegen) 
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=kobylynskyi_graphql-java-codegen&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=kobylynskyi_graphql-java-codegen)
[![SonarCloud Bugs](https://sonarcloud.io/api/project_badges/measure?project=kobylynskyi_graphql-java-codegen&metric=bugs)](https://sonarcloud.io/component_measures/metric/reliability_rating/list?id=kobylynskyi_graphql-java-codegen)
[![SonarCloud Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=kobylynskyi_graphql-java-codegen&metric=vulnerabilities)](https://sonarcloud.io/component_measures/metric/security_rating/list?id=kobylynskyi_graphql-java-codegen)



GraphQL Java Codegen makes it easy to make your Java application to follow a schema-first approach.

Following classes can be generated based on your GraphQL schema:
* Interfaces for GraphQL queries, mutations and subscriptions.
* Interfaces for GraphQL unions.
* POJO classes for GraphQL types and inputs.
* Enum classes for GraphQL enums.
* Interface Resolvers for GraphQL type fields (e.g. for parametrized fields).
* Client Request classes for GraphQL queries, mutations and subscriptions.


## Supported plugins

* Gradle plugin: [graphql-java-codegen-gradle-plugin](plugins/gradle)
* Maven plugin: [grapqhl-java-codegen-maven-plugin](plugins/maven) 


## Contributing

Please see [CONTRIBUTING.md](CONTRIBUTING.md).


## Inspired by
[swagger-codegen](https://github.com/swagger-api/swagger-codegen)

