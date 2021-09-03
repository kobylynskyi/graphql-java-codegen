package com.kobylynskyi.graphql.codegen.generators.impl;

import com.kobylynskyi.graphql.codegen.generators.FilesGenerator;
import com.kobylynskyi.graphql.codegen.generators.FreeMarkerTemplateFilesCreator;
import com.kobylynskyi.graphql.codegen.generators.FreeMarkerTemplateType;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.model.MappingContext;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.GENERATED_ANNOTATION;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.GENERATED_INFO;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.MODEL_NAME_PREFIX;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.MODEL_NAME_SUFFIX;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.PACKAGE;

/**
 * Generates a JacksonTypeIdResolver file
 */
public class JacksonTypeIdResolverGenerator implements FilesGenerator {

    private static final String CLASS_NAME_GRAPHQL_JACKSON_TYPE_ID_RESOLVER = "GraphqlJacksonTypeIdResolver";

    private final MappingContext mappingContext;

    public JacksonTypeIdResolverGenerator(MappingContext mappingContext) {
        this.mappingContext = mappingContext;
    }

    @Override
    public List<File> generate() {
        List<File> generatedFiles = new ArrayList<>(1);
        if (Boolean.TRUE.equals(mappingContext.getGenerateJacksonTypeIdResolver())) {
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
            dataModel.put(MODEL_NAME_PREFIX, mappingContext.getModelNamePrefix());
            dataModel.put(MODEL_NAME_SUFFIX, mappingContext.getModelNameSuffix());
            dataModel.put(CLASS_NAME, CLASS_NAME_GRAPHQL_JACKSON_TYPE_ID_RESOLVER);
            dataModel.put(GENERATED_ANNOTATION, mappingContext.getAddGeneratedAnnotation());
            dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
            File file = FreeMarkerTemplateFilesCreator.create(mappingContext, FreeMarkerTemplateType.JACKSON_TYPE_ID_RESOLVER, dataModel);
            generatedFiles.add(file);
        }
        return generatedFiles;
    }

}
