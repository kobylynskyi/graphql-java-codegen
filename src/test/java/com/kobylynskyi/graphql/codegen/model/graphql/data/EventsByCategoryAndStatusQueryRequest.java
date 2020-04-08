package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest;

import java.util.LinkedHashMap;
import java.util.Map;

public class EventsByCategoryAndStatusQueryRequest implements GraphQLOperationRequest {

    private static final GraphQLOperation OPERATION_TYPE = GraphQLOperation.QUERY;
    private static final String OPERATION_NAME = "eventsByCategoryAndStatus";

    private Map<String, Object> input = new LinkedHashMap<>();

    public EventsByCategoryAndStatusQueryRequest() {
    }

    public void setCategoryId(String categoryId) {
        this.input.put("categoryId", categoryId);
    }

    public void setStatus(Status status) {
        this.input.put("status", status);
    }

    @Override
    public GraphQLOperation getOperationType() {
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

}