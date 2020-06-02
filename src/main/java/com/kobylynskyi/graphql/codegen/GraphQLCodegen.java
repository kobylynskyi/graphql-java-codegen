package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.mapper.EnumDefinitionToDataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.FieldDefinitionToParameterMapper;
import com.kobylynskyi.graphql.codegen.mapper.FieldDefinitionsToResolverDataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.InputDefinitionToDataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.InterfaceDefinitionToDataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.RequestResponseDefinitionToDataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.TypeDefinitionToDataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.UnionDefinitionToDataModelMapper;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.MappingConfigConstants;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDocument;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedEnumTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInputObjectTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInterfaceTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedScalarTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedUnionTypeDefinition;
import com.kobylynskyi.graphql.codegen.supplier.MappingConfigSupplier;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.FieldDefinition;
import graphql.language.ScalarTypeExtensionDefinition;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Generator of:
 * - Interface for each GraphQL query, mutation, subscription, union and field resolvers
 * - POJO Class for each GraphQL type and input
 * - Enum Class for each GraphQL enum
 *
 * @author kobylynskyi
 * @author valinhadev
 */
@Getter
@Setter
public class GraphQLCodegen {

    private List<String> schemas;
    private File outputDir;
    private MappingConfig mappingConfig;

    public GraphQLCodegen(List<String> schemas, File outputDir, MappingConfig mappingConfig) {
        this(schemas, outputDir, mappingConfig, null);
    }

    public GraphQLCodegen(List<String> schemas, File outputDir, MappingConfig mappingConfig, MappingConfigSupplier externalMappingConfigSupplier) {
        this.schemas = schemas;
        this.outputDir = outputDir;
        this.mappingConfig = mappingConfig;
        this.mappingConfig.combine(externalMappingConfigSupplier != null ? externalMappingConfigSupplier.get() : null);
        initDefaultValues(mappingConfig);
    }

    private void initDefaultValues(MappingConfig mappingConfig) {
        if (mappingConfig.getModelValidationAnnotation() == null) {
            mappingConfig.setModelValidationAnnotation(MappingConfigConstants.DEFAULT_VALIDATION_ANNOTATION);
        }
        if (mappingConfig.getGenerateBuilder() == null) {
            mappingConfig.setGenerateBuilder(MappingConfigConstants.DEFAULT_BUILDER);
        }
        if (mappingConfig.getGenerateEqualsAndHashCode() == null) {
            mappingConfig.setGenerateEqualsAndHashCode(MappingConfigConstants.DEFAULT_EQUALS_AND_HASHCODE);
        }
        if (mappingConfig.getGenerateClient() == null) {
            mappingConfig.setGenerateClient(MappingConfigConstants.DEFAULT_GENERATE_CLIENT);
        }
        if (mappingConfig.getRequestSuffix() == null) {
            mappingConfig.setRequestSuffix(MappingConfigConstants.DEFAULT_REQUEST_SUFFIX);
        }
        if (mappingConfig.getResponseSuffix() == null) {
            mappingConfig.setResponseSuffix(MappingConfigConstants.DEFAULT_RESPONSE_SUFFIX);
        }
        if (mappingConfig.getResponseProjectionSuffix() == null) {
            mappingConfig.setResponseProjectionSuffix(MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_SUFFIX);
        }
        if (mappingConfig.getParametrizedInputSuffix() == null) {
            mappingConfig.setParametrizedInputSuffix(MappingConfigConstants.DEFAULT_PARAMETRIZED_INPUT_SUFIX);
        }
        if (mappingConfig.getGenerateToString() == null) {
            mappingConfig.setGenerateToString(MappingConfigConstants.DEFAULT_TO_STRING);
        }
        if (mappingConfig.getGenerateApis() == null) {
            mappingConfig.setGenerateApis(MappingConfigConstants.DEFAULT_GENERATE_APIS);
        }
        if (mappingConfig.getGenerateAsyncApi() == null) {
            mappingConfig.setGenerateAsyncApi(MappingConfigConstants.DEFAULT_GENERATE_ASYNC_APIS);
        }
        if (mappingConfig.getGenerateParameterizedFieldsResolvers() == null) {
            mappingConfig.setGenerateParameterizedFieldsResolvers(MappingConfigConstants.DEFAULT_GENERATE_PARAMETERIZED_FIELDS_RESOLVERS);
        }
        if (mappingConfig.getGenerateExtensionFieldsResolvers() == null) {
            mappingConfig.setGenerateExtensionFieldsResolvers(MappingConfigConstants.DEFAULT_GENERATE_EXTENSION_FIELDS_RESOLVERS);
        }
        if (mappingConfig.getGenerateDataFetchingEnvironmentArgumentInApis() == null) {
            mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(MappingConfigConstants.DEFAULT_GENERATE_DATA_FETCHING_ENV);
        }
        if (mappingConfig.getGenerateClient()) {
            // required for request serialization
            mappingConfig.setGenerateToString(true);
        }
    }

