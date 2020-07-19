name := "sbt-test"


//because in ci, can not find maven local
resolvers += "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository"


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
