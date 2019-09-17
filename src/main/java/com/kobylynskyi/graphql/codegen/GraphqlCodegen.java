package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.mapper.*;
import com.kobylynskyi.graphql.codegen.model.DefinitionType;
import com.kobylynskyi.graphql.codegen.model.DefinitionTypeDeterminer;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import freemarker.template.TemplateException;
import graphql.language.*;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
 */
@Getter
@Setter
public class GraphqlCodegen {

    private List<String> schemas;
    private File outputDir;
    private MappingConfig mappingConfig;

    public GraphqlCodegen(List<String> schemas, File outputDir, MappingConfig mappingConfig) {
        this.schemas = schemas;
        this.outputDir = outputDir;
        this.mappingConfig = mappingConfig;
    }

    public void generate() throws Exception {
        GraphqlCodegenFileCreator.prepareOutputDir(outputDir);
        for (String schema : schemas) {
            long startTime = System.currentTimeMillis();
            Document document = GraphqlDocumentParser.getDocument(schema);
            addScalarsToCustomMappingConfig(document);
            processDocument(document);
            System.out.println(String.format("Finished processing schema '%s' in %d ms", schema, System.currentTimeMillis() - startTime));
        }
    }

    private void processDocument(Document document) throws IOException, TemplateException {
        for (Definition definition : document.getDefinitions()) {
            DefinitionType definitionType = DefinitionTypeDeterminer.determine(definition);
            switch (definitionType) {
                case OPERATION:
                    generateOperation((ObjectTypeDefinition) definition);
                    break;
                case TYPE:
                    generateType((ObjectTypeDefinition) definition, document);
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
        if (mappingConfig.isGenerateApis()) {
            Map<String, Object> dataModel = InterfaceDefinitionToDataModelMapper.map(mappingConfig, definition);
            GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.interfaceTemplate, dataModel, outputDir);
        }
    }

    private void generateOperation(ObjectTypeDefinition definition) throws IOException, TemplateException {
        if (mappingConfig.isGenerateApis()) {
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

    private void generateInput(InputObjectTypeDefinition definition) throws IOException, TemplateException {
        Map<String, Object> dataModel = InputDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.typeTemplate, dataModel, outputDir);
    }

    private void generateEnum(EnumTypeDefinition definition) throws IOException, TemplateException {
        Map<String, Object> dataModel = EnumDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.enumTemplate, dataModel, outputDir);
    }

    private void addScalarsToCustomMappingConfig(Document document) {
        for (Definition definition : document.getDefinitions()) {
            if (definition instanceof ScalarTypeDefinition) {
                String scalarName = ((ScalarTypeDefinition) definition).getName();
                mappingConfig.putCustomTypeMappingIfAbsent(scalarName, "String");
            }
        }
    }

}
