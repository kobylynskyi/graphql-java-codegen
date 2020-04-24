package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.definitions.*;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.*;
import graphql.parser.MultiSourceReader;
import graphql.parser.Parser;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class GraphQLDocumentParser {

    private static final Parser GRAPHQL_PARSER = new Parser();

    static ExtendedDocument getDocument(String schemaFilePath) throws IOException {
        return getDocument(Collections.singletonList(schemaFilePath));
    }

    static ExtendedDocument getDocument(List<String> schemaPaths) throws IOException {
        MultiSourceReader reader = createMultiSourceReader(schemaPaths);
        Document document = GRAPHQL_PARSER.parseDocument(reader);

        Map<String, ExtendedObjectTypeDefinition> operationDefinitions = new HashMap<>();
        Map<String, ExtendedObjectTypeDefinition> typeDefinitions = new HashMap<>();
        Map<String, ExtendedInputObjectTypeDefinition> inputDefinitions = new HashMap<>();
        Map<String, ExtendedEnumTypeDefinition> enumDefinitions = new HashMap<>();
        Map<String, ExtendedScalarTypeDefinition> scalarDefinitions = new HashMap<>();
        Map<String, ExtendedInterfaceTypeDefinition> interfaceDefinitions = new HashMap<>();
        Map<String, ExtendedUnionTypeDefinition> unionDefinitions = new HashMap<>();
        for (Definition<?> definition : document.getDefinitions()) {
            if (!(definition instanceof NamedNode)) {
                // the only definition that does not have a name is SchemaDefinition, so skipping it
                continue;
            }
            // we need to group base definitions and extension definitions by name
            String definitionName = ((NamedNode<?>) definition).getName();

            if (definition instanceof ObjectTypeDefinition) {
                if (Utils.isGraphqlOperation(definitionName)) {
                    populateDefinition(operationDefinitions, definition, definitionName,
                            ObjectTypeDefinition.class, ObjectTypeExtensionDefinition.class,
                            s -> new ExtendedObjectTypeDefinition());
                } else {
                    populateDefinition(typeDefinitions, definition, definitionName,
                            ObjectTypeDefinition.class, ObjectTypeExtensionDefinition.class,
                            s -> new ExtendedObjectTypeDefinition());
                }
            } else if (definition instanceof EnumTypeDefinition) {
                populateDefinition(enumDefinitions, definition, definitionName,
                        EnumTypeDefinition.class, EnumTypeExtensionDefinition.class,
                        s -> new ExtendedEnumTypeDefinition());
            } else if (definition instanceof InputObjectTypeDefinition) {
                populateDefinition(inputDefinitions, definition, definitionName,
                        InputObjectTypeDefinition.class, InputObjectTypeExtensionDefinition.class,
                        s -> new ExtendedInputObjectTypeDefinition());
            } else if (definition instanceof UnionTypeDefinition) {
                populateDefinition(unionDefinitions, definition, definitionName,
                        UnionTypeDefinition.class, UnionTypeExtensionDefinition.class,
                        s -> new ExtendedUnionTypeDefinition());
            } else if (definition instanceof ScalarTypeDefinition) {
                populateDefinition(scalarDefinitions, definition, definitionName,
                        ScalarTypeDefinition.class, ScalarTypeExtensionDefinition.class,
                        s -> new ExtendedScalarTypeDefinition());
            } else if (definition instanceof InterfaceTypeDefinition) {
                populateDefinition(interfaceDefinitions, definition, definitionName,
                        InterfaceTypeDefinition.class, InterfaceTypeExtensionDefinition.class,
                        s -> new ExtendedInterfaceTypeDefinition());
            //} else if (definition instanceof DirectiveDefinition) {
            }
        }
        return ExtendedDocument.builder()
                .operationDefinitions(operationDefinitions.values())
                .typeDefinitions(typeDefinitions.values())
                .inputDefinitions(inputDefinitions.values())
                .enumDefinitions(enumDefinitions.values())
                .scalarDefinitions(scalarDefinitions.values())
                .interfaceDefinitions(interfaceDefinitions.values())
                .unionDefinitions(unionDefinitions.values())
                .build();
    }

    @SuppressWarnings("unchecked")
    private static <D extends ExtendedDefinition<B, E>, B extends NamedNode<B>, E extends B> void populateDefinition(Map<String, D> definitionsMap,
                                                                                                                                Definition<?> definition,
                                                                                                                                String definitionName,
                                                                                                                                Class<B> baseDefinitionClass,
                                                                                                                                Class<E> extensionDefinitionClass,
                                                                                                                                Function<String, D> mappingFunction) {
        D extendedDefinition = definitionsMap.computeIfAbsent(definitionName, mappingFunction);
        if (extensionDefinitionClass.isAssignableFrom(definition.getClass())) {
            extendedDefinition.getExtensions().add((E) definition);
        } else {
            extendedDefinition.setDefinition((B) definition);
        }
    }

    private static MultiSourceReader createMultiSourceReader(List<String> schemaPaths) throws IOException {
        MultiSourceReader.Builder builder = MultiSourceReader.newMultiSourceReader();
        for (String path : schemaPaths) {
            // appending EOL to ensure that schema tokens are not mixed in case files are not properly ended with EOL
            String content = Utils.getFileContent(path) + System.lineSeparator();
            builder.string(content, path);
        }
        return builder.trackData(true).build();
    }
}
