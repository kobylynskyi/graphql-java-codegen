package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.Parameter;
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

    public static List<Parameter> map(MappingConfig mappingConfig, List<InputValueDefinition> valueDefinitions) {
        if (valueDefinitions == null) {
            return Collections.emptyList();
        }
        return valueDefinitions.stream()
                .map(inputValueDefinition -> map(mappingConfig, inputValueDefinition))
                .collect(Collectors.toList());
    }

    public static Parameter map(MappingConfig mappingConfig, InputValueDefinition inputValueDefinition) {
        Parameter parameter = new Parameter();
        parameter.setName(MapperUtils.capitalizeIfRestricted(inputValueDefinition.getName()));
        parameter.setType(GraphqlTypeToJavaTypeMapper.mapToJavaType(mappingConfig, inputValueDefinition.getType()));
        return parameter;
    }

}
