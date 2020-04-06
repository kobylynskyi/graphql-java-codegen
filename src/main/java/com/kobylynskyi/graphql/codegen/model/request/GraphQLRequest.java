package com.kobylynskyi.graphql.codegen.model.request;

public class GraphQLRequest {

    private final GraphQLOperationRequest request;
    private final GraphQLResponseProjection responseProjection;

    public GraphQLRequest(GraphQLOperationRequest request) {
        this(request, null);
    }

    public GraphQLRequest(GraphQLOperationRequest request, GraphQLResponseProjection responseProjection) {
        this.request = request;
        this.responseProjection = responseProjection;
    }

    public GraphQLOperationRequest getRequest() {
        return request;
    }

    public GraphQLResponseProjection getResponseProjection() {
        return responseProjection;
    }

    @Override
    public String toString() {
        return GraphQLRequestSerializer.serialize(this);
    }
}
