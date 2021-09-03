package com.kobylynskyi.graphql.codegen.generators.impl;

import com.kobylynskyi.graphql.codegen.generators.FilesGenerator;
import com.kobylynskyi.graphql.codegen.generators.FreeMarkerTemplateFilesCreator;
import com.kobylynskyi.graphql.codegen.generators.FreeMarkerTemplateType;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapperFactory;
import com.kobylynskyi.graphql.codegen.model.ApiInterfaceStrategy;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;
import graphql.language.FieldDefinition;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Generates files for oeprations
 */
public class OperationsGenerator implements FilesGenerator {

    private final MappingContext mappingContext;
    private final DataModelMapperFactory dataModelMapperFactory;

    public OperationsGenerator(MappingContext mappingContext,
                               DataModelMapperFactory dataModelMapperFactory) {
        this.mappingContext = mappingContext;
        this.dataModelMapperFactory = dataModelMapperFactory;
    }

    @Override
    public List<File> generate() {
        if (!Boolean.TRUE.equals(mappingContext.getGenerateApis())) {
            return Collections.emptyList();
        }
        List<File> generatedFiles = new ArrayList<>();
        for (ExtendedObjectTypeDefinition definition : mappingContext.getDocument().getOperationDefinitions()) {
            generatedFiles.addAll(generateServerOperations(definition));
        }
        return generatedFiles;
    }

    private List<File> generateServerOperations(ExtendedObjectTypeDefinition definition) {
        List<File> generatedFiles = new ArrayList<>();
        // Generate a root interface with all operations inside
        // Relates to https://github.com/facebook/relay/issues/112
        switch (mappingContext.getApiRootInterfaceStrategy()) {
            case INTERFACE_PER_SCHEMA:
                for (ExtendedObjectTypeDefinition defInFile : definition.groupBySourceLocationFile().values()) {
                    generatedFiles.add(generateRootApi(defInFile));
                }
                break;
            case DO_NOT_GENERATE:
                break;
            case SINGLE_INTERFACE:
            default:
                generatedFiles.add(generateRootApi(definition));
                break;
        }

        if (mappingContext.getApiInterfaceStrategy() == ApiInterfaceStrategy.INTERFACE_PER_OPERATION) {
            // Generate separate interfaces for all queries, mutations and subscriptions
            List<String> fieldNames = definition.getFieldDefinitions().stream().map(FieldDefinition::getName)
                    .collect(toList());
            switch (mappingContext.getApiNamePrefixStrategy()) {
                case FOLDER_NAME_AS_PREFIX:
                    for (ExtendedObjectTypeDefinition fileDef : definition.groupBySourceLocationFolder().values()) {
                        generatedFiles.addAll(generateApis(fileDef, fieldNames));
                    }
                    break;
                case FILE_NAME_AS_PREFIX:
                    for (ExtendedObjectTypeDefinition fileDef : definition.groupBySourceLocationFile().values()) {
                        generatedFiles.addAll(generateApis(fileDef, fieldNames));
                    }
                    break;
                case CONSTANT:
                default:
                    generatedFiles.addAll(generateApis(definition, fieldNames));
                    break;
            }
        }
        return generatedFiles;
    }

    private List<File> generateApis(ExtendedObjectTypeDefinition definition,
                                    List<String> fieldNames) {
        List<File> generatedFiles = new ArrayList<>();
        for (ExtendedFieldDefinition operationDef : definition.getFieldDefinitions()) {
            Map<String, Object> dataModel = dataModelMapperFactory.getFieldDefinitionsToResolverMapper()
                    .mapRootTypeField(mappingContext, operationDef,
                            definition.getName(), fieldNames);
            generatedFiles.add(FreeMarkerTemplateFilesCreator
                    .create(mappingContext, FreeMarkerTemplateType.OPERATIONS, dataModel));
        }
        return generatedFiles;
    }

    private File generateRootApi(ExtendedObjectTypeDefinition definition) {
        Map<String, Object> dataModel = dataModelMapperFactory.getFieldDefinitionsToResolverMapper()
                .mapRootTypeFields(mappingContext, definition);
        return FreeMarkerTemplateFilesCreator
                .create(mappingContext, FreeMarkerTemplateType.OPERATIONS, dataModel);
    }

}
