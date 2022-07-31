package com.kobylynskyi.graphql.codegen.generators.impl;

import com.kobylynskyi.graphql.codegen.generators.FilesGenerator;
import com.kobylynskyi.graphql.codegen.generators.FreeMarkerTemplateFilesCreator;
import com.kobylynskyi.graphql.codegen.generators.FreeMarkerTemplateType;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapperFactory;
import com.kobylynskyi.graphql.codegen.mapper.FieldDefinitionsToResolverDataModelMapper;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInterfaceTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Generates files for field resolvers
 */
public class FieldResolversGenerator implements FilesGenerator {

    private final MappingContext mappingContext;
    private final FieldDefinitionsToResolverDataModelMapper fieldDefinitionsToResolverMapper;

    public FieldResolversGenerator(MappingContext mappingContext,
                                   DataModelMapperFactory dataModelMapperFactory) {
        this.mappingContext = mappingContext;
        this.fieldDefinitionsToResolverMapper = dataModelMapperFactory.getFieldDefinitionsToResolverMapper();
    }

    @Override
    public List<File> generate() {
        List<File> generatedFiles = new ArrayList<>();
        for (ExtendedObjectTypeDefinition definition : mappingContext.getDocument().getTypeDefinitions()) {
            generatedFiles.addAll(generate(definition.getFieldDefinitions(), definition));
        }
        for (ExtendedInterfaceTypeDefinition definition : mappingContext.getDocument().getInterfaceDefinitions()) {
            generatedFiles.addAll(generate(definition.getFieldDefinitions(), definition));
        }
        return generatedFiles;
    }

    private List<File> generate(List<ExtendedFieldDefinition> fieldDefinitions,
                                ExtendedDefinition<?, ?> parentDefinition) {
        if (!Boolean.TRUE.equals(mappingContext.getGenerateApis())) {
            return Collections.emptyList();
        }
        Set<String> fieldNamesWithResolvers = mappingContext.getFieldNamesWithResolvers();
        List<ExtendedFieldDefinition> fieldDefsWithResolvers = fieldDefinitions.stream()
                .filter(fieldDef -> fieldNamesWithResolvers.contains(parentDefinition.getName() + "." + fieldDef.getName()))
                .collect(toList());

        List<File> generatedFiles = new ArrayList<>();
        if (!fieldDefsWithResolvers.isEmpty()) {
            Map<String, Object> dataModel = fieldDefinitionsToResolverMapper
                    .mapToTypeResolver(mappingContext, fieldDefsWithResolvers, parentDefinition.getName());
            File file = FreeMarkerTemplateFilesCreator.create(
                    mappingContext, FreeMarkerTemplateType.OPERATIONS, dataModel);
            generatedFiles.add(file);
        }
        return generatedFiles;
    }
}
