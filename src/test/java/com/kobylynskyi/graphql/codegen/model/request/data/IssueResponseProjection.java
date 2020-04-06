package com.kobylynskyi.graphql.codegen.model.request.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class IssueResponseProjection implements com.kobylynskyi.graphql.codegen.model.request.GraphQLResponseProjection {

    private Map<String, Object> fields = new LinkedHashMap<>();

    public IssueResponseProjection() {
    }

    public IssueResponseProjection activeLockReason() {
        fields.put("activeLockReason", null);
        return this;
    }

    // REST OF THE STUFF WAS REMOVED

    @Override
    public String toString() {
        if (fields.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(" ", "{ ", " }");
        for (Map.Entry<String, Object> property : fields.entrySet()) {
            joiner.add(property.getKey());
            if (property.getValue() != null) {
                joiner.add(" ").add(property.getValue().toString());
            }
        }
        return joiner.toString();
    }
}
