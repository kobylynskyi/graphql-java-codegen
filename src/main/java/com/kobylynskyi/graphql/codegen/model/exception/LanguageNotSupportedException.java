package com.kobylynskyi.graphql.codegen.model.exception;

import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;

/**
 * Eception is thrown when specified language is not supportted by GraphQL Code generator
 */
public class LanguageNotSupportedException extends IllegalArgumentException {

    public LanguageNotSupportedException(GeneratedLanguage generatedLanguage) {
        super("Language is not supported: " + generatedLanguage.name());
    }
}
