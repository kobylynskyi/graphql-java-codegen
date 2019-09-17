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

    public static List<ParameterDefinition> map(MappingConfig mappingConfig, List<InputValueDefinition> valueDefinitions) {
        if (valueDefinitions == null) {
            return Collections.emptyList();
        }
        return valueDefinitions.stream()
                .map(inputValueDefinition -> GraphqlTypeToJavaTypeMapper.map(mappingConfig, inputValueDefinition))
                .collect(Collectors.toList());
    }

}
