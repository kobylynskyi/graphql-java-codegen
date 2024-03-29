package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The implementation class should contain the fields of the particular type that should be returned to the client.
 */
public abstract class GraphQLResponseProjection {

    /**
     * Contains all response projection fields, where:
     * key - is the name+alias pair (where alias is nullable)
     * value - is GraphQLResponseField which represents the response projection field
     */
    protected final Map<Pair<String, String>, GraphQLResponseField> fields = new LinkedHashMap<>();

    protected GraphQLResponseProjection() {
    }

    protected GraphQLResponseProjection(GraphQLResponseProjection projection) {
        if (projection == null) {
            return;
        }
        projection.fields.values().forEach(this::add$);
    }

    protected GraphQLResponseProjection(List<? extends GraphQLResponseProjection> projections) {
        if (projections == null) {
            return;
        }
        for (GraphQLResponseProjection projection : projections) {
            if (projection == null) {
                continue;
            }
            projection.fields.values().forEach(this::add$);
        }
    }

    @SuppressWarnings({"checkstyle:MethodName", "java:S100"})
    public abstract GraphQLResponseProjection deepCopy$();

    @SuppressWarnings({"checkstyle:MethodName", "java:S100", "java:S3824"})
    protected void add$(GraphQLResponseField responseField) {
        Pair<String, String> nameAndAlias = new Pair<>(responseField.getName(), responseField.getAlias());
        GraphQLResponseField existingResponseField = fields.get(nameAndAlias);
        if (existingResponseField == null) {
            fields.put(nameAndAlias, responseField.deepCopy());
            return;
        }

        if (!Objects.equals(responseField.getParameters(), existingResponseField.getParameters())) {
            throw new IllegalArgumentException(
                    String.format("Field '%s' has an argument conflict", existingResponseField.getName()));
        }

        if (responseField.getAlias() != null) {
            existingResponseField.alias(responseField.getAlias());
        }
        if (responseField.getParameters() != null) {
            existingResponseField.parameters(responseField.getParameters().deepCopy());
        }
        if (responseField.getProjection() != null) {
            GraphQLResponseProjection projectionCopy = responseField.getProjection().deepCopy$();
            if (existingResponseField.getProjection() != null) {
                for (GraphQLResponseField field : projectionCopy.fields.values()) {
                    existingResponseField.getProjection().add$(field);
                }
            } else {
                existingResponseField.projection(projectionCopy);
            }
        }
    }

    @Override
    public String toString() {
        if (fields.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(" ", "{ ", " }");
        for (GraphQLResponseField value : fields.values()) {
            joiner.add(value.toString());
        }
        return joiner.toString();
    }

}
