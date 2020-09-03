package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

public class OrganizationResponseProjection extends GraphQLResponseProjection {

    public OrganizationResponseProjection() {
    }

    public OrganizationResponseProjection name() {
        return name(null);
    }

    public OrganizationResponseProjection name(String alias) {
        fields.add(new GraphQLResponseField("name").alias(alias));
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
