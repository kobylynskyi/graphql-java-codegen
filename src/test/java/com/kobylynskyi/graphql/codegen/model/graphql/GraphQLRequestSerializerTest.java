package com.kobylynskyi.graphql.codegen.model.graphql;

import com.kobylynskyi.graphql.codegen.model.graphql.data.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GraphQLRequestSerializerTest {

    private static Stream<Arguments> provideStaticSerializers() {
        return Stream.of(
          Arguments.of(
                  "GraphQLRequestSerializer.serialize()",
                  (Function<GraphQLRequest, String>) (request) -> GraphQLRequestSerializer.serialize(request),
                  (Function<String, String>) (query) -> jsonQuery(query)
            ),
          Arguments.of(
                  "GraphQLRequestSerializer.toHttpJsonBody(request)",
                  (Function<GraphQLRequest, String>) (request) -> GraphQLRequestSerializer.toHttpJsonBody(request),
                  (Function<String, String>) (query) -> jsonQuery(query)
            ),
          Arguments.of(
                  "GraphQLRequestSerializer.toQueryString(request)",
                  (Function<GraphQLRequest, String>) (request) -> GraphQLRequestSerializer.toQueryString(request),
                  (Function<String, String>) (query) -> query
            )
        );
    }

    private static Stream<Arguments> provideAllSerializers() {
        return Stream.concat(provideStaticSerializers(), Stream.of(
                Arguments.of(
                        "request.toHttpJsonBody()",
                        (Function<GraphQLRequest, String>) (request) -> request.toHttpJsonBody(),
                        (Function<String, String>) (query) -> jsonQuery(query)
                  ),
                Arguments.of(
                        "request.toQueryString()",
                        (Function<GraphQLRequest, String>) (request) -> request.toQueryString(),
                        (Function<String, String>) (query) -> query
                  )
                )
              );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideStaticSerializers")
    void serialize_Null(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
        assertNull(serializer.apply(null));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAllSerializers")
    void serialize_Empty(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
        assertNull(serializer.apply(new GraphQLRequest(null)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAllSerializers")
    void serialize_noResponseProjection(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
        GraphQLRequest graphQLRequest = new GraphQLRequest(new VersionQueryRequest());
        String serializedQuery = serializer.apply(graphQLRequest).replaceAll(" +", " ").trim();;
        String expectedQueryStr = "query { version }";
        assertEquals(expectedQueryDecorator.apply(expectedQueryStr), serializedQuery);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAllSerializers")
    void serialize_withResponseProjection(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
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
        String serializedQuery = serializer.apply(graphQLRequest).replaceAll(" +", " ").trim();
        String expectedQueryStr = "query { " +
                "eventsByCategoryAndStatus(categoryId: \"categoryIdValue1\", status: OPEN){ " +
                "id " +
                "active " +
                "properties { " +
                "floatVal child { intVal parent { id } } booleanVal } " +
                "status " +
                "} " +
                "}";
        assertEquals(expectedQueryDecorator.apply(expectedQueryStr), serializedQuery);
    }
    
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAllSerializers")
    void serialize_withResponseProjectionAndParametrizedInput(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
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
                                .child(new EventPropertyChildParametrizedInput()
                                                .last(-10)
                                                .first(+10),
                                        new EventPropertyResponseProjection()
                                                .intVal()
                                                .parent(new EventPropertyParentParametrizedInput()
                                                                .withStatus(Status.OPEN),
                                                        new EventResponseProjection()
                                                                .id()))
                                .booleanVal())
                        .status()
        );
        String serializedQuery = serializer.apply(graphQLRequest).replaceAll(" +", " ").trim();
        String expectedQueryStr = "query { " +
                "eventsByCategoryAndStatus(categoryId: \"categoryIdValue1\", status: OPEN){ " +
                "id " +
                "active " +
                "properties { " +
                "floatVal child (first: 10, last: -10) { intVal parent (withStatus: OPEN) { id } } booleanVal } " +
                "status " +
                "} " +
                "}";
        System.out.println(expectedQueryDecorator.apply(expectedQueryStr));
        System.out.println(serializedQuery);
        assertEquals(expectedQueryDecorator.apply(expectedQueryStr), serializedQuery);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAllSerializers")
    void serialize_complexRequestWithDefaultData(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
        UpdateIssueMutationRequest requestWithDefaultData = new UpdateIssueMutationRequest();
        requestWithDefaultData.setInput(new UpdateIssueInput());
        GraphQLRequest graphQLRequest = new GraphQLRequest(requestWithDefaultData,
                new UpdateIssuePayloadResponseProjection()
                        .clientMutationId()
                        .issue(new IssueResponseProjection()
                                .activeLockReason())
        );
        String serializedQuery = serializer.apply(graphQLRequest).replaceAll(" +", " ").trim();
        String expectedQueryStr = "mutation { updateIssue(input: { " +
                "floatVal: 1.23, booleanVal: false, intVal: 42, " +
                "stringVal: \"default \\\" \\\\ \\b \\f \\n \\r \\t ሴ \", " +
                "enumVal: OPEN, intList: [ 1, 2, 3 ], intListEmptyDefault: [ ] }){ " +
                "clientMutationId issue { activeLockReason } } }";
        assertEquals(expectedQueryDecorator.apply(expectedQueryStr), serializedQuery);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAllSerializers")
    void serialize_complexRequest(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
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
        String serializedQuery = serializer.apply(graphQLRequest).replaceAll(" +", " ").trim();
        String expectedQueryStr = "mutation { updateIssue(input: { " +
                "floatVal: 1.23, booleanVal: false, intVal: 42, " +
                "stringVal: \"default \\\" \\\\ \\b \\f \\n \\r \\t ሴ \", " +
                "enumVal: OPEN, intList: [ 1, 2, 3 ], intListEmptyDefault: [ \"\", \"1\", null, \"\\\"\" ] }){ " +
                "clientMutationId issue { activeLockReason } } }";
        assertEquals(expectedQueryDecorator.apply(expectedQueryStr), serializedQuery);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAllSerializers")
    void serialize_collectionRequest(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
        EventsByIdsQueryRequest request = new EventsByIdsQueryRequest.Builder()
                .setIds(Arrays.asList("\"", "\\", "\b", "\f", "\n", "\r", "\t", "\u1234"))
                .build();
        GraphQLRequest graphQLRequest = new GraphQLRequest(request,
                new EventResponseProjection()
                        .id()
        );
        String serializedQuery = serializer.apply(graphQLRequest).replaceAll(" +", " ").trim();
        String expectedQueryStr = "query { eventsByIds(ids: [ " +
                "\"\\\"\", " +
                "\"\\\\\", " +
                "\"\\b\", " +
                "\"\\f\", " +
                "\"\\n\", " +
                "\"\\r\", " +
                "\"\\t\", " +
                                            "\"ሴ\" ]){ id } }";
        assertEquals(expectedQueryDecorator.apply(expectedQueryStr), serializedQuery);
    }

    private static String jsonQuery(String expectedQueryDecorator) {
        return String.format("{\"query\":\"%s\"}", expectedQueryDecorator.replace("\\", "\\\\").replace("\"", "\\\""));
    }

}