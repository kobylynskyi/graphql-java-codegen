name := "simple"

lazy val root = (project in file("."))
  .settings(
    scalaVersion := "2.13.2",
    apiPackageName := Some("io.github.kobylynskyi.graphql.test.api"),
    modelPackageName := Some("io.github.kobylynskyi.graphql.test.model"),
    apiReturnType := Some("scala.concurrent.Future"), // if Async class is not at current source, need import dependency
    graphqlJavaCodegenVersion := Some((version in Scope.ThisScope).value)
      //use full class name is good
  ).enablePlugins(GraphQLCodegenPlugin).settings(GraphQLCodegenPluginDependencies)


//javaSource in Compile := crossTarget.value / "src_managed_graphql"
