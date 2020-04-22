package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.FieldDefinition;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.InterfaceTypeExtensionDefinition;

import java.util.List;

public class ExtendedInterfaceTypeDefinition extends ExtendedDefinition<InterfaceTypeDefinition, InterfaceTypeExtensionDefinition> {

    public List<FieldDefinition> getFieldDefinitions() {
        List<FieldDefinition> definitions = definition.getFieldDefinitions();
        extensions.stream()
                .map(InterfaceTypeDefinition::getFieldDefinitions)
                .forEach(definitions::addAll);
        return definitions;
    }

}