    public List<File> generate() throws Exception {
        GraphQLCodegenFileCreator.prepareOutputDir(outputDir);
        long startTime = System.currentTimeMillis();
        List<File> generatedFiles = Collections.emptyList();
        if (!schemas.isEmpty()) {
            ExtendedDocument document = GraphQLDocumentParser.getDocument(schemas);
            initCustomTypeMappings(document.getScalarDefinitions());
            generatedFiles = processDefinitions(document);
        }
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Finished processing %d schema(s) in %d ms", schemas.size(), elapsed));
        return generatedFiles;
    }

    private List<File> processDefinitions(ExtendedDocument document) {
        MappingContext context = new MappingContext(mappingConfig, document,
                document.getTypeNames(), document.getInterfaceNames());

        List<File> generatedFiles = new ArrayList<>();
        for (ExtendedObjectTypeDefinition extendedObjectTypeDefinition : document.getTypeDefinitions()) {
            generatedFiles.addAll(generateType(context, extendedObjectTypeDefinition));
        }
        for (ExtendedObjectTypeDefinition extendedObjectTypeDefinition : document.getTypeDefinitions()) {
            generateFieldResolver(context, extendedObjectTypeDefinition.getFieldDefinitions(), extendedObjectTypeDefinition.getName())
                    .ifPresent(generatedFiles::add);
        }
        for (ExtendedObjectTypeDefinition extendedObjectTypeDefinition : document.getOperationDefinitions()) {
            generatedFiles.addAll(generateOperation(context, extendedObjectTypeDefinition));
        }
        for (ExtendedInputObjectTypeDefinition extendedInputObjectTypeDefinition : document.getInputDefinitions()) {
            generatedFiles.add(generateInput(context, extendedInputObjectTypeDefinition));
        }
        for (ExtendedEnumTypeDefinition extendedEnumTypeDefinition : document.getEnumDefinitions()) {
            generatedFiles.add(generateEnum(context, extendedEnumTypeDefinition));
        }
        for (ExtendedUnionTypeDefinition extendedUnionTypeDefinition : document.getUnionDefinitions()) {
            generatedFiles.add(generateUnion(context, extendedUnionTypeDefinition));
        }
        for (ExtendedInterfaceTypeDefinition extendedInterfaceTypeDefinition : document.getInterfaceDefinitions()) {
            generatedFiles.add(generateInterface(context, extendedInterfaceTypeDefinition));
        }
        for (ExtendedInterfaceTypeDefinition definition : document.getInterfaceDefinitions()) {
            generateFieldResolver(context, definition.getFieldDefinitions(), definition.getName())
                    .ifPresent(generatedFiles::add);
        }
        System.out.println(String.format("Generated %d definition classes in folder %s",
                generatedFiles.size(), outputDir.getAbsolutePath()));
        return generatedFiles;
    }

