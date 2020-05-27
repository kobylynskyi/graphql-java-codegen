package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

public class IssueResponseProjection extends GraphQLResponseProjection {

    public IssueResponseProjection() {
    }

    public IssueResponseProjection activeLockReason() {
        fields.put("activeLockReason", null);
        return this;
    }

    // REST OF THE STUFF WAS REMOVED

}
