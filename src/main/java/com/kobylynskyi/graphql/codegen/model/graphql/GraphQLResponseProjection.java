package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * The implementation class should basically contain the fields of the particular type which
 * should be returned back to the client.
 */
public abstract class GraphQLResponseProjection {

    protected final List<GraphQLResponseField> fields = new ArrayList<>();

    /**
     * save current depth for self recursive type.(it's actually a marker)
     * such as
     * {{@code
     * type Human implements Character {
     * id: ID!
     * friends: [Character] # if you response this field on Human projection , Character has itself,
     * # so, we need know depth of subquery.
     * }
     * interface Character {
     * id: ID!
     * friends: [Character]
     * }
     * }}
     * Map Notes:
     * `key` is parentProjection.childProjection.currentMethod. e.g. `CharacterResponseProjection.CharacterResponseProjection.friends` (excluding the first layer, so if only want the first child layer, use `all$(1)`)
     * `value` is current depth for Character type. Each projection has a new instance of `projectionDepthOnFields`, so it always be `1` or `0`.
     * and `responseProjectionMaxDepth` will reduce by recursive.
     */
    protected final Map<String, Integer> projectionDepthOnFields = new HashMap<>();

    //Defined at the parent level to use dynamic calls, default null.
    public abstract GraphQLResponseProjection all$();

    public abstract GraphQLResponseProjection all$(int maxDepth);

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
