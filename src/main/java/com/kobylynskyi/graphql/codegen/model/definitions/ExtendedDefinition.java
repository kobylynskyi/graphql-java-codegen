package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.NamedNode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all GraphQL definition types that contains base definition and its extensions
 *
 * @param <T> base type
 * @param <E> extension type
 */
@Getter
@Setter
public abstract class ExtendedDefinition<T extends NamedNode<T>, E extends NamedNode<T>> {

    @NonNull
    protected T definition;
    protected List<E> extensions = new ArrayList<>();

    public String getName() {
        return definition.getName();
    }

}
