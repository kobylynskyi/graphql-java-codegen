package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import graphql.language.Directive;
import graphql.language.DirectivesContainer;
import graphql.language.InputValueDefinition;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Mapper from GraphQL's InputValueDefinition to a Freemarker-understandable format
 *
 * @author kobylynskyi
 */
public class InputValueDefinitionToParameterMapper {

    private InputValueDefinitionToParameterMapper() {
    }

    /**
     * Map input value definition to a Freemarker-understandable data model type
     *
     * @param mappingContext   Global mapping context
     * @param valueDefinitions List of GraphQL value definitions
     * @param parentTypeName   Name of the parent GraphQL type
     * @return Freemarker data model of the GraphQL input value definition
     */
    public static List<ParameterDefinition> map(MappingContext mappingContext, List<InputValueDefinition> valueDefinitions, String parentTypeName) {
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
    private static ParameterDefinition map(MappingContext mappingContext, InputValueDefinition inputValueDefinition, String parentTypeName) {
        ParameterDefinition parameter = new ParameterDefinition();
        parameter.setName(MapperUtils.capitalizeIfRestricted(inputValueDefinition.getName()));
        parameter.setOriginalName(inputValueDefinition.getName());
        parameter.setType(GraphqlTypeToJavaTypeMapper.getJavaType(mappingContext, inputValueDefinition.getType(), inputValueDefinition.getName(), parentTypeName).getName());
        parameter.setDefaultValue(ValueMapper.map(mappingContext, inputValueDefinition.getDefaultValue(), inputValueDefinition.getType()));
        parameter.setAnnotations(GraphqlTypeToJavaTypeMapper.getAnnotations(mappingContext, inputValueDefinition.getType(), inputValueDefinition, parentTypeName, false));
        parameter.setDeprecated(isDeprecated(inputValueDefinition));
        return parameter;
    }

    private static boolean isDeprecated(DirectivesContainer<?> node) {
        return node.getDirectives().stream()
                .map(Directive::getName)
                .anyMatch(Deprecated.class.getSimpleName()::equalsIgnoreCase);
    }

}
