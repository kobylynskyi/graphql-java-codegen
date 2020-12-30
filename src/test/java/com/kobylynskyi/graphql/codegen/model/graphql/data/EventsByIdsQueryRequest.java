package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EventsByIdsQueryRequest implements GraphQLOperationRequest {

    private static final GraphQLOperation OPERATION_TYPE = GraphQLOperation.QUERY;
    private static final String OPERATION_NAME = "eventsByIds";

    private String alias;
    private final Map<String, Object> input = new LinkedHashMap<>();
    private final Set<String> useObjectMapperForInputSerialization = new HashSet<>();

    public EventsByIdsQueryRequest() {
    }

    public EventsByIdsQueryRequest(String alias) {
        this.alias = alias;
    }

    public void setContextId(String contextId) {
        this.input.put("contextId", contextId);
    }

    public void setIds(Collection<String> ids) {
        this.input.put("ids", ids);
    }

    public void setTranslated(Boolean translated) {
        this.input.put("translated", translated);
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
    public Set<String> getUseObjectMapperForInputSerialization() {
        return useObjectMapperForInputSerialization;
    }

    @Override
    public String toString() {
        return Objects.toString(input);
    }

    public static class Builder {

        private String contextId;
        private Collection<String> ids;
        private Boolean translated;

        public Builder() {
        }

        public Builder setContextId(String contextId) {
            this.contextId = contextId;
            return this;
        }

        public Builder setIds(Collection<String> ids) {
            this.ids = ids;
            return this;
        }

        public Builder setTranslated(Boolean translated) {
            this.translated = translated;
            return this;
        }


        public EventsByIdsQueryRequest build() {
            EventsByIdsQueryRequest obj = new EventsByIdsQueryRequest();
            obj.setContextId(contextId);
            obj.setIds(ids);
            obj.setTranslated(translated);
            return obj;
        }

    }
}