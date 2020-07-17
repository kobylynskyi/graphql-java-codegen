import java.util

name := "example-client"

organization := "io.github.jxnu-liguobin"

libraryDependencies ++=  Seq(
  "org.springframework" % "spring-web" % "5.2.7.RELEASE"
) ++ Seq(
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.8.2",
  "org.apache.logging.log4j" % "log4j-core" % "2.8.2",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.8.2")


enablePlugins(GraphQLCodegenPlugin)

GraphQLCodegen

graphqlSchemaPaths := List("src/main/resources/schema.graphqls")
modelPackageName := Some("io.github.dreamylost.model")
apiPackageName := Some("io.github.dreamylost.api")
generateClient := Some(true)
generateApis := Some(true)
// Scala collection cannot be used. The latter one uses the put method, which is not supported by Scala collection.
// in FB, collection is immutable
customTypesMapping := {
  val mapping = new util.HashMap[String, String]
  mapping.put("Email", "io.github.dreamylost.scalar.EmailScalar")
  //Character will conflict with java.lang.Character. maybe because Scala imports it automatically java.lang *.
  //So we use Full class name
  mapping.put("Character", "io.github.dreamylost.model.CharacterEntity")
  mapping
}

////Of course, you can also add a suffix to be different from it
//modelNameSuffix := Some("Entity")