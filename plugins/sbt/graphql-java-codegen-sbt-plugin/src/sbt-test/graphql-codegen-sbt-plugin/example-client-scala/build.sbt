import java.util

name := "example-client-scala"

organization := "io.github.jxnu-liguobin"

libraryDependencies ++= Seq(
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.8.2",
  "org.apache.logging.log4j" % "log4j-core" % "2.8.2",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.8.2",
  "com.squareup.okhttp3" % "okhttp" % "4.7.2",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.11.3",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.3",
  "org.json" % "json" % "20190722")

enablePlugins(GraphQLCodegenPlugin)
graphqlJavaCodegenVersion := Some((version in Scope.ThisScope).value)
GraphQLCodegenPluginDependencies
graphqlSchemaPaths := List("src/main/resources/schema.graphqls")
modelPackageName := Some("io.github.dreamylost.model")
apiPackageName := Some("io.github.dreamylost.api")
generateClient := true
generateApis := true
generatedLanguage := com.kobylynskyi.graphql.codegen.model.GeneratedLanguage.SCALA
generateImmutableModels := true
modelNameSuffix := Some("DO")
customAnnotationsMapping := {
  val mapping = new util.HashMap[String, util.List[String]]
  val annotations = new util.ArrayList[String]()
  annotations.add("@com.fasterxml.jackson.annotation.JsonTypeInfo(use=com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY,property = \"__typename\")")
  annotations.add(
    """@com.fasterxml.jackson.annotation.JsonSubTypes(value = Array(
      |        new com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = classOf[HumanDO], name = "Human"),
      |        new com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = classOf[DroidDO], name = "Droid")))""".stripMargin)
  mapping.put("Character", annotations)
  mapping.put("Droid.appearsIn", util.Arrays.asList("@com.fasterxml.jackson.module.scala.JsonScalaEnumeration(classOf[io.github.dreamylost.model.EpisodeDOTypeRefer])"))
  mapping.put("Human.appearsIn", util.Arrays.asList("@com.fasterxml.jackson.module.scala.JsonScalaEnumeration(classOf[io.github.dreamylost.model.EpisodeDOTypeRefer])"))
  mapping
}
generateCodegenTargetPath in GraphQLCodegenConfig := crossTarget.value / "src_managed_graphql_scala"
generateEqualsAndHashCode := true
generateToString := true