package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class VersionQueryRequest implements GraphQLOperationRequest {

    private static final GraphQLOperation OPERATION_TYPE = GraphQLOperation.QUERY;
    private static final String OPERATION_NAME = "version";

    private String alias;
    private Map<String, Object> input = new LinkedHashMap<>();

    public VersionQueryRequest() {
    }

    public VersionQueryRequest(String alias) {
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

    @Override
    public String toString() {
        return Objects.toString(input);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {


        public Builder() {
        }


        public VersionQueryRequest build() {
            VersionQueryRequest obj = new VersionQueryRequest();
            return obj;
        }

    }
}