package com.kobylynskyi.graphql.codegen.model.exception;

import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;

public class LanguageNotSupportedException extends IllegalArgumentException {

    public LanguageNotSupportedException(GeneratedLanguage generatedLanguage) {
        super("Language is not supported: " + generatedLanguage.name());
    }
}
