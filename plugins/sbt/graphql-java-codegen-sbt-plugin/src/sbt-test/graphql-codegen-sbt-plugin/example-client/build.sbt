import java.util

name := "example-client"

organization := "io.github.jxnu-liguobin"

libraryDependencies ++= Seq(
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.8.2",
  "org.apache.logging.log4j" % "log4j-core" % "2.8.2",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.8.2",
  "com.squareup.okhttp3" % "okhttp" % "4.7.2",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.11.1",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.1",
  "org.json" % "json" % "20190722")


enablePlugins(GraphQLCodegenPlugin)

graphqlJavaCodegenVersion := Some((version in Scope.ThisScope).value)

GraphQLCodegenPluginDependencies

//default graphqlJavaCodegen is release
//graphqlJavaCodegenVersion := Some("4.1.4-SNAPSHOT")
graphqlSchemaPaths := List("src/main/resources/schema.graphqls")
modelPackageName := Some("io.github.dreamylost.model")
apiPackageName := Some("io.github.dreamylost.api")
generateClient := true
generateApis := true
// Scala collection cannot be used. The latter one uses the put method, which is not supported by Scala collection.
// in FB, collection is immutable
customTypesMapping := {
  val mapping = new util.HashMap[String, String]
//  mapping.put("Email", "io.github.dreamylost.scalar.EmailScalar")
  //Character will conflict with java.lang.Character. maybe because Scala imports it automatically java.lang *.
  //So we use Full class name
  mapping
}

//Of course, you can also add a suffix to be different from it
modelNameSuffix := Some("DO")

customAnnotationsMapping := {
  val mapping = new util.HashMap[String, util.List[String]]
  val annotations = new util.ArrayList[String]()
  annotations.add("@com.fasterxml.jackson.annotation.JsonTypeInfo(use=com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY,property = \"__typename\")")
  annotations.add("""@com.fasterxml.jackson.annotation.JsonSubTypes(value = {
                    |        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = HumanDO.class, name = "Human"),
                    |        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = DroidDO.class, name = "Droid")})""".stripMargin)
  //must add this annotation
  //property is __typename and you must with __typename while invoke, like new CharacterResponseProjection().id().name().typename()
  //and in @JsonSubTypes.Type, name is __typename's value
  mapping.put("Character",annotations)//since v3.0.0
  mapping
}

generateCodegenTargetPath in GraphQLCodegenConfig  := crossTarget.value / "src_managed_graphql_my_folder"

generateEqualsAndHashCode := true

generateToString := true