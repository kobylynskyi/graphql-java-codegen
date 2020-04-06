package com.kobylynskyi.graphql.codegen.model.request.data;

import java.util.*;

public class UpdateIssuePayloadResponseProjection implements com.kobylynskyi.graphql.codegen.model.request.GraphQLResponseProjection {

    private Map<String, Object> fields = new LinkedHashMap<>();

    public UpdateIssuePayloadResponseProjection() {
    }

    public UpdateIssuePayloadResponseProjection clientMutationId() {
        fields.put("clientMutationId", null);
        return this;
    }

    public UpdateIssuePayloadResponseProjection issue(IssueResponseProjection subProjection) {
        fields.put("issue", subProjection);
        return this;
    }


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
