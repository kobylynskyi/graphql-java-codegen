package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.Comment;
import graphql.language.Directive;
import graphql.language.FieldDefinition;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Used to identify whether the field definition comes from the extension or base definition
 */
public class ExtendedFieldDefinition extends FieldDefinition {

    private final boolean fromExtension;

    protected ExtendedFieldDefinition(FieldDefinition f, boolean fromExtension) {
        super(f.getName(), f.getType(), f.getInputValueDefinitions(), f.getDirectives(),
                f.getDescription(), f.getSourceLocation(), f.getComments(), f.getIgnoredChars(),
                f.getAdditionalData());
        this.fromExtension = fromExtension;
    }

    public boolean isDeprecated() {
        return getDirectives().stream()
                .map(Directive::getName)
                .anyMatch(Deprecated.class.getSimpleName()::equalsIgnoreCase);
    }

    public List<String> getJavaDoc() {
        if (getComments() == null) {
            return Collections.emptyList();
        }
        return getComments().stream()
                .map(Comment::getContent)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public boolean isFromExtension() {
        return fromExtension;
    }
}
