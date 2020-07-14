package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.ImplementingTypeDefinition;
import graphql.language.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all GraphQL definition types that might implement interfaces
 *
 * @param <T> base type
 * @param <E> extension type
 */
public abstract class ExtendedImplementingTypeDefinition<T extends ImplementingTypeDefinition<T>, E extends T> extends ExtendedDefinition<T, E> {

    @SuppressWarnings({"rawtypes", "java:S3740"})
    public List<Type> getImplements() {
        List<Type> definitionImplements = new ArrayList<>();
        if (definition != null) {
            definitionImplements.addAll(definition.getImplements());
        }
        extensions.stream()
                .map(ImplementingTypeDefinition::getImplements)
                .forEach(definitionImplements::addAll);
        return definitionImplements;
    }

}
