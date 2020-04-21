package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.mapper.*;
import com.kobylynskyi.graphql.codegen.model.DefaultMappingConfigValues;
import com.kobylynskyi.graphql.codegen.model.DefinitionTypeDeterminer;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.UnsupportedGraphqlDefinitionException;
import com.kobylynskyi.graphql.codegen.supplier.MappingConfigSupplier;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import freemarker.template.TemplateException;
import graphql.language.*;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
public class GraphqlCodegen {

    private List<String> schemas;
    private File outputDir;
    private MappingConfig mappingConfig;

    public GraphqlCodegen(List<String> schemas, File outputDir, MappingConfig mappingConfig) {
        this(schemas, outputDir, mappingConfig, null);
    }

    public GraphqlCodegen(List<String> schemas, File outputDir, MappingConfig mappingConfig, MappingConfigSupplier externalMappingConfigSupplier) {
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
        if (mappingConfig.getGenerateParameterizedFieldsResolvers() == null) {
            mappingConfig.setGenerateParameterizedFieldsResolvers(DefaultMappingConfigValues.DEFAULT_GENERATE_PARAMETERIZED_FIELDS_RESOLVERS);
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
        GraphqlCodegenFileCreator.prepareOutputDir(outputDir);
        long startTime = System.currentTimeMillis();
        if (!schemas.isEmpty()) {
            Document document = GraphqlDocumentParser.getDocument(schemas);
            initCustomTypeMappings(document);
            processDocument(document);
        }
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Finished processing %d schema(s) in %d ms", schemas.size(), elapsed));
    }

    private void processDocument(Document document) throws IOException, TemplateException {
        Set<String> typeNames = getAllTypeNames(document);
        for (Definition<?> definition : document.getDefinitions()) {
            try {
                processDefinition(document, definition, typeNames);
            } catch (UnsupportedGraphqlDefinitionException ignored) {
            }
        }
        System.out.println(String.format("Generated %d definitions in folder '%s'", document.getDefinitions().size(),
                outputDir.getAbsolutePath()));
    }

    private void processDefinition(Document document, Definition<?> definition, Set<String> typeNames) throws IOException, TemplateException {
        switch (DefinitionTypeDeterminer.determine(definition)) {
            case OPERATION:
                generateOperation((ObjectTypeDefinition) definition, document);
                break;
            case TYPE:
                generateType((ObjectTypeDefinition) definition, document, typeNames);
                generateFieldResolvers((ObjectTypeDefinition) definition, document);
                break;
            case INTERFACE:
                generateInterface((InterfaceTypeDefinition) definition, document);
                break;
            case ENUM:
                generateEnum((EnumTypeDefinition) definition, document);
                break;
            case INPUT:
                generateInput((InputObjectTypeDefinition) definition, document);
                break;
            case UNION:
                generateUnion((UnionTypeDefinition) definition);
        }
    }

    private void generateUnion(UnionTypeDefinition definition) throws IOException, TemplateException {
        Map<String, Object> dataModel = UnionDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.unionTemplate, dataModel, outputDir);
    }

