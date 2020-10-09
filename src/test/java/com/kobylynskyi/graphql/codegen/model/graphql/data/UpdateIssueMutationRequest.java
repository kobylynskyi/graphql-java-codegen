package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest;

import java.util.LinkedHashMap;
import java.util.Map;

public class UpdateIssueMutationRequest implements GraphQLOperationRequest {

    private static final GraphQLOperation OPERATION_TYPE = GraphQLOperation.MUTATION;
    private static final String OPERATION_NAME = "updateIssue";

    private String alias;
    private Map<String, Object> input = new LinkedHashMap<>();

    public UpdateIssueMutationRequest() {
    }

    public UpdateIssueMutationRequest(String alias) {
        this.alias = alias;
    }

    public void setInput(UpdateIssueInput input) {
        this.input.put("input", input);
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
    public String getAlias() {
        return alias;
    }

    @Override
    public Map<String, Object> getInput() {
        return input;
    }

}
