package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Document;
import graphql.parser.Parser;

import java.io.IOException;

class GraphqlDocumentParser {

    private static final Parser GRAPHQL_PARSER = new Parser();

    static Document getDocument(String schemaFilePath) throws IOException {
        String fileContent = Utils.getFileContent(schemaFilePath);
        return GRAPHQL_PARSER.parseDocument(fileContent);
    }

}
