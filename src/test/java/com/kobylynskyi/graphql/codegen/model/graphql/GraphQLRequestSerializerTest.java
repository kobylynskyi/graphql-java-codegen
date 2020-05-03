package com.kobylynskyi.graphql.codegen.model.graphql;

import com.kobylynskyi.graphql.codegen.model.graphql.data.*;
import org.junit.jupiter.api.Test;

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
    void serialize_noResponseProjection() {
        GraphQLRequest graphQLRequest = new GraphQLRequest(new VersionQueryRequest());
        String serializedQuery = graphQLRequest.toString().replaceAll(" +", " ").trim();
        assertEquals(expected("query { version }"), serializedQuery);
    }

    @Test
    void serialize_withResponseProjection() {
        EventsByCategoryAndStatusQueryRequest request = new EventsByCategoryAndStatusQueryRequest.Builder()
                .setCategoryId("categoryIdValue1")
                .setStatus(Status.OPEN)
                .build();
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
        assertEquals(expected("query { " +
                "eventsByCategoryAndStatus(categoryId: \\\"categoryIdValue1\\\", status: OPEN){ " +
                "id " +
                "active " +
                "properties { " +
                "floatVal child { intVal parent { id } } booleanVal } " +
                "status " +
                "} " +
                "}"), serializedQuery);
    }

    @Test
    void serialize_complexRequestWithDefaultData() {
        UpdateIssueMutationRequest requestWithDefaultData = new UpdateIssueMutationRequest();
        requestWithDefaultData.setInput(new UpdateIssueInput());
        GraphQLRequest graphQLRequest = new GraphQLRequest(requestWithDefaultData,
                new UpdateIssuePayloadResponseProjection()
                        .clientMutationId()
                        .issue(new IssueResponseProjection()
                                .activeLockReason())
        );
        String serializedQuery = graphQLRequest.toString().replaceAll(" +", " ").trim();
        assertEquals(expected("mutation { updateIssue(input: { " +
                "floatVal: 1.23, booleanVal: false, intVal: 42, " +
                "stringVal: \\\"default \\\\\\\" \\\\\\\\ \\\\b \\\\f \\\\n \\\\r \\\\t ሴ \\\", " +
                "enumVal: OPEN, intList: [ 1, 2, 3 ], intListEmptyDefault: [ ] }){ " +
                "clientMutationId issue { activeLockReason } } }"), serializedQuery);
    }

    @Test
    void serialize_complexRequest() {
        UpdateIssueMutationRequest updateIssueMutationRequest = new UpdateIssueMutationRequest();
        UpdateIssueInput input = new UpdateIssueInput();
        input.setStringListEmptyDefault(Arrays.asList("", "1", null, "\""));
        updateIssueMutationRequest.setInput(input);
        GraphQLRequest graphQLRequest = new GraphQLRequest(updateIssueMutationRequest,
                new UpdateIssuePayloadResponseProjection()
                        .clientMutationId()
                        .issue(new IssueResponseProjection()
                                .activeLockReason())
        );
        String serializedQuery = graphQLRequest.toString().replaceAll(" +", " ").trim();
        assertEquals(expected("mutation { updateIssue(input: { " +
                "floatVal: 1.23, booleanVal: false, intVal: 42, " +
                "stringVal: \\\"default \\\\\\\" \\\\\\\\ \\\\b \\\\f \\\\n \\\\r \\\\t ሴ \\\", " +
                "enumVal: OPEN, intList: [ 1, 2, 3 ], intListEmptyDefault: [ \\\"\\\", \\\"1\\\", null, \\\"\\\\\\\"\\\" ] }){ " +
                "clientMutationId issue { activeLockReason } } }"), serializedQuery);
    }

    @Test
    void serialize_collectionRequest() {
        EventsByIdsQueryRequest request = new EventsByIdsQueryRequest.Builder()
                .setIds(Arrays.asList("\"", "\\", "\b", "\f", "\n", "\r", "\t", "\u1234"))
                .build();
        GraphQLRequest graphQLRequest = new GraphQLRequest(request,
                new EventResponseProjection()
                        .id()
        );
        String serializedQuery = graphQLRequest.toString().replaceAll(" +", " ").trim();
        assertEquals(expected("query { eventsByIds(ids: [ " +
                "\\\"\\\\\\\"\\\", " +
                "\\\"\\\\\\\\\\\", " +
                "\\\"\\\\b\\\", " +
                "\\\"\\\\f\\\", " +
                "\\\"\\\\n\\\", " +
                "\\\"\\\\r\\\", " +
                "\\\"\\\\t\\\", " +
                "\\\"ሴ\\\" ]){ id } }"), serializedQuery);
    }

    private static String expected(String expectedQuery) {
        return String.format("{\"query\":\"%s\"}", expectedQuery);
    }

}