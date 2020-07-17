import java.util

name := "sbt-codegen-exmaple"

version := "1.0.0-SNAPSHOT"

organization := "io.github.dreamylost"


enablePlugins(GraphQLCodegenPlugin)

libraryDependencies ++= GraphQLCodegen ++ Seq(
  "org.springframework" % "spring-web" % "5.2.7.RELEASE"
) ++ Seq(
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.8.2",
  "org.apache.logging.log4j" % "log4j-core" % "2.8.2",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.8.2")


graphqlSchemaPaths := List("src/main/resources/schema.graphqls")
modelPackageName := Some("io.github.dreamylost.model")
apiPackageName := Some("io.github.dreamylost.api")
generateClient := Some(true)
generateApis := Some(true)
//因为插件依赖的框架内部调用了put
customTypesMapping := {
  val mapping = new util.HashMap[String, String]
  mapping.put("Email", "io.github.dreamylost.scalar.EmailScalar")
  mapping
}

modelNameSuffix := Some("Entity")