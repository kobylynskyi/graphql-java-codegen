package com.kobylynskyi.graphql.codegen.model.request;

import com.kobylynskyi.graphql.codegen.model.request.data.*;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GraphQLRequestSerializerTest {

    @Test
    void serialize_Null() {
        assertNull(GraphQLRequestSerializer.serialize(null));
    }

    @Test
    void serialize_Empty() {
        assertNull(GraphQLRequestSerializer.serialize(new GraphQLRequest(null)));
    }

    @Test
    void serialize_noResponseProjection() throws IOException {
        String fileContent = getExpectedQueryString("versionQuery.txt");
        GraphQLRequest graphQLRequest = new GraphQLRequest(new VersionQueryRequest());
        String serializedQuery = graphQLRequest.toString().replaceAll(" +", " ").trim();
        assertEquals(fileContent, serializedQuery);
    }

    @Test
    void serialize_withResponseProjection() throws IOException {
        String fileContent = getExpectedQueryString("eventsByCategoryAndStatusQuery.txt");
        EventsByCategoryAndStatusQueryRequest request = new EventsByCategoryAndStatusQueryRequest();
        request.setCategoryId("categoryIdValue1");
        request.setStatus(Status.OPEN);
        GraphQLRequest graphQLRequest = new GraphQLRequest(request,
                new EventResponseProjection()
                        .id()
                        .active()
                        .properties(new EventPropertyResponseProjection()
                                .floatVal()
                                .child(new EventPropertyResponseProjection()
                                        .intVal()
                                        .parent(new EventResponseProjection()
                                                .id()))
                                .booleanVal())
                        .status()
        );
        String serializedQuery = graphQLRequest.toString().replaceAll(" +", " ").trim();
        assertEquals(fileContent, serializedQuery);
    }

    @Test
    void serialize_complexRequestWithDefaultData() throws IOException {
        String fileContent = getExpectedQueryString("updateIssueMutation.txt");
        UpdateIssueMutationRequest requestWithDefaultData = new UpdateIssueMutationRequest();
        requestWithDefaultData.setInput(new UpdateIssueInput());
        GraphQLRequest graphQLRequest = new GraphQLRequest(requestWithDefaultData,
                new UpdateIssuePayloadResponseProjection()
                        .clientMutationId()
                        .issue(new IssueResponseProjection()
                                .activeLockReason())
        );
        String serializedQuery = graphQLRequest.toString().replaceAll(" +", " ").trim();
        assertEquals(fileContent, serializedQuery);
    }

    @Test
    void serialize_collectionRequest() throws IOException {
        String fileContent = getExpectedQueryString("eventsByIdsQuery.txt");
        EventsByIdsQueryRequest request = new EventsByIdsQueryRequest();
        request.setIds(Arrays.asList("4", "5", "6"));
        GraphQLRequest graphQLRequest = new GraphQLRequest(request,
                new EventResponseProjection()
                        .id()
        );
        String serializedQuery = graphQLRequest.toString().replaceAll(" +", " ").trim();
        assertEquals(fileContent, serializedQuery);
    }

    private static String getExpectedQueryString(final String fileName) throws IOException {
        String trimmedContent = Utils.getFileContent(
                new File("src/test/resources/expected-classes/request/graphql-query/" + fileName).getPath())
                .replaceAll(System.lineSeparator(), " ")
                .replaceAll(" +", " ").trim();
        return String.format("{\"query\":\"%s\"}", trimmedContent);
    }

}