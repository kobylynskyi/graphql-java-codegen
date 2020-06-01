package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.EnumTypeDefinition;
import graphql.language.EnumTypeExtensionDefinition;
import graphql.language.EnumValueDefinition;

import java.util.ArrayList;
import java.util.List;

public class ExtendedEnumTypeDefinition extends ExtendedDefinition<EnumTypeDefinition, EnumTypeExtensionDefinition> {

    /**
     * Get enum value definitions from the definition and its extensions
     *
     * @return list of all enum value definitions
     */
    public List<EnumValueDefinition> getValueDefinitions() {
        List<EnumValueDefinition> definitions = new ArrayList<>();
        if (definition != null) {
            definitions.addAll(definition.getEnumValueDefinitions());
        }
        extensions.stream()
                .map(EnumTypeExtensionDefinition::getEnumValueDefinitions)
                .forEach(definitions::addAll);
        return definitions;
    }
}
