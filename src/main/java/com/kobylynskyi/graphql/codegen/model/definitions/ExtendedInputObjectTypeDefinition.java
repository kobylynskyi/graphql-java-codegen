package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputObjectTypeExtensionDefinition;
import graphql.language.InputValueDefinition;

import java.util.List;

public class ExtendedInputObjectTypeDefinition extends ExtendedDefinition<InputObjectTypeDefinition, InputObjectTypeExtensionDefinition> {

    public List<InputValueDefinition> getValueDefinitions() {
        List<InputValueDefinition> definitions = definition.getInputValueDefinitions();
        extensions.stream()
                .map(InputObjectTypeDefinition::getInputValueDefinitions)
                .forEach(definitions::addAll);
        return definitions;
    }
}
