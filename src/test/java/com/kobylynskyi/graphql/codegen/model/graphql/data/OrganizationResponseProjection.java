package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

import java.util.List;

public class OrganizationResponseProjection extends GraphQLResponseProjection {

    public OrganizationResponseProjection() {
    }

    public OrganizationResponseProjection(OrganizationResponseProjection projection) {
        super(projection);
    }

    public OrganizationResponseProjection(List<OrganizationResponseProjection> projections) {
        super(projections);
    }

    public OrganizationResponseProjection name() {
        return name(null);
    }

    public OrganizationResponseProjection name(String alias) {
        add$(new GraphQLResponseField("name").alias(alias));
        return this;
    }

    public GraphQLResponseProjection all$() {
        return null;
    }

    public GraphQLResponseProjection all$(int maxDepth) {
        return null;
    }

    @Override
    public GraphQLResponseProjection deepCopy$() {
        return new OrganizationResponseProjection(this);
    }

    // REST OF THE STUFF WAS REMOVED

}
