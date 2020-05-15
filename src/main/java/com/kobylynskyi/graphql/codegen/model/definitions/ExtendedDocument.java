package com.kobylynskyi.graphql.codegen.model.definitions;

import lombok.Builder;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GraphQL document that holds all extended definitions
 */
@Getter
@Builder
public class ExtendedDocument {

    private Collection<ExtendedObjectTypeDefinition> operationDefinitions;
    private Collection<ExtendedObjectTypeDefinition> typeDefinitions;
    private Collection<ExtendedInputObjectTypeDefinition> inputDefinitions;
    private Collection<ExtendedEnumTypeDefinition> enumDefinitions;
    private Collection<ExtendedScalarTypeDefinition> scalarDefinitions;
    private Collection<ExtendedInterfaceTypeDefinition> interfaceDefinitions;
    private Collection<ExtendedUnionTypeDefinition> unionDefinitions;

    public Set<String> getTypeNames() {
        return typeDefinitions.stream()
                .map(ExtendedDefinition::getName)
                .collect(Collectors.toSet());
    }

    public Set<String> getInterfaceNames() {
        return interfaceDefinitions.stream()
                .map(ExtendedDefinition::getName)
                .collect(Collectors.toSet());
    }

}