    private void generateInterface(InterfaceTypeDefinition definition, Document document) throws IOException, TemplateException {
        Map<String, Object> dataModel = InterfaceDefinitionToDataModelMapper.map(mappingConfig, definition, document);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.interfaceTemplate, dataModel, outputDir);
    }

    private void generateOperation(ObjectTypeDefinition definition, Document document) throws IOException, TemplateException {
        if (Boolean.TRUE.equals(mappingConfig.getGenerateApis())) {
            for (FieldDefinition operationDef : getRootTypeFieldDefinitions(definition, document)) {
                Map<String, Object> dataModel = FieldDefinitionsToResolverDataModelMapper.mapRootTypeField(mappingConfig, operationDef, definition.getName(), document);
                GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.operationsTemplate, dataModel, outputDir);
            }
            // We need to generate a root object to workaround https://github.com/facebook/relay/issues/112
            Map<String, Object> dataModel = FieldDefinitionsToResolverDataModelMapper.mapRootTypeFields(mappingConfig, definition, document);
            GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.operationsTemplate, dataModel, outputDir);
        }

        if (Boolean.TRUE.equals(mappingConfig.getGenerateRequests())) {
            // generate request objects for graphql operations
            for (FieldDefinition operationDef : getRootTypeFieldDefinitions(definition, document)) {
                Map<String, Object> requestDataModel = FieldDefinitionToRequestDataModelMapper.map(mappingConfig, operationDef, definition.getName());
                GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.requestTemplate, requestDataModel, outputDir);
            }
        }
    }

    private void generateType(ObjectTypeDefinition definition, Document document, Set<String> typeNames) throws IOException, TemplateException {
        Map<String, Object> dataModel = TypeDefinitionToDataModelMapper.map(mappingConfig, definition, document);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.typeTemplate, dataModel, outputDir);

        if (Boolean.TRUE.equals(mappingConfig.getGenerateRequests())) {
            Map<String, Object> responseProjDataModel = TypeDefinitionToDataModelMapper.mapResponseProjection(mappingConfig, definition, document, typeNames);
            GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.responseProjectionTemplate, responseProjDataModel, outputDir);
        }
    }

    private void generateFieldResolvers(ObjectTypeDefinition definition, Document document) throws IOException, TemplateException {
        List<FieldDefinition> fieldDefsWithResolvers = definition.getFieldDefinitions().stream()
                .filter(fieldDef -> FieldDefinitionToParameterMapper.generateResolversForField(mappingConfig, fieldDef, definition.getName()))
                .collect(toList());
        if (!fieldDefsWithResolvers.isEmpty()) {
            Map<String, Object> dataModel = FieldDefinitionsToResolverDataModelMapper.mapToTypeResolver(mappingConfig, fieldDefsWithResolvers, definition.getName());
            GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.operationsTemplate, dataModel, outputDir);
        }
    }

    private void generateInput(InputObjectTypeDefinition definition, Document document) throws IOException, TemplateException {
        Map<String, Object> dataModel = InputDefinitionToDataModelMapper.map(mappingConfig, definition, document);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.typeTemplate, dataModel, outputDir);
    }

    private void generateEnum(EnumTypeDefinition definition, Document document) throws IOException, TemplateException {
        Map<String, Object> dataModel = EnumDefinitionToDataModelMapper.map(mappingConfig, definition, document);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.enumTemplate, dataModel, outputDir);
    }

    private void initCustomTypeMappings(Document document) {
        for (Definition<?> definition : document.getDefinitions()) {
            if (definition instanceof ScalarTypeDefinition) {
                String scalarName = ((ScalarTypeDefinition) definition).getName();
                mappingConfig.putCustomTypeMappingIfAbsent(scalarName, "String");
            }
        }
        mappingConfig.putCustomTypeMappingIfAbsent("ID", "String");
        mappingConfig.putCustomTypeMappingIfAbsent("String", "String");
        mappingConfig.putCustomTypeMappingIfAbsent("Int", "Integer");
        mappingConfig.putCustomTypeMappingIfAbsent("Float", "Double");
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean", "Boolean");
    }

    private static Set<String> getAllTypeNames(Document document) {
        return document.getDefinitionsOfType(ObjectTypeDefinition.class)
                .stream()
                .filter(typeDef -> !Utils.isGraphqlOperation(typeDef.getName()))
                .map(ObjectTypeDefinition::getName)
                .collect(Collectors.toSet());
    }

    private static List<FieldDefinition> getRootTypeFieldDefinitions(ObjectTypeDefinition definition,
                                                                     Document document) {
        List<FieldDefinition> definitions = definition.getFieldDefinitions();
        // Merge all field definitions from the object type with field definitions from object type extension
        document.getDefinitions().stream()
                .filter(d -> ObjectTypeExtensionDefinition.class.isAssignableFrom(d.getClass()))
                .map(ObjectTypeExtensionDefinition.class::cast)
                .filter(d -> d.getName().equals(definition.getName()))
                .map(ObjectTypeExtensionDefinition::getFieldDefinitions)
                .forEach(definitions::addAll);
        return definitions;
    }

}
