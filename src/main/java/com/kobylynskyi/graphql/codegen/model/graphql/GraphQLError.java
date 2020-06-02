package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GraphQLError {

    private String message;
    private List<GraphQLErrorSourceLocation> locations;
    private GraphQLErrorType errorType;
    private List<Object> path;
    private Map<String, Object> extensions;

    public GraphQLError() {
    }

    public GraphQLError(String message, List<GraphQLErrorSourceLocation> locations,
                        GraphQLErrorType errorType, List<Object> path, Map<String, Object> extensions) {
        this.message = message;
        this.locations = locations;
        this.errorType = errorType;
        this.path = path;
        this.extensions = extensions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<GraphQLErrorSourceLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<GraphQLErrorSourceLocation> locations) {
        this.locations = locations;
    }

    public GraphQLErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(GraphQLErrorType errorType) {
        this.errorType = errorType;
    }

    public List<Object> getPath() {
        return path;
    }

    public void setPath(List<Object> path) {
        this.path = path;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphQLError that = (GraphQLError) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(locations, that.locations) &&
                errorType == that.errorType &&
                Objects.equals(path, that.path) &&
                Objects.equals(extensions, that.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, locations, errorType, path, extensions);
    }
}