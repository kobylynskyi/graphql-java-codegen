# GraphQL Codegen #

[![Build Status](https://travis-ci.com/kobylynskyi/graphql-java-codegen.svg?branch=master)](https://travis-ci.com/kobylynskyi/graphql-java-codegen)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Plugins

Please refer to:
* Gradle plugin: [grapqhl-java-codegen-gradle-plugin](https://github.com/kobylynskyi/graphql-java-codegen-gradle-plugin)
* Maven plugin: [grapqhl-java-codegen-maven-plugin](https://github.com/kobylynskyi/grapqhl-java-codegen-maven-plugin) 



## Supported Options

| Key                     | Data Type          | Default value | Description |
| ----------------------- | ------------------ | ------------- | ----------- |
| graphqlSchemaPaths      | List(String)       | None          | GraphQL schema locations. You can supply multiple paths to GraphQL schemas. |
| packageName             | String             | Empty         | Java package for generated classes. |
| outputDir               | String             | None          | The output target directory into which code will be generated. |
| customTypesMapping      | Map(String,String) | Empty         | Can be used to supply custom mappings for scalars. <br/> Supports:<br/> * Map of (GraphqlObjectName.fieldName) to (JavaType) <br/> * Map of (GraphqlType) to (JavaType) |
| modelNamePrefix         | String             | Empty         | Sets the prefix for GraphQL model classes (type, input, interface, enum, union). |
| modelNameSuffix         | String             | Empty         | Sets the suffix for GraphQL model classes (type, input, interface, enum, union). |
| apiPackage              | String             | Empty         | Java package for generated api classes (Query, Mutation, Subscription). |
| modelPackage            | String             | Empty         | Java package for generated model classes (type, input, interface, enum, union). |


### Inspired by
[swagger-codegen](https://github.com/swagger-api/swagger-codegen)

