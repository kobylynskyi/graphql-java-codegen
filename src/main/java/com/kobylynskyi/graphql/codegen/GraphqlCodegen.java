package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.mapper.*;
import com.kobylynskyi.graphql.codegen.model.*;
import com.kobylynskyi.graphql.codegen.supplier.MappingConfigSupplier;
import freemarker.template.TemplateException;
import graphql.language.*;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Generator of:
 * - Interface for each GraphQL query
 * - Interface for each GraphQL mutation
 * - Interface for each GraphQL subscription
 * - Class for each GraphQL data type
 * - Class for each GraphQL enum type
 * - Class for each GraphQL scalar type
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
    private MappingConfig result;

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
        if (mappingConfig.getGenerateEqualsAndHashCode() == null) {
            mappingConfig.setGenerateEqualsAndHashCode(DefaultMappingConfigValues.DEFAULT_EQUALS_AND_HASHCODE);
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
    }


    public void generate() throws Exception {
        GraphqlCodegenFileCreator.prepareOutputDir(outputDir);
        long startTime = System.currentTimeMillis();
        if (!schemas.isEmpty()) {
            Document document = GraphqlDocumentParser.getDocument(schemas);
            addScalarsToCustomMappingConfig(document);
            processDocument(document);
        }
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Finished processing %d schemas in %d ms", schemas.size(), elapsed));
    }

    private void processDocument(Document document) throws IOException, TemplateException {
        for (Definition<?> definition : document.getDefinitions()) {
            GraphqlDefinitionType definitionType;
            try {
                definitionType = DefinitionTypeDeterminer.determine(definition);
            } catch (UnsupportedGraphqlDefinitionException ex) {
                continue;
            }
            switch (definitionType) {
                case OPERATION:
                    generateOperation((ObjectTypeDefinition) definition);
                    break;
                case TYPE:
                    generateType((ObjectTypeDefinition) definition, document);
                    generateFieldResolvers((ObjectTypeDefinition) definition);
                    break;
                case INTERFACE:
                    generateInterface((InterfaceTypeDefinition) definition);
                    break;
                case ENUM:
                    generateEnum((EnumTypeDefinition) definition);
                    break;
                case INPUT:
                    generateInput((InputObjectTypeDefinition) definition);
                    break;
                case UNION:
                    generateUnion((UnionTypeDefinition) definition);
            }
        }
        System.out.println(String.format("Generated %d definitions in folder '%s'", document.getDefinitions().size(),
                outputDir.getAbsolutePath()));
    }

    private void generateUnion(UnionTypeDefinition definition) throws IOException, TemplateException {
        Map<String, Object> dataModel = UnionDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.unionTemplate, dataModel, outputDir);
    }

    private void generateInterface(InterfaceTypeDefinition definition) throws IOException, TemplateException {
        Map<String, Object> dataModel = InterfaceDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.interfaceTemplate, dataModel, outputDir);
    }

    private void generateOperation(ObjectTypeDefinition definition) throws IOException, TemplateException {
        if (Boolean.TRUE.equals(mappingConfig.getGenerateApis())) {
            for (FieldDefinition fieldDef : definition.getFieldDefinitions()) {
                Map<String, Object> dataModel = FieldDefinitionToDataModelMapper.map(mappingConfig, fieldDef, definition.getName());
                GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.operationsTemplate, dataModel, outputDir);
            }
            // We need to generate a root object to workaround https://github.com/facebook/relay/issues/112
            Map<String, Object> dataModel = ObjectDefinitionToDataModelMapper.map(mappingConfig, definition);
            GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.operationsTemplate, dataModel, outputDir);
        }
    }

    private void generateType(ObjectTypeDefinition definition, Document document) throws IOException, TemplateException {
        Map<String, Object> dataModel = TypeDefinitionToDataModelMapper.map(mappingConfig, definition, document);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.typeTemplate, dataModel, outputDir);
    }

    private void generateFieldResolvers(ObjectTypeDefinition definition) throws IOException, TemplateException {
        List<FieldDefinition> fieldDefsWithResolvers = definition.getFieldDefinitions().stream()
                .filter(fieldDef -> FieldDefinitionToParameterMapper.generateResolversForField(mappingConfig, fieldDef, definition.getName()))
                .collect(toList());
        if (!fieldDefsWithResolvers.isEmpty()) {
            Map<String, Object> dataModel = FieldResolverDefinitionToDataModelMapper.map(mappingConfig, fieldDefsWithResolvers, definition.getName());
            GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.fieldsResolverTemplate, dataModel, outputDir);
        }
    }

    private void generateInput(InputObjectTypeDefinition definition) throws IOException, TemplateException {
        Map<String, Object> dataModel = InputDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.typeTemplate, dataModel, outputDir);
    }

    private void generateEnum(EnumTypeDefinition definition) throws IOException, TemplateException {
        Map<String, Object> dataModel = EnumDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.enumTemplate, dataModel, outputDir);
    }

    private void addScalarsToCustomMappingConfig(Document document) {
        for (Definition<?> definition : document.getDefinitions()) {
            if (definition instanceof ScalarTypeDefinition) {
                String scalarName = ((ScalarTypeDefinition) definition).getName();
                mappingConfig.putCustomTypeMappingIfAbsent(scalarName, "String");
            }
        }
    }
}
