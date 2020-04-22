package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.FieldDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.ObjectTypeExtensionDefinition;
import graphql.language.Type;

import java.util.List;

public class ExtendedObjectTypeDefinition extends ExtendedDefinition<ObjectTypeDefinition, ObjectTypeExtensionDefinition> {

    public List<FieldDefinition> getFieldDefinitions() {
        List<FieldDefinition> definitions = definition.getFieldDefinitions();
        extensions.stream()
                .map(ObjectTypeExtensionDefinition::getFieldDefinitions)
                .forEach(definitions::addAll);
        return definitions;
    }

    public List<Type> getImplements() {
        List<Type> definitionImplements = definition.getImplements();
        extensions.stream()
                .map(ObjectTypeDefinition::getImplements)
                .forEach(definitionImplements::addAll);
        return definitionImplements;
    }

}
