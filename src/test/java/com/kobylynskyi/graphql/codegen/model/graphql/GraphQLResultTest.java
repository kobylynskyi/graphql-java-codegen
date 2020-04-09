package com.kobylynskyi.graphql.codegen.model.graphql;

import com.kobylynskyi.graphql.codegen.model.graphql.data.UpdateIssueInput;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

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
                singletonList(new GraphQLError())).hasErrors());
    }

    @Test
    void someErrors2() {
        GraphQLResult<UpdateIssueInput> result = new GraphQLResult<>();
        result.setData(new UpdateIssueInput());
        result.setErrors(singletonList(getGraphQLErrorO()));
        assertNotNull(result.getData());
        assertEquals(1, result.getErrors().size());
        assertEquals(getGraphQLErrorO(), result.getErrors().get(0));
    }

    private static GraphQLError getGraphQLErrorO() {
        GraphQLError graphQLError = new GraphQLError();
        graphQLError.setErrorType(GraphQLErrorType.ValidationError);
        graphQLError.setExtensions(new HashMap<>());
        graphQLError.setLocations(singletonList(new GraphQLErrorSourceLocation(1, 2, "3")));
        graphQLError.setMessage("something went wrong");
        graphQLError.setPath(singletonList("/order/items[0]/product"));
        return graphQLError;
    }

}