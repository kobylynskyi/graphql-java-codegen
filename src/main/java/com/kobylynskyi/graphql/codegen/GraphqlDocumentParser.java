package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Document;
import graphql.parser.MultiSourceReader;
import graphql.parser.Parser;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

class GraphqlDocumentParser {

    private static final Parser GRAPHQL_PARSER = new Parser();

    static Document getDocument(String schemaFilePath) throws IOException {
        return getDocument(Collections.singletonList(schemaFilePath));
    }

    static Document getDocument(List<String> schemaPaths) throws IOException {
        MultiSourceReader reader = createMultiSourceReader(schemaPaths);
        return GRAPHQL_PARSER.parseDocument(reader);
    }

    private static MultiSourceReader createMultiSourceReader(List<String> schemaPaths) throws IOException {
        MultiSourceReader.Builder builder = MultiSourceReader.newMultiSourceReader();
        for (String path : schemaPaths) {
            // appending EOL to ensure that schema tokens are not mixed in case files are not properly ended with EOL
            String content = Utils.getFileContent(path) + System.lineSeparator();
            builder.string(content, path);
        }
        return builder.trackData(true).build();
    }
}
