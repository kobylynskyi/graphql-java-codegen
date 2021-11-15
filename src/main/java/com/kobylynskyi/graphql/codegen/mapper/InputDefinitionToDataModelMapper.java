package com.kobylynskyi.graphql.codegen.mapper;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.builders.JavaDocBuilder;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInputObjectTypeDefinition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.ANNOTATIONS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.INITIALIZE_NULLABLE_TYPES;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.BUILDER;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.ENUM_IMPORT_IT_SELF_IN_SCALA;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.EQUALS_AND_HASH_CODE;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.FIELDS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.GENERATED_ANNOTATION;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.GENERATED_INFO;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.GENERATE_MODEL_OPEN_CLASSES;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.IMMUTABLE_MODELS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.JAVA_DOC;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.PACKAGE;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.TO_STRING;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.TO_STRING_FOR_REQUEST;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.SUPPORT_UNKNOWN_FIELDS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.UNKNOWN_FIELDS_PROPERTY_NAME;

/**
 * Map input type definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class InputDefinitionToDataModelMapper {

    private final AnnotationsMapper annotationsMapper;
    private final DataModelMapper dataModelMapper;
    private final InputValueDefinitionToParameterMapper inputValueDefinitionToParameterMapper;

    public InputDefinitionToDataModelMapper(MapperFactory mapperFactory,
                                            InputValueDefinitionToParameterMapper inputValueDefToParamMapper) {
        this.annotationsMapper = mapperFactory.getAnnotationsMapper();
        this.dataModelMapper = mapperFactory.getDataModelMapper();
        this.inputValueDefinitionToParameterMapper = inputValueDefToParamMapper;
    }

    /**
     * Map input type definition to a Freemarker data model
     *
     * @param mappingContext Global mapping context
     * @param definition     Definition of input type including base definition and its extensions
     * @return Freemarker data model of the GraphQL type
     */
    public Map<String, Object> map(MappingContext mappingContext, ExtendedInputObjectTypeDefinition definition) {
        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, dataModelMapper.getModelClassNameWithPrefixAndSuffix(mappingContext, definition));
        dataModel.put(JAVA_DOC, JavaDocBuilder.build(definition));
        dataModel.put(NAME, definition.getName());
        List<ParameterDefinition> fields = inputValueDefinitionToParameterMapper
                .map(mappingContext, definition.getValueDefinitions(), definition.getName());
        if (mappingContext.isSupportUnknownFields()){
            ParameterDefinition unknownFields = new ParameterDefinition();
            unknownFields.setName(mappingContext.getUnknownFieldsPropertyName());
            unknownFields.setOriginalName(mappingContext.getUnknownFieldsPropertyName());
            unknownFields.setType("java.util.Map<String,Object>");
            unknownFields.setAnnotations(Arrays.asList(
                    JsonAnySetter.class.getName(),
                    JsonAnyGetter.class.getName()
            ));
            fields.add(unknownFields);
        }
        dataModel.put(FIELDS, fields);
        dataModel.put(ANNOTATIONS, annotationsMapper.getAnnotations(mappingContext, definition));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingContext.getGenerateEqualsAndHashCode());
        dataModel.put(IMMUTABLE_MODELS, mappingContext.getGenerateImmutableModels());
        dataModel.put(TO_STRING, mappingContext.getGenerateToString());
        dataModel.put(TO_STRING_FOR_REQUEST, mappingContext.getGenerateClient());
        dataModel.put(GENERATED_ANNOTATION, mappingContext.getAddGeneratedAnnotation());
        dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
        dataModel.put(ENUM_IMPORT_IT_SELF_IN_SCALA, mappingContext.getEnumImportItSelfInScala());
        dataModel.put(GENERATE_MODEL_OPEN_CLASSES, mappingContext.isGenerateModelOpenClasses());
        dataModel.put(INITIALIZE_NULLABLE_TYPES, mappingContext.isInitializeNullableTypes());
        dataModel.put(SUPPORT_UNKNOWN_FIELDS, mappingContext.isSupportUnknownFields());
        dataModel.put(UNKNOWN_FIELDS_PROPERTY_NAME, mappingContext.getUnknownFieldsPropertyName());


        return dataModel;
    }

}
