name := "simple"

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.13.2",
    scriptedLaunchOpts += s"-Dplugin.version=${version.value}",
    apiPackageName := Some("io.github.kobylynskyi.graphql.test.api"),
    modelPackageName := Some("io.github.kobylynskyi.graphql.test.model"),
    apiAsyncReturnType := "scala.concurrent.Future", // if Async class is not at current source, need import dependency
    generateAsyncApi := true
    //use full class name is good
  ).enablePlugins(GraphQLCodegenPlugin).settings(GraphQLCodegenPluginDependencies)
