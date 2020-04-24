package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.Comment;
import graphql.language.FieldDefinition;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Used to identify whether the field definition comes from the extension or base definition
 */
@Getter
public class ExtendedFieldDefinition extends FieldDefinition {

    private final boolean fromExtension;

    protected ExtendedFieldDefinition(FieldDefinition f, boolean fromExtension) {
        super(f.getName(), f.getType(), f.getInputValueDefinitions(), f.getDirectives(),
                f.getDescription(), f.getSourceLocation(), f.getComments(), f.getIgnoredChars(),
                f.getAdditionalData());
        this.fromExtension = fromExtension;
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

}
