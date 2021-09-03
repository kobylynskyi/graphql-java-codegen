package com.kobylynskyi.graphql.codegen.model.graphql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Serializer of GraphQL request.
 * Provides ability to convert GraphQLRequest object to HTTP Json body, as well as to raw query string.
 */
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
        GraphQLOperationRequest firstRequest = graphQLRequests.getRequests().get(0).getRequest();
        StringBuilder queryBuilder = new StringBuilder();
        for (GraphQLRequest request : graphQLRequests.getRequests()) {
            if (request == null || request.getRequest() == null) {
                throw new IllegalArgumentException("Null GraphQL request was supplied");
            }
            if (firstRequest.getOperationType() != null &&
                    firstRequest.getOperationType() != request.getRequest().getOperationType()) {
                throw new IllegalArgumentException(
                        "Only operations of the same type (query/mutation/subscription) can be executed at once");
            }
            queryBuilder.append(buildQuery(request)).append(" ");
        }
        return jsonQuery(operationWrapper(
                firstRequest.getOperationType(),
                graphQLRequests.getOperationName(),
                queryBuilder.toString()));
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
        return jsonQuery(toQueryString(graphQLRequest));
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

        String operationName = graphQLRequest.getOperationName() == null ?
                graphQLRequest.getRequest().getOperationName() : graphQLRequest.getOperationName();

        return operationWrapper(
                graphQLRequest.getRequest().getOperationType(),
                operationName,
                buildQuery(graphQLRequest));
    }

    private static String operationWrapper(GraphQLOperation operationType, String operationName, String queryValue) {
        assert operationType != null;
        String operationTypeLowerCased = operationType.name().toLowerCase();
        if (operationName == null) {
            return String.format("%s { %s }", operationTypeLowerCased, queryValue);
        } else {
            return String.format("%s %s { %s }", operationTypeLowerCased, operationName, queryValue);
        }
    }

    private static String buildQuery(GraphQLRequest graphQLRequest) {
        StringBuilder builder = new StringBuilder();
        GraphQLOperationRequest request = graphQLRequest.getRequest();
        if (request.getAlias() != null) {
            builder.append(request.getAlias()).append(": ");
        }
        builder.append(request.getOperationName());
        Map<String, Object> input = request.getInput();
        Set<String> useObjectMapperForInputSerialization = request.getUseObjectMapperForInputSerialization();
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
                    boolean useObjectMapper = useObjectMapperForInputSerialization.contains(inputEntry.getKey());
                    builder.append(getEntry(inputEntry.getValue(), useObjectMapper));
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

    /**
     * Serialize object to a string
     *
     * @param input           can be any object or collection/map of objects.
     * @param useObjectMapper if true, then use Jackson's ObjectMapper to convert from object to string,
     *                        otherwise use toString
     * @return serialized object
     */
    @SuppressWarnings("java:S1872")
    public static String getEntry(Object input, boolean useObjectMapper) {
        if (input == null) {
            return null;
        } else if (useObjectMapper) {
            return objectMapperWriteValueAsString(input);
        } else if (input instanceof Collection<?>) {
            return serializeCollection((Collection<?>) input, useObjectMapper);
        } else if (input instanceof Map<?, ?>) {
            return serializeMap((Map<?, ?>) input, useObjectMapper);
        } else if (input instanceof Map.Entry<?, ?>) {
            return serializeMapEntry((Map.Entry<?, ?>) input, useObjectMapper);
        } else if (input instanceof Enum<?>) {
            return serializeEnum((Enum<?>) input);
        } else if (input instanceof String) {
            return escapeJsonString(input.toString());
        } else if (input.getClass().getName().equals("scala.Some")) { // TODO: move to Scala Serializer
            // Currently, option only supports primitive types, so that's fine.
            // Now, this kind of case will appear if and only if Seq[Option[Int]] is
            return input.toString().replace("Some(", "").replace(")", "");
        } else if (input.getClass().getName().equals("scala.None$")) {
            return null;
        } else {
            return input.toString();
        }
    }

    public static String serializeCollection(Collection<?> input, boolean useObjectMapper) {
        StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");
        for (Object entry : input) {
            joiner.add(getEntry(entry, useObjectMapper));
        }
        return joiner.toString();
    }

    public static String serializeMap(Map<?, ?> input, boolean useObjectMapper) {
        StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
        for (Map.Entry<?, ?> entry : input.entrySet()) {
            joiner.add(getEntry(entry, useObjectMapper));
        }
        return joiner.toString();
    }

    public static String serializeMapEntry(Map.Entry<?, ?> input, boolean useObjectMapper) {
        // no need to quote String key
        return input.getKey() + ": " + getEntry(input.getValue(), useObjectMapper);
    }

    public static String serializeEnum(Enum<?> input) {
        return input.toString();
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

    public static String objectMapperWriteValueAsString(Object input) {
        try {
            return OBJECT_MAPPER.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            throw new UnableToBuildJsonQueryException(e);
        }
    }

}
