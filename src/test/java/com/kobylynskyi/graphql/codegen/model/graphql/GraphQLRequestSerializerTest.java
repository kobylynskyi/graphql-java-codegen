package com.kobylynskyi.graphql.codegen.model.graphql;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kobylynskyi.graphql.codegen.model.graphql.data.DateInput;
import com.kobylynskyi.graphql.codegen.model.graphql.data.EventPropertyChildParametrizedInput;
import com.kobylynskyi.graphql.codegen.model.graphql.data.EventPropertyParentParametrizedInput;
import com.kobylynskyi.graphql.codegen.model.graphql.data.EventPropertyResponseProjection;
import com.kobylynskyi.graphql.codegen.model.graphql.data.EventResponseProjection;
import com.kobylynskyi.graphql.codegen.model.graphql.data.EventsByCategoryAndStatusQueryRequest;
import com.kobylynskyi.graphql.codegen.model.graphql.data.EventsByIdsQueryRequest;
import com.kobylynskyi.graphql.codegen.model.graphql.data.IssueResponseProjection;
import com.kobylynskyi.graphql.codegen.model.graphql.data.OrganizationResponseProjection;
import com.kobylynskyi.graphql.codegen.model.graphql.data.Status;
import com.kobylynskyi.graphql.codegen.model.graphql.data.UpdateDate2MutationRequest;
import com.kobylynskyi.graphql.codegen.model.graphql.data.UpdateDateMutationRequest;
import com.kobylynskyi.graphql.codegen.model.graphql.data.UpdateIssueInput;
import com.kobylynskyi.graphql.codegen.model.graphql.data.UpdateIssueMutationRequest;
import com.kobylynskyi.graphql.codegen.model.graphql.data.UpdateIssuePayloadResponseProjection;
import com.kobylynskyi.graphql.codegen.model.graphql.data.UpdateNodeUnionResponseProjection;
import com.kobylynskyi.graphql.codegen.model.graphql.data.VersionQueryRequest;
import com.kobylynskyi.graphql.codegen.model.graphql.data.ZonedDateTimeSerializer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GraphQLRequestSerializerTest {

    private static Stream<Arguments> provideStaticSerializers() {
        return Stream.of(
                Arguments.of(
                        "GraphQLRequestSerializer.toHttpJsonBody(request)",
                        (Function<GraphQLRequest, String>) GraphQLRequestSerializer::toHttpJsonBody,
                        (Function<String, String>) GraphQLRequestSerializerTest::jsonQuery),
                Arguments.of(
                        "GraphQLRequestSerializer.toQueryString(request)",
                        (Function<GraphQLRequest, String>) GraphQLRequestSerializer::toQueryString,
                        (Function<String, String>) (query) -> query));
    }

    private static Stream<Arguments> provideAllSerializers() {
        return Stream.concat(provideStaticSerializers(), Stream.of(
                Arguments.of(
                        "request.toHttpJsonBody()",
                        (Function<GraphQLRequest, String>) GraphQLRequest::toHttpJsonBody,
                        (Function<String, String>) GraphQLRequestSerializerTest::jsonQuery),
                Arguments.of(
                        "request.toQueryString()",
                        (Function<GraphQLRequest, String>) GraphQLRequest::toQueryString,
                        (Function<String, String>) (query) -> query)));
    }

    private static Stream<Arguments> provideStaticSerializerForMultiRequest() {
        return Stream.of(Arguments.of(
                "GraphQLRequestSerializer.toHttpJsonBody(request)",
                (Function<GraphQLRequests, String>) GraphQLRequestSerializer::toHttpJsonBody,
                (Function<String, String>) GraphQLRequestSerializerTest::jsonQuery));
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
        String serializedQuery = serializer.apply(graphQLRequest).replaceAll(" +", " ").trim();
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
    void serialize_withResponseProjectionAndAlias(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
        EventsByCategoryAndStatusQueryRequest request = new EventsByCategoryAndStatusQueryRequest.Builder()
                .setCategoryId("categoryIdValue1")
                .setStatus(Status.OPEN)
                .build();
        GraphQLRequest graphQLRequest = new GraphQLRequest(request,
                new EventResponseProjection()
                        .id("myId")
                        .active("myActive")
                        .properties("myProps", new EventPropertyResponseProjection()
                                .floatVal("myFloatVal")
                                .child("myChild", new EventPropertyResponseProjection()
                                        .intVal("myIntVal")
                                        .parent("myParent", new EventResponseProjection()
                                                .id("myId")))
                                .booleanVal("myBooleanVal"))
                        .status("myStatus")
        );
        String serializedQuery = serializer.apply(graphQLRequest).replaceAll(" +", " ").trim();
        String expectedQueryStr = "query { " +
                "eventsByCategoryAndStatus(categoryId: \"categoryIdValue1\", status: OPEN){ " +
                "myId : id " +
                "myActive : active " +
                "myProps : properties { " +
                "myFloatVal : floatVal myChild : child { myIntVal : intVal myParent : parent { myId : id } } myBooleanVal : booleanVal } " +
                "myStatus : status " +
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
        assertEquals(expectedQueryDecorator.apply(expectedQueryStr), serializedQuery);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAllSerializers")
    void serialize_withResponseProjectionAndParametrizedInputAndAlias(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
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
                                .child("myChild", new EventPropertyChildParametrizedInput()
                                                .last(-10)
                                                .first(+10),
                                        new EventPropertyResponseProjection()
                                                .intVal()
                                                .parent("myParent", new EventPropertyParentParametrizedInput()
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
                "floatVal myChild : child (first: 10, last: -10) { intVal myParent : parent (withStatus: OPEN) { id } } booleanVal } " +
                "status " +
                "} " +
                "}";
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
    void serialize_UseObjectMapperForSomeFields(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
        GraphQLRequestSerializer.OBJECT_MAPPER.registerModule(
                new SimpleModule().addSerializer(new ZonedDateTimeSerializer()));

        UpdateDateMutationRequest updateDateMutationRequest = new UpdateDateMutationRequest();
        DateInput input = new DateInput();
        input.setDateTime(ZonedDateTime.parse("2020-07-30T22:17:17.884-05:00[America/Chicago]"));
        updateDateMutationRequest.setInput(input);
        GraphQLRequest graphQLRequest = new GraphQLRequest(updateDateMutationRequest);

        String serializedQuery = serializer.apply(graphQLRequest).replaceAll(" +", " ").trim();
        String expectedQueryStr = "mutation { updateDate(input: { " +
                "dateTime: \"2020-07-31T03:17:17.884Z\" }) }";
        assertEquals(expectedQueryDecorator.apply(expectedQueryStr), serializedQuery);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAllSerializers")
    void serialize_UseObjectMapperForQueryParameter(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
        GraphQLRequestSerializer.OBJECT_MAPPER.registerModule(
                new SimpleModule().addSerializer(new ZonedDateTimeSerializer()));

        UpdateDate2MutationRequest updateDateMutationRequest = new UpdateDate2MutationRequest();
        updateDateMutationRequest.setInput(ZonedDateTime.parse("2020-07-30T22:17:17.884-05:00[America/Chicago]"));
        GraphQLRequest graphQLRequest = new GraphQLRequest(updateDateMutationRequest);

        String serializedQuery = serializer.apply(graphQLRequest).replaceAll(" +", " ").trim();
        String expectedQueryStr = "mutation { updateDate(input: \"2020-07-31T03:17:17.884Z\") }";
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
    void serialize_withConditionalFragments(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
        GraphQLRequest graphQLRequest = new GraphQLRequest(new UpdateIssueMutationRequest(),
                new UpdateIssuePayloadResponseProjection()
                        .union(new UpdateNodeUnionResponseProjection()
                                .onIssue(new IssueResponseProjection()
                                        .activeLockReason())
                                .onOrganization(new OrganizationResponseProjection()
                                        .name())
                                .typename()));
        String serializedQuery = serializer.apply(graphQLRequest).replaceAll(" +", " ").trim();
        String expectedQueryStr = "mutation { updateIssue{ " +
                "union { ...on Issue { activeLockReason } ...on Organization { name } __typename } } }";
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAllSerializers")
    void serialize_collectionRequest_Null(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
        EventsByIdsQueryRequest request = new EventsByIdsQueryRequest.Builder()
                .setContextId("something")
                .setIds(null)
                .setTranslated(false)
                .build();
        GraphQLRequest graphQLRequest = new GraphQLRequest(request,
                new EventResponseProjection()
                        .id()
        );
        String serializedQuery = serializer.apply(graphQLRequest).replaceAll(" +", " ").trim();
        String expectedQueryStr = "query { eventsByIds(contextId: \"something\", translated: false){ id } }";
        assertEquals(expectedQueryDecorator.apply(expectedQueryStr), serializedQuery);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAllSerializers")
    void serialize_AllInputsNull(String name, Function<GraphQLRequest, String> serializer, Function<String, String> expectedQueryDecorator) {
        EventsByIdsQueryRequest request = new EventsByIdsQueryRequest.Builder()
                .setContextId(null)
                .setIds(null)
                .setTranslated(null)
                .build();
        GraphQLRequest graphQLRequest = new GraphQLRequest(request,
                new EventResponseProjection()
                        .id()
        );
        String serializedQuery = serializer.apply(graphQLRequest).replaceAll(" +", " ").trim();
        String expectedQueryStr = "query { eventsByIds{ id } }";
        assertEquals(expectedQueryDecorator.apply(expectedQueryStr), serializedQuery);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideStaticSerializerForMultiRequest")
    void serialize_multipleRequests(String name, Function<GraphQLRequests, String> serializer, Function<String, String> expectedQueryDecorator) {
        EventsByCategoryAndStatusQueryRequest request1 = new EventsByCategoryAndStatusQueryRequest.Builder().alias("req1").setStatus(Status.OPEN).build();
        GraphQLRequest graphQLRequest1 = new GraphQLRequest(request1, new EventResponseProjection().id());

        EventsByCategoryAndStatusQueryRequest request2 = new EventsByCategoryAndStatusQueryRequest("req2");
        GraphQLRequest graphQLRequest2 = new GraphQLRequest(request2, new EventResponseProjection().id().status());

        EventsByCategoryAndStatusQueryRequest request21 = new EventsByCategoryAndStatusQueryRequest();
        GraphQLRequest graphQLRequest21 = new GraphQLRequest(request21);

        String serializedQuery = serializer.apply(new GraphQLRequests(graphQLRequest1, graphQLRequest2, graphQLRequest21)).replaceAll(" +", " ").trim();
        String expectedQueryStr = "query { " +
                "req1: eventsByCategoryAndStatus(status: OPEN){ id } " +
                "req2: eventsByCategoryAndStatus{ id status } " +
                "eventsByCategoryAndStatus " +
                "}";
        assertEquals(expectedQueryDecorator.apply(expectedQueryStr), serializedQuery);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideStaticSerializerForMultiRequest")
    void serialize_multipleRequests_DiffTypes(String name, Function<GraphQLRequests, String> serializer, Function<String, String> expectedQueryDecorator) {
        GraphQLRequests graphQLRequests = new GraphQLRequests(
                new GraphQLRequest(new EventsByCategoryAndStatusQueryRequest()),
                new GraphQLRequest(new UpdateIssueMutationRequest()));

        assertThrows(IllegalArgumentException.class, () -> serializer.apply(graphQLRequests));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideStaticSerializerForMultiRequest")
    void serialize_multipleRequests_NullRequest(String name, Function<GraphQLRequests, String> serializer, Function<String, String> expectedQueryDecorator) {
        GraphQLRequests graphQLRequests = new GraphQLRequests(
                new GraphQLRequest(new EventsByCategoryAndStatusQueryRequest()),
                null);

        assertThrows(IllegalArgumentException.class, () -> serializer.apply(graphQLRequests));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideStaticSerializerForMultiRequest")
    void serialize_multipleRequests_NullRequestRequest(String name, Function<GraphQLRequests, String> serializer, Function<String, String> expectedQueryDecorator) {
        GraphQLRequests graphQLRequests = new GraphQLRequests(
                new GraphQLRequest(new EventsByCategoryAndStatusQueryRequest()),
                new GraphQLRequest(null));

        assertThrows(IllegalArgumentException.class, () -> serializer.apply(graphQLRequests));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideStaticSerializerForMultiRequest")
    void serialize_multipleRequests_NoRequests(String name, Function<GraphQLRequests, String> serializer, Function<String, String> expectedQueryDecorator) {
        GraphQLRequests graphQLRequests = new GraphQLRequests();

        assertThrows(IllegalArgumentException.class, () -> serializer.apply(graphQLRequests));
    }

    private static String jsonQuery(String expectedQueryDecorator) {
        return String.format("{\"query\":\"%s\"}", expectedQueryDecorator.replace("\\", "\\\\").replace("\"", "\\\""));
    }

}