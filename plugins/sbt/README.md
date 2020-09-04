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


GraphQLCodegenPluginDependencies

//default graphqlJavaCodegen is release
graphqlJavaCodegenVersion := Some("3.1.0")
graphqlSchemaPaths := List("src/main/resources/schema.graphqls")
modelPackageName := Some("io.github.dreamylost.model")
apiPackageName := Some("io.github.dreamylost.api")
generateClient := false
generateApis := true
// Scala collection cannot be used. The latter one uses the put method, which is not supported by Scala collection.
// in FB, collection is immutable
customTypesMapping := {
  val mapping = new util.HashMap[String, String]
  mapping.put("Email", "io.github.dreamylost.scalar.EmailScalar")
  //Character will conflict with java.lang.Character. maybe because Scala imports it automatically java.lang *.
  //So we use Full class name
  mapping
}

//Of course, you can also add a suffix to be different from it
modelNameSuffix := Some("DO")

customAnnotationsMapping := {
  val mapping = new util.HashMap[String, String]
  //must add this annotation
  //property is __typename and you must with __typename while invoke, like new CharacterResponseProjection().id().name().typename()
  //and in @JsonSubTypes.Type, name is __typename's value
  mapping.put("Character",
    s"""@com.fasterxml.jackson.annotation.JsonTypeInfo(use=com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY,property = "__typename")${System.lineSeparator()}@com.fasterxml.jackson.annotation.JsonSubTypes(value = {
      |        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = HumanDO.class, name = "Human"),
      |        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = DroidDO.class, name = "Droid")})
      |""".stripMargin)
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

> in sbt plugin option 
- `packageName` was rename to `generatePackageName`
- `generateCodegenTargetPath` can be used for setting codegen path where code have bean created by task `graphqlCodegen`, since 3.0.0 .


