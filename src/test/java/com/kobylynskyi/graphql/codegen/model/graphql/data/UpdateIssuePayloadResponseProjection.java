package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

public class UpdateIssuePayloadResponseProjection extends GraphQLResponseProjection {

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

}
