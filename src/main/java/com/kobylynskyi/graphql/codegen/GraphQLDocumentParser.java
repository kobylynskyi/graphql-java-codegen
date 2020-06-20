package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDocument;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedEnumTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInputObjectTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInterfaceTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedScalarTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedUnionTypeDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.EnumTypeDefinition;
import graphql.language.EnumTypeExtensionDefinition;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputObjectTypeExtensionDefinition;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.InterfaceTypeExtensionDefinition;
import graphql.language.NamedNode;
import graphql.language.ObjectTypeDefinition;
import graphql.language.ObjectTypeExtensionDefinition;
import graphql.language.ScalarTypeDefinition;
import graphql.language.ScalarTypeExtensionDefinition;
import graphql.language.UnionTypeDefinition;
import graphql.language.UnionTypeExtensionDefinition;
import graphql.parser.MultiSourceReader;
import graphql.parser.Parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class GraphQLDocumentParser {

    private static final Parser GRAPHQL_PARSER = new Parser();

    private GraphQLDocumentParser() {
    }

    static ExtendedDocument getDocument(MappingConfig mappingConfig, List<String> schemaPaths) throws IOException {
        Document document = readDocument(schemaPaths);

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
                    if (Boolean.TRUE.equals(mappingConfig.getGenerateModelsForRootTypes())) {
                        populateDefinition(typeDefinitions, definition, definitionName,
                                ObjectTypeDefinition.class, ObjectTypeExtensionDefinition.class,
                                s -> new ExtendedObjectTypeDefinition());
                    }
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
            }
            // TODO: consider DirectiveDefinition
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
    private static <
            D extends ExtendedDefinition<B, E>,
            B extends NamedNode<B>,
            E extends B> void populateDefinition(Map<String, D> definitionsMap,
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

    private static Document readDocument(List<String> schemaPaths) throws IOException {
        try (MultiSourceReader reader = createMultiSourceReader(schemaPaths)) {
            return GRAPHQL_PARSER.parseDocument(reader);
        }
    }

    public static MultiSourceReader createMultiSourceReader(List<String> schemaPaths) throws IOException {
        if (schemaPaths == null) {
            return MultiSourceReader.newMultiSourceReader().build();
        }
        MultiSourceReader.Builder builder = MultiSourceReader.newMultiSourceReader();
        for (String path : schemaPaths) {
            // appending EOL to ensure that schema tokens are not mixed in case files are not properly ended with EOL
            String content = Utils.getFileContent(path) + System.lineSeparator();
            builder.string(content, path);
        }
        return builder.trackData(true).build();
    }
}
