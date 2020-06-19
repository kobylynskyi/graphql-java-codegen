package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.exception.SchemaValidationException;
import graphql.GraphQLException;
import graphql.parser.MultiSourceReader;
import graphql.parser.Parser;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.List;

/**
 * Validator of GraphQL schemas
 *
 * @author kobylynskyi
 */
@AllArgsConstructor
public class GraphQLCodegenValidate {

    private final List<String> schemas;

    public void validate() throws IOException {
        try {
            long startTime = System.currentTimeMillis();
            MultiSourceReader reader = GraphQLDocumentParser.createMultiSourceReader(schemas);
            new Parser().parseDocument(reader);
            System.out.println(String.format("Validated schemas '%s' in %d ms",
                    schemas, System.currentTimeMillis() - startTime));
        } catch (GraphQLException e) {
            throw new SchemaValidationException(e.getMessage());
        }

    }

}
