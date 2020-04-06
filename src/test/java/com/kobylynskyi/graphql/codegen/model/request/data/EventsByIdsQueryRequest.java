package com.kobylynskyi.graphql.codegen.model.request.data;

import java.util.*;
import graphql.language.*;

public class EventsByIdsQueryRequest implements com.kobylynskyi.graphql.codegen.model.request.GraphQLOperationRequest {

    private static final OperationDefinition.Operation OPERATION_TYPE = OperationDefinition.Operation.QUERY;
    private static final String OPERATION_NAME = "eventsByIds";

    private Map<String, Object> input = new LinkedHashMap<>();

    public EventsByIdsQueryRequest() {
    }

    public void setIds(Collection<String> ids) {
        this.input.put("ids", ids);
    }

    @Override
    public OperationDefinition.Operation getOperationType() {
        return OPERATION_TYPE;
    }

    @Override
    public String getOperationName() {
        return OPERATION_NAME;
    }

    @Override
    public Map<String, Object> getInput() {
        return input;
    }

    @Override
    public String toString() {
        return Objects.toString(input);
    }
}