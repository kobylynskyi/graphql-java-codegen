package com.kobylynskyi.graphql.codegen.model.graphql;

/**
 * Class which represents GraphQL Request
 */
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

    /**
     * @deprecated Not intended for use and will be removed in the next version.
     * Please use one of: {@link #toHttpJsonBody} or {@link #toQueryString}
     */
    @Override
    @Deprecated
    public String toString() {
        return toHttpJsonBody();
    }

    /**
     * Serializes GraphQL request to be used as HTTP JSON body
     * according to https://graphql.org/learn/serving-over-http specifications
     * 
     * @return the serialized request
     */
    public String toHttpJsonBody() {
        return GraphQLRequestSerializer.toHttpJsonBody(this);
    }

    /**
     * Serializes GraphQL request as raw query string
     * 
     * @return the serialized request
     */
    public String toQueryString() {
        return GraphQLRequestSerializer.toQueryString(this);
    }
}
