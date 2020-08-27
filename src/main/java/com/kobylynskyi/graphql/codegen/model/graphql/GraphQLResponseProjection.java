package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The implementation class should basically contain the fields of the particular type which
 * should be returned back to the client.
 */
public abstract class GraphQLResponseProjection {

    protected final List<GraphQLResponseField> fields = new ArrayList<>();

    /**
     * save current depth for self recursive type.
     * such as {{@code
     * type Human implements Character {
     *     id: ID!
     *     friends: [Character] # if you response this field on Human projection , Character has itself,
     *     # so, we need know depth of subquery
     * }
     * interface Character {
     *     id: ID!
     *     friends: [Character]
     * }
     * Note:
     * Map key is parentProjection.childProjection.currentMethod. e.g. Character.Character.friends (Excluding the first layer)
     * Map value is current depth for Character type. e.g. 1
     * }}
     */
    protected final static Map<String, Integer> projectionDepthOnFields = new ConcurrentHashMap<>();

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
