import sbt.Keys.libraryDependencies
import sbt._

/**
 * The dependence of the plugin itself
 *
 * @author 梦境迷离 dreamylost
 * @since 2020-07-19
 * @version v1.0
 */
object Dependencies {

  object Versions {
    lazy val scala212 = "2.12.12"
    lazy val scala211 = "2.11.12"
    val codegen = "2.4.0"
  }

  import Versions._

  object Compiles {
    val selfDependencies = libraryDependencies ++= Seq(
      "io.github.kobylynskyi" % "graphql-java-codegen" % codegen
    )
  }

}
