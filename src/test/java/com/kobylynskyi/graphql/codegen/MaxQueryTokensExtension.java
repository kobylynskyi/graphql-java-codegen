package com.kobylynskyi.graphql.codegen;

import graphql.parser.ParserOptions;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * This extension is to increase the {@link ParserOptions#MAX_QUERY_TOKENS}} to 20_000 JVM wide
 * to allow successful test schema parsing
 */
public class MaxQueryTokensExtension implements BeforeAllCallback, AfterAllCallback {

    private static final ParserOptions defaultJvmParserOptions = ParserOptions.getDefaultParserOptions();

    @Override
    public void beforeAll(ExtensionContext context) {
        ParserOptions.setDefaultParserOptions(
                ParserOptions.getDefaultParserOptions().transform(o -> o.maxTokens(20_000))
        );
    }

    @Override
    public void afterAll(ExtensionContext context) {
        ParserOptions.setDefaultParserOptions(defaultJvmParserOptions);
    }
}
