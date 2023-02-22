package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

import java.util.List;

public class IssueResponseProjection extends GraphQLResponseProjection {

    public IssueResponseProjection() {
    }

    public IssueResponseProjection(IssueResponseProjection projection) {
        super(projection);
    }

    public IssueResponseProjection(List<IssueResponseProjection> projections) {
        super(projections);
    }

    public IssueResponseProjection activeLockReason() {
        return activeLockReason(null);
    }

    public IssueResponseProjection activeLockReason(String alias) {
        add$(new GraphQLResponseField("activeLockReason").alias(alias));
        return this;
    }

    public GraphQLResponseProjection all$() {
        return null;
    }

    public GraphQLResponseProjection all$(int maxDepth) {
        return null;
    }

    @Override
    public IssueResponseProjection deepCopy$() {
        return new IssueResponseProjection(this);
    }

    // REST OF THE STUFF WAS REMOVED

}
