name := "sbt-test"

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.13.2",
    apiPackageName := Some("io.github.kobylynskyi.graphql.test.api"),
    modelPackageName := Some("io.github.kobylynskyi.graphql.test.model"),
    apiAsyncReturnType := Some("scala.concurrent.Future"), // if Async class is not at current source, need import dependency
    //use full class name is good
    generateAsyncApi := Some(true)
  ).enablePlugins(GraphQLCodegenPlugin).settings(GraphQLCodegenPluginDependencies)
