package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.NamedDefinition;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import graphql.language.InputValueDefinition;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Mapper from GraphQL's InputValueDefinition to a Freemarker-understandable format
 *
 * @author kobylynskyi
 */
public class InputValueDefinitionToParameterMapper {

    private final ValueMapper valueMapper;
    private final GraphQLTypeMapper graphQLTypeMapper;
    private final DataModelMapper dataModelMapper;

    public InputValueDefinitionToParameterMapper(ValueMapper valueMapper,
                                                 GraphQLTypeMapper graphQLTypeMapper,
                                                 DataModelMapper dataModelMapper) {
        this.valueMapper = valueMapper;
        this.graphQLTypeMapper = graphQLTypeMapper;
        this.dataModelMapper = dataModelMapper;
    }

    /**
     * Map input value definition to a Freemarker-understandable data model type
     *
     * @param mappingContext   Global mapping context
     * @param valueDefinitions List of GraphQL value definitions
     * @param parentTypeName   Name of the parent GraphQL type
     * @return Freemarker data model of the GraphQL input value definition
     */
    public List<ParameterDefinition> map(MappingContext mappingContext, List<InputValueDefinition> valueDefinitions, String parentTypeName) {
        return valueDefinitions.stream()
                .map(inputValueDef -> map(mappingContext, inputValueDef, parentTypeName))
                .collect(toList());
    }

    /**
     * Map GraphQL's InputValueDefinition to a Freemarker-understandable format of operation
     *
     * @param mappingContext       Global mapping context
     * @param inputValueDefinition GraphQL input value definition
     * @param parentTypeName       Name of the parent type
     * @return Freemarker-understandable format of parameter (field)
     */
    private ParameterDefinition map(MappingContext mappingContext, InputValueDefinition inputValueDefinition, String parentTypeName) {
        NamedDefinition namedDefinition = graphQLTypeMapper.getLanguageType(mappingContext, inputValueDefinition.getType(), inputValueDefinition.getName(), parentTypeName);

        ParameterDefinition parameter = new ParameterDefinition();
        parameter.setName(dataModelMapper.capitalizeIfRestricted(mappingContext, inputValueDefinition.getName()));
        parameter.setOriginalName(inputValueDefinition.getName());
        parameter.setType(graphQLTypeMapper.getTypeConsideringPrimitive(mappingContext, namedDefinition, namedDefinition.getJavaName()));
        parameter.setDefaultValue(valueMapper.map(mappingContext, inputValueDefinition.getDefaultValue(), inputValueDefinition.getType()));
        parameter.setAnnotations(graphQLTypeMapper.getAnnotations(mappingContext, inputValueDefinition.getType(), inputValueDefinition, parentTypeName, false));
        parameter.setDeprecated(graphQLTypeMapper.getDeprecated(mappingContext, inputValueDefinition));
        parameter.setMandatory(namedDefinition.isMandatory());
        parameter.setSerializeUsingObjectMapper(namedDefinition.isSerializeUsingObjectMapper());
        return parameter;
    }

}
