package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.Comment;
import graphql.language.NamedNode;
import graphql.language.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Base class for all GraphQL definition types that contains base definition and its extensions
 *
 * @param <T> base type
 * @param <E> extension type
 */
@Getter
@Setter
public abstract class ExtendedDefinition<T extends NamedNode<T>, E extends T> {

    /**
     * Nullable because some schemas can have just "extends"
     */
    protected T definition;
    protected List<E> extensions = new ArrayList<>();

    public String getName() {
        if (definition != null) {
            return definition.getName();
        } else {
            return extensions.stream().map(NamedNode::getName).findFirst().orElse(null);
        }
    }

    public List<String> getJavaDoc() {
        List<String> comments = new ArrayList<>();
        if (definition != null && definition.getComments() != null) {
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
