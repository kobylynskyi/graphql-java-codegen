package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class UpdateDateMutationRequest implements GraphQLOperationRequest {

    private static final GraphQLOperation OPERATION_TYPE = GraphQLOperation.MUTATION;
    private static final String OPERATION_NAME = "updateDate";
    private final Map<String, Object> input = new LinkedHashMap<>();
    private final Set<String> useObjectMapperForInputSerialization = new HashSet<>();
    private String alias;

    public UpdateDateMutationRequest() {
    }

    public UpdateDateMutationRequest(String alias) {
        this.alias = alias;
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

    public void setInput(DateInput input) {
        this.input.put("input", input);
    }

}
