package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

public class IssueResponseProjection extends GraphQLResponseProjection {

    public IssueResponseProjection() {
    }

    public IssueResponseProjection activeLockReason() {
        return activeLockReason(null);
    }

    public IssueResponseProjection activeLockReason(String alias) {
        fields.add(new GraphQLResponseField("activeLockReason").alias(alias));
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
    // REST OF THE STUFF WAS REMOVED

}