    private File generateUnion(MappingContext mappingContext, ExtendedUnionTypeDefinition definition) {
        Map<String, Object> dataModel = UnionDefinitionToDataModelMapper.map(mappingContext, definition);
        return GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.unionTemplate, dataModel, outputDir);
    }

    private File generateInterface(MappingContext mappingContext, ExtendedInterfaceTypeDefinition definition) {
        Map<String, Object> dataModel = InterfaceDefinitionToDataModelMapper.map(mappingContext, definition);
        return GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.interfaceTemplate, dataModel, outputDir);
    }

    private List<File> generateOperation(MappingContext mappingContext, ExtendedObjectTypeDefinition definition) {
        List<File> generatedFiles = new ArrayList<>();
        List<String> fieldNames = definition.getFieldDefinitions().stream().map(FieldDefinition::getName).collect(toList());
        if (mappingConfig.getGenerateApis()) {
            for (ExtendedFieldDefinition operationDef : definition.getFieldDefinitions()) {
                Map<String, Object> dataModel = FieldDefinitionsToResolverDataModelMapper.mapRootTypeField(mappingContext, operationDef, definition.getName(), fieldNames);
                generatedFiles.add(GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.operationsTemplate, dataModel, outputDir));
            }
            // We need to generate a root object to workaround https://github.com/facebook/relay/issues/112
            Map<String, Object> dataModel = FieldDefinitionsToResolverDataModelMapper.mapRootTypeFields(mappingContext, definition);
            generatedFiles.add(GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.operationsTemplate, dataModel, outputDir));
        }

        if (mappingConfig.getGenerateClient()) {
            // generate request objects for graphql operations
            for (ExtendedFieldDefinition operationDef : definition.getFieldDefinitions()) {
                Map<String, Object> requestDataModel = RequestResponseDefinitionToDataModelMapper.mapRequest(mappingContext, operationDef, definition.getName(), fieldNames);
                generatedFiles.add(GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.requestTemplate, requestDataModel, outputDir));

                Map<String, Object> responseDataModel = RequestResponseDefinitionToDataModelMapper.mapResponse(mappingContext, operationDef, definition.getName(), fieldNames);
                generatedFiles.add(GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.responseTemplate, responseDataModel, outputDir));
            }
        }
        return generatedFiles;
    }

    private List<File> generateType(MappingContext mappingContext, ExtendedObjectTypeDefinition definition) {
        List<File> generatedFiles = new ArrayList<>();
        Map<String, Object> dataModel = TypeDefinitionToDataModelMapper.map(mappingContext, definition);
        generatedFiles.add(GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.typeTemplate, dataModel, outputDir));

        if (mappingConfig.getGenerateClient()) {
            Map<String, Object> responseProjDataModel = RequestResponseDefinitionToDataModelMapper.mapResponseProjection(mappingContext, definition);
            generatedFiles.add(GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.responseProjectionTemplate, responseProjDataModel, outputDir));

            for (ExtendedFieldDefinition fieldDefinition : definition.getFieldDefinitions()) {
                if (!Utils.isEmpty(fieldDefinition.getInputValueDefinitions())) {
                    Map<String, Object> fieldProjDataModel = RequestResponseDefinitionToDataModelMapper.mapParametrizedInput(mappingContext, fieldDefinition, definition);
                    generatedFiles.add(GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.parametrizedInputTemplate, fieldProjDataModel, outputDir));
                }
            }
        }
        return generatedFiles;
    }

    private Optional<File> generateFieldResolver(MappingContext mappingContext, List<ExtendedFieldDefinition> fieldDefinitions, String definitionName) {
        List<ExtendedFieldDefinition> fieldDefsWithResolvers = fieldDefinitions.stream()
                .filter(fieldDef -> FieldDefinitionToParameterMapper.generateResolversForField(mappingContext, fieldDef, definitionName))
                .collect(toList());
        if (!fieldDefsWithResolvers.isEmpty()) {
            Map<String, Object> dataModel = FieldDefinitionsToResolverDataModelMapper.mapToTypeResolver(mappingContext, fieldDefsWithResolvers, definitionName);
            return Optional.of(GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.operationsTemplate, dataModel, outputDir));
        }
        return Optional.empty();
    }

    private File generateInput(MappingContext mappingContext, ExtendedInputObjectTypeDefinition definition) {
        Map<String, Object> dataModel = InputDefinitionToDataModelMapper.map(mappingContext, definition);
        return GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.typeTemplate, dataModel, outputDir);
    }

    private File generateEnum(MappingContext mappingContext, ExtendedEnumTypeDefinition definition) {
        Map<String, Object> dataModel = EnumDefinitionToDataModelMapper.map(mappingContext, definition);
        return GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.enumTemplate, dataModel, outputDir);
    }

    private void initCustomTypeMappings(Collection<ExtendedScalarTypeDefinition> scalarTypeDefinitions) {
        for (ExtendedScalarTypeDefinition definition : scalarTypeDefinitions) {
            if (definition.getDefinition() != null) {
                mappingConfig.putCustomTypeMappingIfAbsent(definition.getDefinition().getName(), "String");
            }
            for (ScalarTypeExtensionDefinition extension : definition.getExtensions()) {
                mappingConfig.putCustomTypeMappingIfAbsent(extension.getName(), "String");
            }
        }
        mappingConfig.putCustomTypeMappingIfAbsent("ID", "String");
        mappingConfig.putCustomTypeMappingIfAbsent("String", "String");
        mappingConfig.putCustomTypeMappingIfAbsent("Int", "Integer");
        mappingConfig.putCustomTypeMappingIfAbsent("Float", "Double");
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean", "Boolean");
    }

}
