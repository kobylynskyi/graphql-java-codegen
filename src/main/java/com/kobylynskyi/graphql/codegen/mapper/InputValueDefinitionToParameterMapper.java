package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import graphql.language.InputValueDefinition;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        if (valueDefinitions == null) {
            return Collections.emptyList();
        }
        return valueDefinitions.stream()
                .map(inputValueDefinition -> GraphqlTypeToJavaTypeMapper.map(mappingConfig, inputValueDefinition, parentTypeName))
                .collect(Collectors.toList());
    }

}
