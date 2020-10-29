package com.kobylynskyi.graphql.codegen;

import com.fasterxml.jackson.core.type.TypeReference;
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
import graphql.introspection.IntrospectionResultToSchema;
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

    private GraphQLDocumentParser() {
    }

    static ExtendedDocument getDocumentFromSchemas(MappingConfig mappingConfig, List<String> schemaPaths) throws IOException {
        Document document = readDocument(schemaPaths);

        ExtendedDocumentBuilder extendedDocumentBuilder = new ExtendedDocumentBuilder();

        for (Definition<?> definition : document.getDefinitions()) {
            processDefinition(mappingConfig, extendedDocumentBuilder, definition);
        }
        return extendedDocumentBuilder.build();
    }

    static ExtendedDocument getDocumentFromIntrospectionResult(MappingConfig mappingConfig, String introspectionResult) throws IOException {
        String introspectionResultContent = Utils.getFileContent(introspectionResult);
        Map<String, Object> introspectionResultMap = Utils.OBJECT_MAPPER.readValue(introspectionResultContent,
                new TypeReference<Map<String, Object>>() {
                });
        // unwrapping "data" (in case such GraphQL response supplied)
        if (introspectionResultMap.containsKey("data")) {
            introspectionResultMap = (Map<String, Object>) introspectionResultMap.get("data");
        }
        Document document = new IntrospectionResultToSchema().createSchemaDefinition(introspectionResultMap);

        ExtendedDocumentBuilder extendedDocumentBuilder = new ExtendedDocumentBuilder();

        for (Definition<?> definition : document.getDefinitions()) {
            processDefinition(mappingConfig, extendedDocumentBuilder, definition);
        }
        return extendedDocumentBuilder.build();
    }

    private static void processDefinition(MappingConfig mappingConfig, ExtendedDocumentBuilder extendedDocumentBuilder, Definition<?> definition) {
        if (!(definition instanceof NamedNode)) {
            // the only definition that does not have a name is SchemaDefinition, so skipping it
            return;
        }
        // we need to group base definitions and extension definitions by name
        String definitionName = ((NamedNode<?>) definition).getName();

        if (definition instanceof ObjectTypeDefinition) {
            if (Utils.isGraphqlOperation(definitionName)) {
                populateDefinition(extendedDocumentBuilder.operationDefinitions, definition, definitionName,
                        ObjectTypeExtensionDefinition.class, s -> new ExtendedObjectTypeDefinition());
                if (Boolean.TRUE.equals(mappingConfig.getGenerateModelsForRootTypes())) {
                    populateDefinition(extendedDocumentBuilder.typeDefinitions, definition, definitionName,
                            ObjectTypeExtensionDefinition.class, s -> new ExtendedObjectTypeDefinition());
                }
            } else {
                populateDefinition(extendedDocumentBuilder.typeDefinitions, definition, definitionName,
                        ObjectTypeExtensionDefinition.class, s -> new ExtendedObjectTypeDefinition());
            }
        } else if (definition instanceof EnumTypeDefinition) {
            populateDefinition(extendedDocumentBuilder.enumDefinitions, definition, definitionName,
                    EnumTypeExtensionDefinition.class, s -> new ExtendedEnumTypeDefinition());
        } else if (definition instanceof InputObjectTypeDefinition) {
            populateDefinition(extendedDocumentBuilder.inputDefinitions, definition, definitionName,
                    InputObjectTypeExtensionDefinition.class, s -> new ExtendedInputObjectTypeDefinition());
        } else if (definition instanceof UnionTypeDefinition) {
            populateDefinition(extendedDocumentBuilder.unionDefinitions, definition, definitionName,
                    UnionTypeExtensionDefinition.class, s -> new ExtendedUnionTypeDefinition());
        } else if (definition instanceof ScalarTypeDefinition) {
            populateDefinition(extendedDocumentBuilder.scalarDefinitions, definition, definitionName,
                    ScalarTypeExtensionDefinition.class, s -> new ExtendedScalarTypeDefinition());
        } else if (definition instanceof InterfaceTypeDefinition) {
            populateDefinition(extendedDocumentBuilder.interfaceDefinitions, definition, definitionName,
                    InterfaceTypeExtensionDefinition.class, s -> new ExtendedInterfaceTypeDefinition());
        }
    }

    @SuppressWarnings("unchecked")
    private static <
            D extends ExtendedDefinition<B, E>,
            B extends NamedNode<B>,
            E extends B> void populateDefinition(Map<String, D> definitionsMap,
                                                 Definition<?> definition,
                                                 String definitionName,
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
            return new Parser().parseDocument(reader);
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

    private static class ExtendedDocumentBuilder {

        private final Map<String, ExtendedObjectTypeDefinition> operationDefinitions = new HashMap<>();
        private final Map<String, ExtendedObjectTypeDefinition> typeDefinitions = new HashMap<>();
        private final Map<String, ExtendedInputObjectTypeDefinition> inputDefinitions = new HashMap<>();
        private final Map<String, ExtendedEnumTypeDefinition> enumDefinitions = new HashMap<>();
        private final Map<String, ExtendedScalarTypeDefinition> scalarDefinitions = new HashMap<>();
        private final Map<String, ExtendedInterfaceTypeDefinition> interfaceDefinitions = new HashMap<>();
        private final Map<String, ExtendedUnionTypeDefinition> unionDefinitions = new HashMap<>();

        ExtendedDocument build() {
            return new ExtendedDocument(
                    operationDefinitions.values(),
                    typeDefinitions.values(),
                    inputDefinitions.values(),
                    enumDefinitions.values(),
                    scalarDefinitions.values(),
                    interfaceDefinitions.values(),
                    unionDefinitions.values());
        }

    }

}
