package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.InterfaceTypeDefinition;
import graphql.language.InterfaceTypeExtensionDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExtendedInterfaceTypeDefinition extends ExtendedDefinition<InterfaceTypeDefinition, InterfaceTypeExtensionDefinition> {

    public List<FieldDefinitionFromExtension> getFieldDefinitions() {
        List<FieldDefinitionFromExtension> definitions = new ArrayList<>();
        definition.getFieldDefinitions().stream()
                .map(f -> new FieldDefinitionFromExtension(f, false))
                .forEach(definitions::add);
        extensions.stream()
                .map(InterfaceTypeExtensionDefinition::getFieldDefinitions)
                .flatMap(Collection::stream)
                .map(f -> new FieldDefinitionFromExtension(f, true))
                .forEach(definitions::add);
        return definitions;
    }

}
