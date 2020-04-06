package com.kobylynskyi.graphql.codegen.model.request.data;

import graphql.language.OperationDefinition;

import java.util.LinkedHashMap;
import java.util.Map;

public class UpdateIssueMutationRequest implements com.kobylynskyi.graphql.codegen.model.request.GraphQLOperationRequest {

    private static final OperationDefinition.Operation OPERATION_TYPE = OperationDefinition.Operation.MUTATION;
    private static final String OPERATION_NAME = "updateIssue";

    private Map<String, Object> input = new LinkedHashMap<>();

    public UpdateIssueMutationRequest() {
    }

    public void setInput(UpdateIssueInput input) {
        this.input.put("input", input);
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

}
