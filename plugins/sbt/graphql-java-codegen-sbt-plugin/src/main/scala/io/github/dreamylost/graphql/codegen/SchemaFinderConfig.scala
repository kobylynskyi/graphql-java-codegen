package io.github.dreamylost.graphql.codegen

import com.kobylynskyi.graphql.codegen.supplier.SchemaFinder

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/7/15
 */
case class SchemaFinderConfig(
    rootDir:        String,
    recursive:      Boolean     = SchemaFinder.DEFAULT_RECURSIVE,
    includePattern: String      = SchemaFinder.DEFAULT_INCLUDE_PATTERN,
    excludedFiles:  Set[String] = Set.empty)
