package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

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
     * Alias of GraphQL operation.
     *
     * @return alias of GraphQL operation
     */
    String getAlias();

    /**
     * Input for for GraphQL operation. Where:
     * - key is input field name
     * - value is input field value
     *
     * @return input data for GraphQL operation
     */
    Map<String, Object> getInput();

    /**
     * Fields that require serialization using
     * {@link com.fasterxml.jackson.databind.ObjectMapper#writeValueAsString(Object)}
     * <p>
     * Values should be defined here in format: <i>GraphqlObjectName.fieldName</i> or <i>GraphqlTypeName</i>
     * <p>
     * If just type is specified, then all fields of this type will be serialized using ObjectMapper.
     * <p>
     * E.g.:
     * <ul>
     *   <li>{@code Person.createdDateTime}</li>
     *   <li>{@code ZonedDateTime}</li>
     * </ul>
     *
     * @return Set of types and fields that should be serialized using
     * {@link com.fasterxml.jackson.databind.ObjectMapper#writeValueAsString(Object)}
     */
    default Set<String> getUseObjectMapperForInputSerialization() {
        return Collections.emptySet();
    }

}
