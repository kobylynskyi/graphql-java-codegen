package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.*;
import lombok.Getter;

/**
 * Used to identify whether the field definition comes from the extension or base definition
 */
@Getter
public class FieldDefinitionFromExtension extends FieldDefinition {

    private final boolean fromExtension;

    protected FieldDefinitionFromExtension(FieldDefinition f, boolean fromExtension) {
        super(f.getName(), f.getType(), f.getInputValueDefinitions(), f.getDirectives(),
                f.getDescription(), f.getSourceLocation(), f.getComments(), f.getIgnoredChars(),
                f.getAdditionalData());
        this.fromExtension = fromExtension;
    }

}
