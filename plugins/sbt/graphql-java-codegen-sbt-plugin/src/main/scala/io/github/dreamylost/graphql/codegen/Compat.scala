package io.github.dreamylost.graphql.codegen

import sbt.Keys.{ sourceDirectory, watchSources }
import sbt.Watched.WatchSource
import sbt.internal.io.Source
import sbt.io.{ AllPassFilter, SuffixFilter }
import sbt.{ Def, Task }
import sbt.Configuration

/**
 *
 * @author 梦境迷离 dreamylost
 * @since 2020-07-18
 * @version v1.0
 */
trait Compat {
  self: GraphQLCodegenPlugin =>

  import self.GlobalImport._

  val watchSourcesSetting: Def.Setting[Task[Seq[WatchSource]]] = {
    watchSources += new Source(
      (sourceDirectory in graphqlCodegen).value,
      new SuffixFilter(".graphql") | new SuffixFilter(".graphqls"),
      AllPassFilter)
  }

  protected[this] lazy val GraphQLCodegenConfig = Configuration.of("GraphQLCodegen", "graphqlCodegen" + configurationPostfix)

}
