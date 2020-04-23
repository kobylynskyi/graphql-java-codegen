package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.mapper.*;
import com.kobylynskyi.graphql.codegen.model.DefaultMappingConfigValues;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.definitions.*;
import com.kobylynskyi.graphql.codegen.supplier.MappingConfigSupplier;
import graphql.language.*;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            mappingConfig.setModelValidationAnnotation(DefaultMappingConfigValues.DEFAULT_VALIDATION_ANNOTATION);
        }
        if (mappingConfig.getGenerateBuilder() == null) {
            mappingConfig.setGenerateBuilder(DefaultMappingConfigValues.DEFAULT_BUILDER);
        }
        if (mappingConfig.getGenerateEqualsAndHashCode() == null) {
            mappingConfig.setGenerateEqualsAndHashCode(DefaultMappingConfigValues.DEFAULT_EQUALS_AND_HASHCODE);
        }
        if (mappingConfig.getGenerateRequests() == null) {
            mappingConfig.setGenerateRequests(DefaultMappingConfigValues.DEFAULT_GENERATE_REQUESTS);
        }
        if (mappingConfig.getRequestSuffix() == null) {
            mappingConfig.setRequestSuffix(DefaultMappingConfigValues.DEFAULT_REQUEST_SUFFIX);
        }
        if (mappingConfig.getResponseProjectionSuffix() == null) {
            mappingConfig.setResponseProjectionSuffix(DefaultMappingConfigValues.DEFAULT_RESPONSE_PROJECTION_SUFFIX);
        }
        if (mappingConfig.getGenerateToString() == null) {
            mappingConfig.setGenerateToString(DefaultMappingConfigValues.DEFAULT_TO_STRING);
        }
        if (mappingConfig.getGenerateApis() == null) {
            mappingConfig.setGenerateApis(DefaultMappingConfigValues.DEFAULT_GENERATE_APIS);
        }
        if (mappingConfig.getGenerateAsyncApi() == null) {
            mappingConfig.setGenerateAsyncApi(DefaultMappingConfigValues.DEFAULT_GENERATE_ASYNC_APIS);
        }
        if (mappingConfig.getGenerateParameterizedFieldsResolvers() == null) {
            mappingConfig.setGenerateParameterizedFieldsResolvers(DefaultMappingConfigValues.DEFAULT_GENERATE_PARAMETERIZED_FIELDS_RESOLVERS);
        }
        if (mappingConfig.getGenerateExtensionFieldsResolvers() == null) {
            mappingConfig.setGenerateExtensionFieldsResolvers(DefaultMappingConfigValues.DEFAULT_GENERATE_EXTENSION_FIELDS_RESOLVERS);
        }
        if (mappingConfig.getGenerateDataFetchingEnvironmentArgumentInApis() == null) {
            mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(DefaultMappingConfigValues.DEFAULT_GENERATE_DATA_FETCHING_ENV);
        }
        if (mappingConfig.getGenerateRequests()) {
            // required for request serialization
            mappingConfig.setGenerateToString(true);
        }
    }


    public void generate() throws Exception {
        GraphQLCodegenFileCreator.prepareOutputDir(outputDir);
        long startTime = System.currentTimeMillis();
        if (!schemas.isEmpty()) {
            ExtendedDocument document = GraphQLDocumentParser.getDocument(schemas);
            initCustomTypeMappings(document.getScalarDefinitions());
            processDefinitions(document);
        }
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Finished processing %d schema(s) in %d ms", schemas.size(), elapsed));
    }

    private void processDefinitions(ExtendedDocument document) {
        Set<String> typeNames = document.getTypeNames();
        document.getTypeDefinitions().forEach(definition -> generateType(definition, document, typeNames));
        document.getTypeDefinitions().forEach(definition -> generateFieldResolvers(definition.getFieldDefinitions(), definition.getName()));
        document.getOperationDefinitions().forEach(this::generateOperation);
        document.getInputDefinitions().forEach(this::generateInput);
        document.getEnumDefinitions().forEach(this::generateEnum);
        document.getUnionDefinitions().forEach(this::generateUnion);
        document.getInterfaceDefinitions().forEach(this::generateInterface);
        document.getInterfaceDefinitions().forEach(definition -> generateFieldResolvers(definition.getFieldDefinitions(), definition.getName()));

        System.out.println(String.format("Generated definition classes in folder %s", outputDir.getAbsolutePath()));
    }

    private void generateUnion(ExtendedUnionTypeDefinition definition) {
        Map<String, Object> dataModel = UnionDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.unionTemplate, dataModel, outputDir);
    }

    private void generateInterface(ExtendedInterfaceTypeDefinition definition) {
        Map<String, Object> dataModel = InterfaceDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.interfaceTemplate, dataModel, outputDir);
    }

    private void generateOperation(ExtendedObjectTypeDefinition definition) {
        if (Boolean.TRUE.equals(mappingConfig.getGenerateApis())) {
            for (FieldDefinitionFromExtension operationDef : definition.getFieldDefinitions()) {
                Map<String, Object> dataModel = FieldDefinitionsToResolverDataModelMapper.mapRootTypeField(mappingConfig, operationDef, definition.getName());
                GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.operationsTemplate, dataModel, outputDir);
            }
            // We need to generate a root object to workaround https://github.com/facebook/relay/issues/112
            Map<String, Object> dataModel = FieldDefinitionsToResolverDataModelMapper.mapRootTypeFields(mappingConfig, definition);
            GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.operationsTemplate, dataModel, outputDir);
        }

        if (Boolean.TRUE.equals(mappingConfig.getGenerateRequests())) {
            // generate request objects for graphql operations
            for (FieldDefinition operationDef : definition.getFieldDefinitions()) {
                Map<String, Object> requestDataModel = FieldDefinitionToRequestDataModelMapper.map(mappingConfig, operationDef, definition.getName());
                GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.requestTemplate, requestDataModel, outputDir);
            }
        }
    }

    private void generateType(ExtendedObjectTypeDefinition definition, ExtendedDocument document, Set<String> typeNames) {
        Map<String, Object> dataModel = TypeDefinitionToDataModelMapper.map(mappingConfig, definition, document);
        GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.typeTemplate, dataModel, outputDir);

        if (Boolean.TRUE.equals(mappingConfig.getGenerateRequests())) {
            Map<String, Object> responseProjDataModel = TypeDefinitionToDataModelMapper.mapResponseProjection(mappingConfig, definition, document, typeNames);
            GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.responseProjectionTemplate, responseProjDataModel, outputDir);
        }
    }

    private void generateFieldResolvers(List<FieldDefinitionFromExtension> fieldDefinitions, String definitionName) {
        List<FieldDefinitionFromExtension> fieldDefsWithResolvers = fieldDefinitions.stream()
                .filter(fieldDef -> FieldDefinitionToParameterMapper.generateResolversForField(mappingConfig, fieldDef, definitionName))
                .collect(toList());
        if (!fieldDefsWithResolvers.isEmpty()) {
            Map<String, Object> dataModel = FieldDefinitionsToResolverDataModelMapper.mapToTypeResolver(mappingConfig, fieldDefsWithResolvers, definitionName);
            GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.operationsTemplate, dataModel, outputDir);
        }
    }

    private void generateInput(ExtendedInputObjectTypeDefinition definition) {
        Map<String, Object> dataModel = InputDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.typeTemplate, dataModel, outputDir);
    }

    private void generateEnum(ExtendedEnumTypeDefinition definition) {
        Map<String, Object> dataModel = EnumDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.enumTemplate, dataModel, outputDir);
    }

    private void initCustomTypeMappings(Collection<ExtendedScalarTypeDefinition> scalarTypeDefinitions) {
        for (ExtendedScalarTypeDefinition definition : scalarTypeDefinitions) {
            mappingConfig.putCustomTypeMappingIfAbsent(definition.getDefinition().getName(), "String");
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
