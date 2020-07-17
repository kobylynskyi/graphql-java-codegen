package io.github.dreamylost.graphql.codegen

import com.kobylynskyi.graphql.codegen.supplier.SchemaFinder

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/7/15
 */
class SchemaFinderConfig(
    val rootDir:        String,
    val recursive:      Boolean     = SchemaFinder.DEFAULT_RECURSIVE,
    val includePattern: String      = SchemaFinder.DEFAULT_INCLUDE_PATTERN,
    val excludedFiles:  Set[String] = Set.empty) {

  def this() = {
    this(null, SchemaFinder.DEFAULT_RECURSIVE, SchemaFinder.DEFAULT_INCLUDE_PATTERN, Set.empty)
  }

}
