package com.kobylynskyi.graphql.codegen.model;

/**
 * Strategy for creating API interface calsses
 */
public enum ApiInterfaceStrategy {

    /**
     * Generate separate interface classes for each GraphQL operation.
     */
    INTERFACE_PER_OPERATION,

    /**
     * Do not generate separate interfaces classes for GraphQL operation.
     */
    DO_NOT_GENERATE

}
