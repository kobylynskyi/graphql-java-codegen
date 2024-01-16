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
import java.util.Set;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.GENERATED_ANNOTATION;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.GENERATED_INFO;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.IMPORTS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.PACKAGE;

/**
 * Generates files for inputs
 */
public class InputWrapperGenerator implements FilesGenerator {

    public static final String CLASS_NAME_GRAPHQL_INPUT_PARAMETER = "GraphQLInputParameter";
    private final MappingContext mappingContext;

    public InputWrapperGenerator(MappingContext mappingContext) {
        this.mappingContext = mappingContext;
    }

    @Override
    public List<File> generate() {
        List<File> generatedFiles = new ArrayList<>();
        if (Boolean.TRUE.equals(mappingContext.getUseWrapperForNullableInputTypes())) {
            String packageName = DataModelMapper.getModelPackageName(mappingContext);
            Set<String> imports = DataModelMapper.getImports(mappingContext, packageName);

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put(NAME, CLASS_NAME_GRAPHQL_INPUT_PARAMETER);
            dataModel.put(CLASS_NAME, CLASS_NAME_GRAPHQL_INPUT_PARAMETER);
            dataModel.put(PACKAGE, packageName);
            dataModel.put(IMPORTS, imports);
            dataModel.put(GENERATED_ANNOTATION, mappingContext.getAddGeneratedAnnotation());
            dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
            generatedFiles.add(FreeMarkerTemplateFilesCreator
                    .create(mappingContext, FreeMarkerTemplateType.GRAPHQL_INPUT_PARAMETER, dataModel));
        }

        return generatedFiles;
    }

}
