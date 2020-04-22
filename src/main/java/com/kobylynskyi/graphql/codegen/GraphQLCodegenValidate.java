package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.SchemaValidationException;
import graphql.GraphQLException;
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
        for (String schema : schemas) {
            try {
                long startTime = System.currentTimeMillis();
                GraphQLDocumentParser.getDocument(schema);
                System.out.println(String.format("Validated schema '%s' in %d ms",
                        schema, System.currentTimeMillis() - startTime));
            } catch (GraphQLException e) {
                throw new SchemaValidationException(e.getMessage());
            }
        }
    }

}
