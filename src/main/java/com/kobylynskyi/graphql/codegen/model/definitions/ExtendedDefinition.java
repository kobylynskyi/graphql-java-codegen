package com.kobylynskyi.graphql.codegen.model.definitions;

import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Directive;
import graphql.language.DirectivesContainer;
import graphql.language.NamedNode;
import graphql.language.Node;
import graphql.language.SourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    /**
     * Return all directives for this definition
     *
     * @return list of directive names
     */
    public List<String> getDirectiveNames() {
        return getDirectives().stream()
                .map(Directive::getName)
                .collect(Collectors.toList());
    }

    /**
     * Return all directives for this definition
     *
     * @return list of directive names
     */
    public List<Directive> getDirectives() {
        List<Directive> directives = new ArrayList<>();
        if (this.definition instanceof DirectivesContainer) {
            List<Directive> definitionDirectives = ((DirectivesContainer<?>) this.definition).getDirectives();
            if (!Utils.isEmpty(definitionDirectives)) {
                directives.addAll(definitionDirectives);
            }
            this.extensions.stream()
                    .filter(Objects::nonNull)
                    .map(DirectivesContainer.class::cast)
                    .map(DirectivesContainer::getDirectives)
                    .filter(dc -> !Utils.isEmpty(dc))
                    .forEach(directives::addAll);
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
