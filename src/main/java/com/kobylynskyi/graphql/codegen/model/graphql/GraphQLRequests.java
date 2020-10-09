package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class which represents multiple GraphQL Requests
 */
public class GraphQLRequests {

    private final List<GraphQLRequest> requests = new ArrayList<>();

    public GraphQLRequests(GraphQLRequest... requests) {
        this.requests.addAll(Arrays.asList(requests));
    }

    public void addRequest(GraphQLRequest request) {
        this.requests.add(request);
    }

    public List<GraphQLRequest> getRequests() {
        return new ArrayList<>(requests);
    }

    /**
     * Serializes multiple GraphQL requests to be used as HTTP JSON body
     * according to https://graphql.org/learn/serving-over-http specifications
     *
     * @return the serialized request
     */
    public String toHttpJsonBody() {
        return GraphQLRequestSerializer.toHttpJsonBody(this);
    }

}
