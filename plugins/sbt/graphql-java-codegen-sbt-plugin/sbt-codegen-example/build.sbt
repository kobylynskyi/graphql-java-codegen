import java.util

name := "sbt-codegen-exmaple"

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
  //会与java.lang.* 冲突，指定自定义类型，会使用全类名构建
  mapping.put("Character", "io.github.dreamylost.model.CharacterEntity")
  mapping
}

//也可以使用后缀避免与java.lang.* 冲突
modelNameSuffix := Some("Entity")