package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.NamedNode;
import graphql.language.UnionTypeDefinition;
import graphql.language.UnionTypeExtensionDefinition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ExtendedUnionTypeDefinition extends ExtendedDefinition<UnionTypeDefinition, UnionTypeExtensionDefinition> {

    private Set<String> memberTypeNames;

    /**
     * Find out if a given definition is part of a union.
     *
     * @param definition GraphQL definition (type / interface / object / union / etc.)
     * @return <b>true</b> if <code>definition</code> is a part of <code>union</code>. <b>false</b>if <code>definition</code> is a part of <code>union</code>.
     */
    public boolean isDefinitionPartOfUnion(ExtendedDefinition<?, ?> definition) {
        if (memberTypeNames == null) {
            memberTypeNames = getMemberTypeNames();
        }
        return memberTypeNames.contains(definition.getName());
    }

    private Set<String> getMemberTypeNames() {
        Set<String> memberTypeNames = new HashSet<>();
        if (definition != null) {
            definition.getMemberTypes().stream()
                    .map(NamedNode.class::cast)
                    .map(NamedNode::getName)
                    .forEach(memberTypeNames::add);
        }
        extensions.stream()
                .map(UnionTypeDefinition::getMemberTypes)
                .flatMap(Collection::stream)
                .map(NamedNode.class::cast)
                .map(NamedNode::getName)
                .forEach(memberTypeNames::add);
        return memberTypeNames;
    }
}
