package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.Type;
import graphql.language.TypeName;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GraphQL document that holds all extended definitions
 */
public class ExtendedDocument {

    private final Collection<ExtendedObjectTypeDefinition> operationDefinitions;
    private final Collection<ExtendedObjectTypeDefinition> typeDefinitions;
    private final Collection<ExtendedInputObjectTypeDefinition> inputDefinitions;
    private final Collection<ExtendedEnumTypeDefinition> enumDefinitions;
    private final Collection<ExtendedScalarTypeDefinition> scalarDefinitions;
    private final Collection<ExtendedInterfaceTypeDefinition> interfaceDefinitions;
    private final Collection<ExtendedUnionTypeDefinition> unionDefinitions;

    public ExtendedDocument(Collection<ExtendedObjectTypeDefinition> operationDefinitions,
                            Collection<ExtendedObjectTypeDefinition> typeDefinitions,
                            Collection<ExtendedInputObjectTypeDefinition> inputDefinitions,
                            Collection<ExtendedEnumTypeDefinition> enumDefinitions,
                            Collection<ExtendedScalarTypeDefinition> scalarDefinitions,
                            Collection<ExtendedInterfaceTypeDefinition> interfaceDefinitions,
                            Collection<ExtendedUnionTypeDefinition> unionDefinitions) {
        this.operationDefinitions = operationDefinitions;
        this.typeDefinitions = typeDefinitions;
        this.inputDefinitions = inputDefinitions;
        this.enumDefinitions = enumDefinitions;
        this.scalarDefinitions = scalarDefinitions;
        this.interfaceDefinitions = interfaceDefinitions;
        this.unionDefinitions = unionDefinitions;
    }

    public Set<String> getTypesUnionsInterfacesNames() {
        Set<String> typesUnionsInterfaces = new LinkedHashSet<>();
        typeDefinitions.stream()
                .map(ExtendedDefinition::getName)
                .forEach(typesUnionsInterfaces::add);
        unionDefinitions.stream()
                .map(ExtendedDefinition::getName)
                .forEach(typesUnionsInterfaces::add);
        interfaceDefinitions.stream()
                .map(ExtendedDefinition::getName)
                .forEach(typesUnionsInterfaces::add);
        return typesUnionsInterfaces;
    }

    public Map<String, Set<String>> getInterfaceChildren() {
        Map<String, Set<String>> interfaceChildren = new HashMap<>();
        for (ExtendedObjectTypeDefinition typeDefinition : typeDefinitions) {
            for (Type<?> interfaceType : typeDefinition.getImplements()) {
                interfaceChildren.computeIfAbsent(((TypeName) interfaceType).getName(), k -> new HashSet<>())
                        .add(typeDefinition.getName());
            }
        }
        for (ExtendedInterfaceTypeDefinition interfaceTypeDefinition : interfaceDefinitions) {
            for (Type<?> interfaceType : interfaceTypeDefinition.getImplements()) {
                interfaceChildren.computeIfAbsent(((TypeName) interfaceType).getName(), k -> new HashSet<>())
                        .add(interfaceTypeDefinition.getName());
            }
        }
        return interfaceChildren;
    }

    public Set<String> getInterfacesNames() {
        return interfaceDefinitions.stream()
                .map(ExtendedDefinition::getName)
                .collect(Collectors.toSet());
    }

    public Collection<ExtendedObjectTypeDefinition> getOperationDefinitions() {
        return operationDefinitions;
    }

    public Collection<ExtendedObjectTypeDefinition> getTypeDefinitions() {
        return typeDefinitions;
    }

    public Collection<ExtendedInputObjectTypeDefinition> getInputDefinitions() {
        return inputDefinitions;
    }

    public Collection<ExtendedEnumTypeDefinition> getEnumDefinitions() {
        return enumDefinitions;
    }

    public Collection<ExtendedScalarTypeDefinition> getScalarDefinitions() {
        return scalarDefinitions;
    }

    public Collection<ExtendedInterfaceTypeDefinition> getInterfaceDefinitions() {
        return interfaceDefinitions;
    }

    public Collection<ExtendedUnionTypeDefinition> getUnionDefinitions() {
        return unionDefinitions;
    }
}
