package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.Map;

/**
 * The contract for GraphQL request
 */
public interface GraphQLOperationRequest {

    /**
     * Type of GraphQL operation.
     * Can be one of {@link GraphQLOperation}
     *
     * @return type of GraphQL operation
     */
    GraphQLOperation getOperationType();

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
