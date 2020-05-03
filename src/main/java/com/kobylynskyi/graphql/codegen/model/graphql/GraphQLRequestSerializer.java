package com.kobylynskyi.graphql.codegen.model.graphql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphQLRequestSerializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String serialize(GraphQLRequest graphQLRequest) {
        if (graphQLRequest == null || graphQLRequest.getRequest() == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(graphQLRequest.getRequest().getOperationType().name().toLowerCase());
        builder.append(" { ");
        builder.append(graphQLRequest.getRequest().getOperationName());
        Map<String, Object> input = graphQLRequest.getRequest().getInput();
        if (input != null && !input.isEmpty()) {
            builder.append("(");
            Iterator<Map.Entry<String, Object>> inputEntryIterator = input.entrySet().iterator();
            while (inputEntryIterator.hasNext()) {
                Map.Entry<String, Object> inputEntry = inputEntryIterator.next();
                if (inputEntry.getValue() != null) {
                    builder.append(inputEntry.getKey());
                    builder.append(": ");
                    builder.append(getEntry(inputEntry.getValue()));
                }
                if (inputEntryIterator.hasNext()) {
                    builder.append(", ");
                }
            }
            builder.append(")");
        }
        if (graphQLRequest.getResponseProjection() != null) {
            builder.append(graphQLRequest.getResponseProjection().toString());
        }
        builder.append(" }");
        return buildJsonQuery(builder.toString());
    }

    private static String buildJsonQuery(String queryString) {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("query", queryString);
        try {
            return OBJECT_MAPPER.writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {
            throw new UnableToBuildJsonQueryException(e);
        }
    }

    public static String getEntry(Object input) {
        if (input == null) {
            return null;
        }
        if (input instanceof Collection) {
            Collection<?> inputCollection = (Collection) input;
            return inputCollection.stream()
                    .map(GraphQLRequestSerializer::getEntry)
                    .collect(Collectors.joining(", ", "[ ", " ]"));
        }
        if (input instanceof Enum<?>) {
            return input.toString();
        } else if (input instanceof String) {
            return escapeJsonString(input.toString());
        } else {
            return input.toString();
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
