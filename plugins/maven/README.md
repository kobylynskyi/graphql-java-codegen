# GraphQL Codegen Maven plugin #

![Build](https://github.com/kobylynskyi/graphql-java-codegen/workflows/Build/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.kobylynskyi/graphql-codegen-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.kobylynskyi/graphql-codegen-maven-plugin)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

* [Plugin Setup and Configuration](#plugin-setup-and-configuration)
* [Plugin Options](#plugin-options)
* [Examples](#examples)
  * [GraphQL **server** code generation](#graphql-server-code-generation)
  * [GraphQL **client** code generation](#graphql-client-code-generation)
* [Different configurations for graphql schemas](#different-configurations-for-graphql-schemas)


### Plugin Setup and Configuration

```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>io.github.kobylynskyi</groupId>
            <artifactId>graphql-codegen-maven-plugin</artifactId>
            <version>4.1.3</version>
            <executions>
                <execution>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                    <configuration>
                        <!-- all config options: 
                        https://github.com/kobylynskyi/graphql-java-codegen/blob/master/docs/codegen-options.md
                        -->
                        <graphqlSchemas>
                            <includePattern>schema\.graphqls</includePattern>
                        </graphqlSchemas>
                        <outputDir>${project.build.directory}/generated-sources/graphql</outputDir>
                        <packageName>io.github.kobylynskyi.bikeshop.graphql.model</packageName>
                        <customTypesMapping>
                            <DateTime>java.util.Date</DateTime>
                            <Price.amount>java.math.BigDecimal</Price.amount>
                        </customTypesMapping>
                        <customAnnotationsMapping>
                            <EpochMillis>
                                <annotation>com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.EpochMillisScalarDeserializer.class)</annotation>
                            </EpochMillis>
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

Please refer to [Codegen Options](../../docs/codegen-options.md)

### Examples

#### GraphQL **server** code generation

[example-server](example-server):
  * [Plugin configuration in pom.xml](example-server/pom.xml)
  * [GraphQL Resolver classes that implement generated interfaces](example-server/src/main/java/io/github/kobylynskyi/product/graphql/resolvers)

#### GraphQL **client** code generation

[example-client](example-client):
  * [Plugin configuration in pom.xml](example-client/pom.xml)
  * [Building GraphQL request and parsing response using Spring RestTemplate](example-client/src/main/java/io/github/kobylynskyi/order/external/product/ProductServiceGraphQLClient.java)
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

