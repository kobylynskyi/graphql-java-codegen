package com.kobylynskyi.graphql.codegen.model.request;

import graphql.language.OperationDefinition;

import java.util.Map;

/**
 * The contract for GraphQL request
 */
public interface GraphQLOperationRequest {

    /**
     * Type of GraphQL operation.
     * Can be one of {@link graphql.language.OperationDefinition.Operation}
     *
     * @return type of GraphQL operation
     */
    OperationDefinition.Operation getOperationType();

    /**
     * Name of GraphQL operation.
     *
     * @return name of GraphQL operation
     */
    String getOperationName();

    /**
     * Input for for GraphQL operation. Where:
     * - key is input field name
     * - value is input field value
     *
     * @return input data for GraphQL operation
     */
    Map<String, Object> getInput();

}
