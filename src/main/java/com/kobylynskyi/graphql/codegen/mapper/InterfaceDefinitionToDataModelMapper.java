package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInterfaceTypeDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;

/**
 * Map interface definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class InterfaceDefinitionToDataModelMapper {

    private final GraphQLTypeMapper graphQLTypeMapper;
    private final DataModelMapper dataModelMapper;
    private final FieldDefinitionToParameterMapper fieldDefinitionToParameterMapper;

    public InterfaceDefinitionToDataModelMapper(GraphQLTypeMapper graphQLTypeMapper,
                                                DataModelMapper dataModelMapper,
                                                FieldDefinitionToParameterMapper fieldDefinitionToParameterMapper) {
        this.graphQLTypeMapper = graphQLTypeMapper;
        this.dataModelMapper = dataModelMapper;
        this.fieldDefinitionToParameterMapper = fieldDefinitionToParameterMapper;
    }

    /**
     * Map interface definition to a Freemarker data model
     *
     * @param mappingContext Global mapping context
     * @param definition     Definition of interface type including base definition and its extensions
     * @return Freemarker data model of the GraphQL interface
     */
    public Map<String, Object> map(MappingContext mappingContext, ExtendedInterfaceTypeDefinition definition) {
        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        String clazzName  = dataModelMapper.getModelClassNameWithPrefixAndSuffix(mappingContext, definition);
        List<ParameterDefinition> fields = fieldDefinitionToParameterMapper.mapFields(mappingContext, definition.getFieldDefinitions(), definition);
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, clazzName);
        dataModel.put(JAVA_DOC, definition.getJavaDoc());
        dataModel.put(ANNOTATIONS, graphQLTypeMapper.getAnnotations(mappingContext, definition));
        dataModel.put(FIELDS, fields);
        dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
        dataModel.put(ENUM_IMPORT_IT_SELF_IN_SCALA, mappingContext.getEnumImportItSelfInScala());
        dataModel.put(IMMUTABLE_MODELS, mappingContext.getGenerateImmutableModels());
        if (mappingContext.getParentInterfacePropertiesInKotlin().containsKey(clazzName)) {
            mappingContext.getParentInterfacePropertiesInKotlin().get(clazzName).addAll(fields.stream().map(ParameterDefinition::getName).collect(Collectors.toSet()));
        } else {
            mappingContext.getParentInterfacePropertiesInKotlin().put(clazzName, fields.stream().map(ParameterDefinition::getName).collect(Collectors.toSet()));
        }
        return dataModel;
    }

}
