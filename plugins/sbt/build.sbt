sbtPlugin := true

name := "sbt-graphql-java-codegen"
organization := "io.github.kobylynskyi"
description := "Plugin for generating Java code based on GraphQL schema"

libraryDependencies += "io.github.kobylynskyi" % "graphql-java-codegen" % version.value

enablePlugins(SbtPlugin)
scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}
scriptedBufferLog := false

licenses := Seq("MIT License" -> url("https://github.com/kobylynskyi/graphql-java-codegen/blob/master/LICENSE.md"))
bintrayOrganization := None
bintrayRepository := "sbt-plugins"
