package com.kobylynskyi.graphql.codegen.model;

/**
 * Strategy of how root API interface (Query/Mutation/Subscription) will be generated
 */
public enum ApiRootInterfaceStrategy {

    /**
     * Generate multiple super-interfaces for each graphql file.
     * Takes into account apiNamePrefixStrategy.
     * e.g.: OrderServiceQueryResolver.java, ProductServiceQueryResolver.java, etc.
     * (in addition to separate interfaces for each query/mutation/subscription)
     */
    INTERFACE_PER_SCHEMA,

    /**
     * Generate a single QueryResolver.java, MutationResolver.java, SubscriptionResolver.java for all graphqls
     * (in addition to separate interfaces for each query/mutation/subscription)
     */
    SINGLE_INTERFACE,

    /**
     * Do not generate super interface for GraphQL operations.
     */
    DO_NOT_GENERATE

}
