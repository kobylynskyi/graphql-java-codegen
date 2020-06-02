package com.kobylynskyi.graphql.codegen.model.graphql;

import com.kobylynskyi.graphql.codegen.model.graphql.data.UpdateIssueInput;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphQLResultTest {

    @Test
    void noErrorsEmpty() {
        assertFalse(new GraphQLResult<>(new UpdateIssueInput(), emptyList()).hasErrors());
    }

    @Test
    void noErrorsNull() {
        assertFalse(new GraphQLResult<>().hasErrors());
    }

    @Test
    void someErrors1() {
        assertTrue(new GraphQLResult<>(new UpdateIssueInput(),
                singletonList(getGraphQLErrorO())).hasErrors());
    }

    @Test
    void someErrors2() {
        GraphQLResult<UpdateIssueInput> result = new GraphQLResult<>();
        result.setData(new UpdateIssueInput());
        result.setErrors(singletonList(getGraphQLError1()));
        assertNotNull(result.getData());
        assertEquals(1, result.getErrors().size());
        assertEquals(getGraphQLError1(), result.getErrors().get(0));
    }

    @Test
    void checkGetters() {
        GraphQLError graphQLError = getGraphQLErrorO();
        assertEquals(GraphQLErrorType.ValidationError, graphQLError.getErrorType());
        assertEquals(4, graphQLError.getLocations().get(0).getColumn());
        assertEquals(5, graphQLError.getLocations().get(0).getLine());
        assertEquals("6", graphQLError.getLocations().get(0).getSourceName());
        assertEquals("something went wrong", graphQLError.getMessage());
        assertEquals(singletonList("/order/items[0]/product"), graphQLError.getPath());
        assertEquals("extValue", graphQLError.getExtensions().get("extKey").toString());
    }

    private static GraphQLError getGraphQLErrorO() {
        GraphQLErrorSourceLocation sourceLocation = new GraphQLErrorSourceLocation();
        sourceLocation.setColumn(4);
        sourceLocation.setLine(5);
        sourceLocation.setSourceName("6");
        GraphQLError graphQLError = new GraphQLError();
        graphQLError.setErrorType(GraphQLErrorType.ValidationError);
        graphQLError.setExtensions(singletonMap("extKey", "extValue"));
        graphQLError.setLocations(singletonList(sourceLocation));
        graphQLError.setMessage("something went wrong");
        graphQLError.setPath(singletonList("/order/items[0]/product"));
        return graphQLError;
    }

    private static GraphQLError getGraphQLError1() {
        return new GraphQLError("something went very wrong",
                singletonList(new GraphQLErrorSourceLocation(1, 2, "3")),
                GraphQLErrorType.ExecutionAborted,
                singletonList("/order/items[1]/product"),
                singletonMap("extKey1", "extValue1"));
    }

}