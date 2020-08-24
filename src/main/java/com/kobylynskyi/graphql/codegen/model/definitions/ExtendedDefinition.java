package com.kobylynskyi.graphql.codegen.model.definitions;

import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.AbstractDescribedNode;
import graphql.language.Comment;
import graphql.language.Description;
import graphql.language.Directive;
import graphql.language.DirectivesContainer;
import graphql.language.NamedNode;
import graphql.language.Node;
import graphql.language.SourceLocation;

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

    public SourceLocation getSourceLocation() {
        if (definition != null) {
            return definition.getSourceLocation();
        } else {
            return extensions.stream().map(Node::getSourceLocation).findFirst().orElse(null);
        }
    }

    public List<String> getJavaDoc() {
        List<String> javaDocFromDescription = getJavaDocFromDescription();
        if (javaDocFromDescription.isEmpty()) {
            return getJavaDocFromComments();
        }
        return javaDocFromDescription;
    }

    public List<String> getJavaDocFromDescription() {
        List<String> descriptions = new ArrayList<>();
        if (this.definition instanceof AbstractDescribedNode) {
            Description description = ((AbstractDescribedNode<?>) this.definition).getDescription();
            if (description != null && Utils.isNotBlank(description.getContent())) {
                descriptions.add(description.getContent().trim());
            }
            this.extensions.stream()
                    .filter(Objects::nonNull)
                    .map(AbstractDescribedNode.class::cast)
                    .map(AbstractDescribedNode::getDescription).filter(Objects::nonNull)
                    .map(Description::getContent).filter(Utils::isNotBlank)
                    .map(String::trim).forEach(descriptions::add);
        }
        return descriptions;
    }

    public List<String> getJavaDocFromComments() {
        List<String> comments = new ArrayList<>();
        if (definition != null && definition.getComments() != null) {
            definition.getComments().stream()
                    .map(Comment::getContent).filter(Utils::isNotBlank)
                    .map(String::trim).forEach(comments::add);
        }
        extensions.stream()
                .map(Node::getComments)
                .flatMap(Collection::stream).filter(Objects::nonNull)
                .map(Comment::getContent).filter(Utils::isNotBlank)
                .map(String::trim).forEach(comments::add);
        return comments;
    }

    public List<String> getDirectiveNames() {
        List<String> directives = new ArrayList<>();
        if (this.definition instanceof DirectivesContainer) {
            List<Directive> definitionDirectives = ((DirectivesContainer<?>) this.definition).getDirectives();
            if (!Utils.isEmpty(definitionDirectives)) {
                definitionDirectives.stream().map(Directive::getName).forEach(directives::add);
            }
            this.extensions.stream().filter(Objects::nonNull)
                    .map(DirectivesContainer.class::cast)
                    .map(DirectivesContainer::getDirectives).filter(Objects::nonNull)
                    .forEach(ds -> ds.forEach(d -> directives.add(((Directive) d).getName())));
        }
        return directives;
    }

    public T getDefinition() {
        return definition;
    }

    public void setDefinition(T definition) {
        this.definition = definition;
    }

    public List<E> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<E> extensions) {
        this.extensions = extensions;
    }
}
