package com.kobylynskyi.graphql.codegen.model.definitions;

import java.util.ArrayList;
import java.util.List;

import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputObjectTypeExtensionDefinition;
import graphql.language.InputValueDefinition;

public class ExtendedInputObjectTypeDefinition
    extends ExtendedDefinition<InputObjectTypeDefinition, InputObjectTypeExtensionDefinition> {

    public List<InputValueDefinition> getValueDefinitions() {
        List<InputValueDefinition> definitions = new ArrayList<>();
        if (definition != null) {
            definitions.addAll(definition.getInputValueDefinitions());
        }
        extensions.stream()
                  .map(InputObjectTypeDefinition::getInputValueDefinitions)
                  .forEach(definitions::addAll);
        return definitions;
    }
}
