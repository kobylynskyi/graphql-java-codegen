package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

public class UpdateIssuePayloadResponseProjection extends GraphQLResponseProjection {

    public UpdateIssuePayloadResponseProjection() {
    }

    public UpdateIssuePayloadResponseProjection clientMutationId() {
        return clientMutationId(null);
    }

    public UpdateIssuePayloadResponseProjection clientMutationId(String alias) {
        fields.add(new GraphQLResponseField("clientMutationId").alias(alias));
        return this;
    }

    public UpdateIssuePayloadResponseProjection issue(IssueResponseProjection subProjection) {
        return issue(null, subProjection);
    }

    public UpdateIssuePayloadResponseProjection issue(String alias, IssueResponseProjection subProjection) {
        fields.add(new GraphQLResponseField("issue").alias(alias).projection(subProjection));
        return this;
    }

    public UpdateIssuePayloadResponseProjection union(UpdateNodeUnionResponseProjection subProjection) {
        return union(null, subProjection);
    }

    public UpdateIssuePayloadResponseProjection union(String alias, UpdateNodeUnionResponseProjection subProjection) {
        fields.add(new GraphQLResponseField("union").alias(alias).projection(subProjection));
        return this;
    }

    @Override
    public GraphQLResponseProjection all$() {
        return null;
    }

    @Override
    public GraphQLResponseProjection all$(int maxDepth) {
        return null;
    }
}
