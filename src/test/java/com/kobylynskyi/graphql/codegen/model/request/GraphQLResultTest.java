package com.kobylynskyi.graphql.codegen.model.request;

import com.kobylynskyi.graphql.codegen.model.request.data.UpdateIssueInput;
import graphql.schema.CoercingParseLiteralException;
import org.junit.jupiter.api.Test;

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
                singletonList(new CoercingParseLiteralException())).hasErrors());
    }

    @Test
    void someErrors2() {
        GraphQLResult<UpdateIssueInput> result = new GraphQLResult<>();
        result.setData(new UpdateIssueInput());
        result.setErrors(singletonList(new CoercingParseLiteralException()));
        assertNotNull(result.getData());
        assertEquals(1, result.getErrors().size());
    }

}