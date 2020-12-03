package com.kobylynskyi.graphql.codegen.model.graphql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class GraphQLRequestSerializer {

    public static final ObjectMapper OBJECT_MAPPER = Utils.OBJECT_MAPPER;

    private GraphQLRequestSerializer() {
    }

    /**
     * Serializes GraphQL requests to be used as HTTP JSON body
     * according to https://graphql.org/learn/serving-over-http specifications
     *
     * @param graphQLRequests GraphQL requests to serialize
     * @return the serialized request
     */
    public static String toHttpJsonBody(GraphQLRequests graphQLRequests) {
        if (graphQLRequests.getRequests().isEmpty()) {
            throw new IllegalArgumentException("At least one GraphQL request should be supplied");
        }
        GraphQLOperation operation = GraphQLOperation.QUERY;
        StringBuilder queryBuilder = new StringBuilder();
        for (GraphQLRequest request : graphQLRequests.getRequests()) {
            if (request == null || request.getRequest() == null) {
                throw new IllegalArgumentException("Null GraphQL request was supplied");
            }
            if (operation != null && operation != request.getRequest().getOperationType()) {
                throw new IllegalArgumentException("Only operations of the same type (query/mutation/subscription) can be executed at once");
            }
            queryBuilder.append(buildQuery(request)).append(" ");
            operation = request.getRequest().getOperationType();
        }
        return jsonQuery(operationWrapper(queryBuilder.toString(), operation));
    }

    /**
     * Serializes GraphQL request to be used as HTTP JSON body
     * according to https://graphql.org/learn/serving-over-http specifications
     *
     * @param graphQLRequest the GraphQL request to serialize
     * @return the serialized request
     */
    public static String toHttpJsonBody(GraphQLRequest graphQLRequest) {
        if (graphQLRequest == null || graphQLRequest.getRequest() == null) {
            return null;
        }
        GraphQLOperation operationType = graphQLRequest.getRequest().getOperationType();
        String query = buildQuery(graphQLRequest);
        String queryString = operationWrapper(query, operationType);
        return jsonQuery(queryString);
    }

    /**
     * Serializes GraphQL request as raw query string
     *
     * @param graphQLRequest the GraphQL request to serialize
     * @return the serialized request
     */
    public static String toQueryString(GraphQLRequest graphQLRequest) {
        if (graphQLRequest == null || graphQLRequest.getRequest() == null) {
            return null;
        }
        GraphQLOperation operationType = graphQLRequest.getRequest().getOperationType();
        String query = buildQuery(graphQLRequest);
        return operationWrapper(query, operationType);
    }

    private static String operationWrapper(String query, GraphQLOperation operationType) {
        assert operationType != null;
        return operationType.name().toLowerCase() + " { " + query + " }";
    }

    private static String buildQuery(GraphQLRequest graphQLRequest) {
        StringBuilder builder = new StringBuilder();
        if (graphQLRequest.getRequest().getAlias() != null) {
            builder.append(graphQLRequest.getRequest().getAlias()).append(": ");
        }
        builder.append(graphQLRequest.getRequest().getOperationName());
        Map<String, Object> input = graphQLRequest.getRequest().getInput();
        if (requestHasInput(input)) {
            builder.append("(");
            Iterator<Map.Entry<String, Object>> inputEntryIterator = input.entrySet().iterator();
            boolean valueAdded = false;
            while (inputEntryIterator.hasNext()) {
                Map.Entry<String, Object> inputEntry = inputEntryIterator.next();
                if (inputEntry.getValue() != null) {
                    if (valueAdded) {
                        builder.append(", ");
                    }
                    builder.append(inputEntry.getKey());
                    builder.append(": ");
                    builder.append(getEntry(inputEntry.getValue()));
                    valueAdded = true;
                }
            }
            builder.append(")");
        }
        if (graphQLRequest.getResponseProjection() != null) {
            builder.append(graphQLRequest.getResponseProjection().toString());
        }
        return builder.toString();
    }

    private static boolean requestHasInput(Map<String, Object> input) {
        return input != null && !input.isEmpty() &&
                input.values().stream().anyMatch(Objects::nonNull);
    }

    private static String jsonQuery(String queryString) {
        ObjectNode objectNode = Utils.OBJECT_MAPPER.createObjectNode();
        objectNode.put("query", queryString);
        return objectMapperWriteValueAsString(objectNode);
    }

    public static String getEntry(Object input) {
        return getEntry(input, false);
    }

    public static String getEntry(Object input, boolean useObjectMapper) {
        if (input == null) {
            return null;
        }
        if (input instanceof Collection<?>) {
            StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");
            for (Object collectionEntry : (Collection<?>) input) {
                joiner.add(getEntry(collectionEntry, useObjectMapper));
            }
            return joiner.toString();
        }
        if (useObjectMapper) {
            return objectMapperWriteValueAsString(input);
        }
        if (input instanceof Enum<?>) {
            return input.toString();
        } else if (input instanceof String) {
            return escapeJsonString(input.toString());
        } else {
            return input.toString();
        }
    }

    public static String objectMapperWriteValueAsString(Object input) {
        try {
            return OBJECT_MAPPER.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            throw new UnableToBuildJsonQueryException(e);
        }
    }

    /**
     * Encodes the value as a JSON string according to http://json.org/ rules
     *
     * @param stringValue the value to encode as a JSON string
     * @return the encoded string
     */
    public static String escapeJsonString(String stringValue) {
        int len = stringValue.length();
        StringBuilder sb = new StringBuilder(len + 2);
        sb.append("\"");
        for (int i = 0; i < len; i++) {
            char ch = stringValue.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(ch);
            }
        }
        sb.append("\"");
        return sb.toString();
    }

}
