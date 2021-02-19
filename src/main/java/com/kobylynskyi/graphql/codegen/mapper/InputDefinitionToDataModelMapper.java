package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInputObjectTypeDefinition;

import java.util.HashMap;
import java.util.Map;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;

/**
 * Map input type definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class InputDefinitionToDataModelMapper {

    private final GraphQLTypeMapper graphQLTypeMapper;
    private final DataModelMapper dataModelMapper;
    private final InputValueDefinitionToParameterMapper inputValueDefinitionToParameterMapper;

    public InputDefinitionToDataModelMapper(GraphQLTypeMapper graphQLTypeMapper,
                                            DataModelMapper dataModelMapper,
                                            InputValueDefinitionToParameterMapper inputValueDefinitionToParameterMapper) {
        this.graphQLTypeMapper = graphQLTypeMapper;
        this.dataModelMapper = dataModelMapper;
        this.inputValueDefinitionToParameterMapper = inputValueDefinitionToParameterMapper;
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
        dataModel.put(JAVA_DOC, definition.getJavaDoc());
        dataModel.put(NAME, definition.getName());
        dataModel.put(FIELDS, inputValueDefinitionToParameterMapper.map(mappingContext, definition.getValueDefinitions(), definition.getName()));
        dataModel.put(ANNOTATIONS, graphQLTypeMapper.getAnnotations(mappingContext, definition));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingContext.getGenerateEqualsAndHashCode());
        dataModel.put(IMMUTABLE_MODELS, mappingContext.getGenerateImmutableModels());
        dataModel.put(TO_STRING, mappingContext.getGenerateToString());
        dataModel.put(TO_STRING_FOR_REQUEST, mappingContext.getGenerateClient());
        dataModel.put(GENERATED_ANNOTATION, mappingContext.getAddGeneratedAnnotation());
        dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
        dataModel.put(ENUM_IMPORT_IT_SELF_IN_SCALA, mappingContext.getEnumImportItSelfInScala());
        dataModel.put(GENERATE_MODEL_OPEN_CLASSES, mappingContext.isGenerateModelOpenClasses());
        return dataModel;
    }

}
