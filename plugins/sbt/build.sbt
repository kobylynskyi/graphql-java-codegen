sbtPlugin := true

name := "sbt-graphql-java-codegen"
organization := "io.github.kobylynskyi"

libraryDependencies += "io.github.kobylynskyi" % "graphql-java-codegen" % "1.6.0"

enablePlugins(SbtPlugin)
scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}
scriptedBufferLog := false

licenses := Seq("MIT License" -> url("https://github.com/kobylynskyi/graphql-java-codegen/blob/master/LICENSE.md"))
