package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * The implementation class should basically contain the fields of the particular type which
 * should be returned back to the client.
 */
public abstract class GraphQLResponseProjection {

    protected final Map<String, Object> fields = new LinkedHashMap<>();
    protected final Map<String, GraphQLParametrizedInput> parametrizedInputs = new LinkedHashMap<>();

    @Override
    public String toString() {
        if (fields.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(" ", "{ ", " }");
        for (Map.Entry<String, Object> property : fields.entrySet()) {
            joiner.add(property.getKey());
            GraphQLParametrizedInput parametrizedInput = parametrizedInputs.get(property.getKey());
            if (parametrizedInput != null) {
                joiner.add(parametrizedInput.toString());
            }
            if (property.getValue() != null) {
                joiner.add(" ").add(property.getValue().toString());
            }
        }
        return joiner.toString();
    }

}
