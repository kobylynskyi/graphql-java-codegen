package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

import java.util.List;

public class UpdateIssuePayloadResponseProjection extends GraphQLResponseProjection {

    public UpdateIssuePayloadResponseProjection() {
    }

    public UpdateIssuePayloadResponseProjection(UpdateIssuePayloadResponseProjection projection) {
        super(projection);
    }

    public UpdateIssuePayloadResponseProjection(List<UpdateIssuePayloadResponseProjection> projections) {
        super(projections);
    }

    public UpdateIssuePayloadResponseProjection clientMutationId() {
        return clientMutationId(null);
    }

    public UpdateIssuePayloadResponseProjection clientMutationId(String alias) {
        add$(new GraphQLResponseField("clientMutationId").alias(alias));
        return this;
    }

    public UpdateIssuePayloadResponseProjection issue(IssueResponseProjection subProjection) {
        return issue(null, subProjection);
    }

    public UpdateIssuePayloadResponseProjection issue(String alias, IssueResponseProjection subProjection) {
        add$(new GraphQLResponseField("issue").alias(alias).projection(subProjection));
        return this;
    }

    public UpdateIssuePayloadResponseProjection union(UpdateNodeUnionResponseProjection subProjection) {
        return union(null, subProjection);
    }

    public UpdateIssuePayloadResponseProjection union(String alias, UpdateNodeUnionResponseProjection subProjection) {
        add$(new GraphQLResponseField("union").alias(alias).projection(subProjection));
        return this;
    }

    public GraphQLResponseProjection all$() {
        return null;
    }

    public GraphQLResponseProjection all$(int maxDepth) {
        return null;
    }


    @Override
    public UpdateIssuePayloadResponseProjection deepCopy$() {
        return new UpdateIssuePayloadResponseProjection(this);
    }

}
