package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.InterfaceTypeDefinition;
import graphql.language.InterfaceTypeExtensionDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExtendedInterfaceTypeDefinition extends ExtendedImplementingTypeDefinition<InterfaceTypeDefinition, InterfaceTypeExtensionDefinition> {

    public List<ExtendedFieldDefinition> getFieldDefinitions() {
        List<ExtendedFieldDefinition> definitions = new ArrayList<>();
        if (definition != null) {
            definition.getFieldDefinitions().stream()
                    .map(f -> new ExtendedFieldDefinition(f, false))
                    .forEach(definitions::add);
        }
        extensions.stream()
                .map(InterfaceTypeExtensionDefinition::getFieldDefinitions)
                .flatMap(Collection::stream)
                .map(f -> new ExtendedFieldDefinition(f, true))
                .forEach(definitions::add);
        return definitions;
    }

}
