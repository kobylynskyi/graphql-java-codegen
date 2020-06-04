package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.SchemaValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static java.util.Collections.singletonList;

class GraphQLCodegenValidateTest {

    @Test
    void validate_Invalid() {
        Assertions.assertThrows(SchemaValidationException.class, () ->
                new GraphQLCodegenValidate(singletonList("src/test/resources/schemas/invalid.graphqls"))
                        .validate());
    }

    @Test
    void validate_Valid() throws IOException {
        new GraphQLCodegenValidate(singletonList("src/test/resources/schemas/test.graphqls"))
                .validate();
    }

    @Test
    void validate_Mixed() {
        Assertions.assertThrows(SchemaValidationException.class, () ->
                new GraphQLCodegenValidate(Arrays.asList(
                        "src/test/resources/schemas/test.graphqls",
                        "src/test/resources/schemas/invalid.graphqls"))
                        .validate());
    }

}