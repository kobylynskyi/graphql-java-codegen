# GraphQL Codegen SBT plugin #

This is a sbt plugin for https://github.com/kobylynskyi/graphql-java-codegen

Server example at https://github.com/jxnu-liguobin/springboot-examples/tree/master/graphql-complete (do not use plugin, only a normal graphql server )


![Build](https://github.com/kobylynskyi/graphql-java-codegen/workflows/Build/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.jxnu-liguobin/graphql-codegen-sbt-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.jxnu-liguobin/graphql-codegen-sbt-plugin)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)


### Plugin Setup


```scala
// plugins.sbt
addSbtPlugin("io.github.jxnu-liguobin" % "graphql-codegen-sbt-plugin" % "<version>")

//since graphql-java-codegen V2.2.1
```

### Config


```scala
// build.sbt
enablePlugins(GraphQLCodegenPlugin)

// GraphQLCodegenPluginDependencies will import java validation-api
// also you can override them version by  javaxValidationApiVersion
// and graphql-java-codegen will be use
GraphQLCodegenPluginDependencies

libraryDependencies ++= Seq(<yours>)

graphqlSchemaPaths := List("src/main/resources/schema.graphqls")
// all key is Option, because key impl by macro, nested call will error
modelPackageName := Some("io.github.dreamylost.model")
apiPackageName := Some("io.github.dreamylost.api")
generateClient := Some(true)
generateApis := Some(true)

customTypesMapping := {
  val mapping = new util.HashMap[String, String]
  mapping.put("Email", "io.github.dreamylost.scalar.EmailScalar")
  mapping
}
```

### Codegen Options


SBT task 

1. graphqlSchemaValidate          
    - use validate at terminal by user, can get args from terminal, such as `graphqlSchemaValidate  src/main/resources/schema.graphqls`, args split with space
2. graphqlCodegen                 
    - generate java code from graphql schema
3. graphqlCodegenValidate         
    - use validate schemas that config in build.sbt key: `graphqlSchemaPaths`


### Plugin Options


Please refer to [Codegen Options](../../docs/codegen-options.md)

> in sbt plugin option `packageName` was rename to `generatePackageName`


