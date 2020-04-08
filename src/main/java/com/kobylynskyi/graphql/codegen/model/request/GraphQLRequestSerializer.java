package com.kobylynskyi.graphql.codegen.model.request;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphQLRequestSerializer {

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
        String escapedString = builder.toString().replaceAll("\"", "\\\\\"");
        return String.format("{\"query\":\"%s\"}", escapedString);
    }

    private static String getEntry(Object input) {
        if (input instanceof Collection) {
            Collection<?> inputCollection = (Collection) input;
            return inputCollection.stream()
                    .map(GraphQLRequestSerializer::getEntry)
                    .collect(Collectors.joining(", ", "[ ", " ]"));
        }
        if (input instanceof Enum<?>) {
            return input.toString();
        } else if (input instanceof String) {
            return "\"" + input.toString() + "\"";
        } else {
            return input.toString();
        }
    }

}
