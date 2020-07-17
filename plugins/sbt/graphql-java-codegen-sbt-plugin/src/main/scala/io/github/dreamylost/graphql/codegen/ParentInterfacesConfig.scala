package io.github.dreamylost.graphql.codegen

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/7/15
 */
class ParentInterfacesConfig(
    val queryResolver:        String,
    val mutationResolver:     String,
    val subscriptionResolver: String,
    val resolver:             String) {

  def this() = {
    this(null, null, null, null)
  }
}
