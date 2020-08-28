package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.*;

/**
 * The implementation class should basically contain the fields of the particular type which
 * should be returned back to the client.
 */
public abstract class GraphQLResponseProjection {

    protected final List<GraphQLResponseField> fields = new ArrayList<>();

    /**
     * save current depth for self recursive type.
     * such as
     * {{@code
     * type Human implements Character {
     *     id: ID!
     *     friends: [Character] # if you response this field on Human projection , Character has itself,
     *     # so, we need know depth of subquery
     * }
     * interface Character {
     *     id: ID!
     *     friends: [Character]
     * }
     *
     * Note:
     * Map key is parentProjection.childProjection.currentMethod. e.g. CharacterResponseProjection.CharacterResponseProjection.friends (excluding the first layer, so if only want the first child layer, use `all$(1)`)
     * Map value is current depth for Character type. Values exists 1 or 0, because each projection have a new instance of projectionDepthOnFields, so it always be `1`
     * and `responseProjectionMaxDepth` will reduce by recursive. it's actually a marker.
     * }}
     */
    protected final Map<String, Integer> projectionDepthOnFields = new HashMap<>();

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
