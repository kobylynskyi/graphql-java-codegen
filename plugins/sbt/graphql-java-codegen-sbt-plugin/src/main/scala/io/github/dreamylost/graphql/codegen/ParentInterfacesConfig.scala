package io.github.dreamylost.graphql.codegen

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/7/15
 */
case class ParentInterfacesConfig(
    queryResolver:        String = null,
    mutationResolver:     String = null,
    subscriptionResolver: String = null,
    resolver:             String = null)
