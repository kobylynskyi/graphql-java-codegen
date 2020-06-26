package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.exception.SchemaValidationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GraphQLCodegenValidateTest {

    @Test
    void validate_Invalid() {
        GraphQLCodegenValidate graphQLCodegenValidate = new GraphQLCodegenValidate(singletonList("src/test/resources/schemas/invalid.graphqls"));
        assertThrows(SchemaValidationException.class, graphQLCodegenValidate::validate);
    }

    @Test
    void validate_Valid() throws IOException {
        new GraphQLCodegenValidate(singletonList("src/test/resources/schemas/test.graphqls"))
                .validate();
    }

    @Test
    void validate_Mixed() {
        GraphQLCodegenValidate graphQLCodegenValidate = new GraphQLCodegenValidate(Arrays.asList(
                "src/test/resources/schemas/test.graphqls",
                "src/test/resources/schemas/invalid.graphqls"));
        assertThrows(SchemaValidationException.class, graphQLCodegenValidate::validate);
    }

}