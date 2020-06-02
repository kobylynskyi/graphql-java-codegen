package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.List;

/**
 * GraphQL response. Contains data and errors
 *
 * @param <T> type of response
 */
public class GraphQLResult<T> {

    private T data;
    private List<GraphQLError> errors;

    public GraphQLResult() {
    }

    public GraphQLResult(T data, List<GraphQLError> errors) {
        this.data = data;
        this.errors = errors;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<GraphQLError> getErrors() {
        return errors;
    }

    public void setErrors(List<GraphQLError> errors) {
        this.errors = errors;
    }

    /**
     * @return true if there are any errors present
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

}
