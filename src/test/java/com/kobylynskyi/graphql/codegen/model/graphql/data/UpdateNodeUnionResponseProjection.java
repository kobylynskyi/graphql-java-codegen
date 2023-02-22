package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

import java.util.List;

/**
 * Response projection for SearchResultItem
 */
public class UpdateNodeUnionResponseProjection extends GraphQLResponseProjection {

    public UpdateNodeUnionResponseProjection() {
    }

    public UpdateNodeUnionResponseProjection(UpdateNodeUnionResponseProjection projection) {
        super(projection);
    }

    public UpdateNodeUnionResponseProjection(List<UpdateNodeUnionResponseProjection> projections) {
        super(projections);
    }

    public UpdateNodeUnionResponseProjection onIssue(IssueResponseProjection subProjection) {
        return onIssue(null, subProjection);
    }

    public UpdateNodeUnionResponseProjection onIssue(String alias, IssueResponseProjection subProjection) {
        add$(new GraphQLResponseField("...on Issue").alias(alias).projection(subProjection));
        return this;
    }

    public UpdateNodeUnionResponseProjection onOrganization(OrganizationResponseProjection subProjection) {
        return onOrganization(null, subProjection);
    }

    public UpdateNodeUnionResponseProjection onOrganization(String alias,
                                                            OrganizationResponseProjection subProjection) {
        add$(new GraphQLResponseField("...on Organization").alias(alias).projection(subProjection));
        return this;
    }


    public UpdateNodeUnionResponseProjection typename() {
        return typename(null);
    }

    public UpdateNodeUnionResponseProjection typename(String alias) {
        add$(new GraphQLResponseField("__typename").alias(alias));
        return this;
    }

    public GraphQLResponseProjection all$() {
        return null;
    }

    public GraphQLResponseProjection all$(int maxDepth) {
        return null;
    }

    @Override
    public UpdateNodeUnionResponseProjection deepCopy$() {
        return new UpdateNodeUnionResponseProjection(this);
    }

}
