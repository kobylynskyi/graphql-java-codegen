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

    private final Collection<ExtendedObjectTypeDefinition> operationDefinitions;
    private final Collection<ExtendedObjectTypeDefinition> typeDefinitions;
    private final Collection<ExtendedInputObjectTypeDefinition> inputDefinitions;
    private final Collection<ExtendedEnumTypeDefinition> enumDefinitions;
    private final Collection<ExtendedScalarTypeDefinition> scalarDefinitions;
    private final Collection<ExtendedInterfaceTypeDefinition> interfaceDefinitions;
    private final Collection<ExtendedUnionTypeDefinition> unionDefinitions;

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
