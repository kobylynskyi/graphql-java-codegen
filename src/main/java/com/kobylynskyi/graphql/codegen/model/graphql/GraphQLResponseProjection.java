package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * The implementation class should contain the fields of the particular type that should be returned to the client.
 */
public abstract class GraphQLResponseProjection {

    protected final Map<String, GraphQLResponseField> fields = new LinkedHashMap<>();

    public GraphQLResponseProjection() {
    }

    public GraphQLResponseProjection(GraphQLResponseProjection projection) {
        if (projection == null) {
            return;
        }
        projection.fields.values().forEach(this::add$);
    }

    public GraphQLResponseProjection(List<? extends GraphQLResponseProjection> projections) {
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

    public abstract GraphQLResponseProjection deepCopy$();

    protected void add$(GraphQLResponseField responseField) {
        GraphQLResponseField existingResponseField = fields.get(responseField.getName());
        if (existingResponseField != null) {
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
        } else {
            fields.put(responseField.getName(), responseField.deepCopy());
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
