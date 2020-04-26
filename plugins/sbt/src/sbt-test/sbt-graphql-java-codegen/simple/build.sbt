lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.13.2",
    libraryDependencies += "javax.validation" % "validation-api" % "2.0.1.Final",
    graphqlApiPackageName := Some("io.github.kobylynskyi.graphql.test.api"),
    graphqlModelPackageName := Some("io.github.kobylynskyi.graphql.test.model")
  )
