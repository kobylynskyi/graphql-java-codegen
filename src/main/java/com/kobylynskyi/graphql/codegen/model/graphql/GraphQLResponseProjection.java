package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * The implementation class should basically contain the fields of the particular type which
 * should be returned back to the client.
 */
public abstract class GraphQLResponseProjection {

    protected final List<GraphQLResponseField> fields = new ArrayList<>();

    @Override
    public String toString() {
        if (fields.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(" ", "{ ", " }");
        fields.forEach(field -> joiner.add(field.toString()));
        return joiner.toString();
    }
}
