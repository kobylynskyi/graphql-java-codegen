package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import graphql.language.Directive;
import graphql.language.DirectivesContainer;
import graphql.language.InputValueDefinition;

import java.util.List;

import static com.kobylynskyi.graphql.codegen.mapper.GraphqlTypeToJavaTypeMapper.getAnnotations;
import static com.kobylynskyi.graphql.codegen.mapper.GraphqlTypeToJavaTypeMapper.getJavaType;
import static java.util.stream.Collectors.toList;

/**
 * Mapper from GraphQL's InputValueDefinition to a Freemarker-understandable format
 *
 * @author kobylynskyi
 */
public class InputValueDefinitionToParameterMapper {

    /**
     * Map input value definition to a Freemarker-understandable data model type
     *
     * @param mappingConfig    Global mapping configuration
     * @param valueDefinitions List of GraphQL value definitions
     * @param parentTypeName   Name of the parent GraphQL type
     * @return Freemarker data model of the GraphQL input value definition
     */
    public static List<ParameterDefinition> map(MappingConfig mappingConfig, List<InputValueDefinition> valueDefinitions, String parentTypeName) {
        return valueDefinitions.stream()
                .map(inputValueDef -> map(mappingConfig, inputValueDef, parentTypeName))
                .collect(toList());
    }

    /**
     * Map GraphQL's InputValueDefinition to a Freemarker-understandable format of operation
     *
     * @param mappingConfig        Global mapping configuration
     * @param inputValueDefinition GraphQL input value definition
     * @param parentTypeName       Name of the parent type
     * @return Freemarker-understandable format of parameter (field)
     */
    private static ParameterDefinition map(MappingConfig mappingConfig, InputValueDefinition inputValueDefinition, String parentTypeName) {
        ParameterDefinition parameter = new ParameterDefinition();
        parameter.setName(MapperUtils.capitalizeIfRestricted(inputValueDefinition.getName()));
        parameter.setType(getJavaType(mappingConfig, inputValueDefinition.getType()));
        parameter.setDefaultValue(DefaultValueMapper.map(mappingConfig, inputValueDefinition.getDefaultValue(), inputValueDefinition.getType()));
        parameter.setAnnotations(getAnnotations(mappingConfig, inputValueDefinition.getType(), inputValueDefinition.getName(), parentTypeName, false));
        parameter.setDeprecated(isDeprecated(inputValueDefinition));
        return parameter;
    }

    private static boolean isDeprecated(DirectivesContainer<?> node) {
        return node.getDirectives().stream()
                .map(Directive::getName)
                .anyMatch(Deprecated.class.getSimpleName()::equalsIgnoreCase);
    }

}
