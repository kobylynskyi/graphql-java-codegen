package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.*;
import java.util.function.Function;

/**
 * Base class for all GraphQL definition types that contains base definition and its extensions
 *
 * @param <T> base type
 * @param <E> extension type
 */
@Getter
@Setter
public abstract class ExtendedDefinition<T extends NamedNode<T>, E extends T> {

    @NonNull
    protected T definition;
    protected List<E> extensions = new ArrayList<>();

    public String getName() {
        return definition.getName();
    }

    public List<String> getJavaDoc() {
        List<String> comments = new ArrayList<>();
        if (definition.getComments() != null) {
            definition.getComments().stream()
                    .map(Comment::getContent)
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(comments::add);
        }
        extensions.stream()
                .map(Node::getComments)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(Comment::getContent)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(comments::add);
        return comments;
    }

}
