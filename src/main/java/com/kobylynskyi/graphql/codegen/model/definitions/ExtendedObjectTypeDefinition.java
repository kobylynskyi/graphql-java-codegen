package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExtendedObjectTypeDefinition extends ExtendedDefinition<ObjectTypeDefinition, ObjectTypeExtensionDefinition> {

    public List<ExtendedFieldDefinition> getFieldDefinitions() {
        List<ExtendedFieldDefinition> definitions = new ArrayList<>();
        definition.getFieldDefinitions().stream()
                .map(f -> new ExtendedFieldDefinition(f, false))
                .forEach(definitions::add);
        extensions.stream()
                .map(ObjectTypeExtensionDefinition::getFieldDefinitions)
                .flatMap(Collection::stream)
                .map(f -> new ExtendedFieldDefinition(f, true))
                .forEach(definitions::add);
        return definitions;
    }

    @SuppressWarnings("rawtypes")
    public List<Type> getImplements() {
        List<Type> definitionImplements = definition.getImplements();
        extensions.stream()
                .map(ObjectTypeDefinition::getImplements)
                .forEach(definitionImplements::addAll);
        return definitionImplements;
    }

}
