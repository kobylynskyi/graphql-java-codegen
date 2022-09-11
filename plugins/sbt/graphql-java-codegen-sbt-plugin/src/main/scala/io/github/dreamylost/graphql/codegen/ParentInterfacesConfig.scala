package io.github.dreamylost.graphql.codegen

/**
 *
 * @author 梦境迷离
 * @version 1.0,2020/7/15
 */
final case class ParentInterfacesConfig(
    queryResolver:        String = null,
    mutationResolver:     String = null,
    subscriptionResolver: String = null,
    resolver:             String = null)
