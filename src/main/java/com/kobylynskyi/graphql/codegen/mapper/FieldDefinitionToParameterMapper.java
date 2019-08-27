package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.Parameter;
import graphql.language.FieldDefinition;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper from GraphQL's FieldDefinition to a Freemarker-understandable format
 *
 * @author kobylynskyi
 */
public class FieldDefinitionToParameterMapper {

    public static List<Parameter> map(MappingConfig mappingConfig, List<FieldDefinition> fieldDefinitions) {
        if (fieldDefinitions == null) {
            return Collections.emptyList();
        }
        return fieldDefinitions.stream()
                .map(fieldDefinition -> map(mappingConfig, fieldDefinition))
                .collect(Collectors.toList());
    }

    private static Parameter map(MappingConfig mappingConfig, FieldDefinition fieldDefinition) {
        Parameter parameter = new Parameter();
        parameter.setName(MapperUtils.capitalizeIfRestricted(fieldDefinition.getName()));
        parameter.setType(GraphqlTypeToJavaTypeMapper.mapToJavaType(mappingConfig, fieldDefinition.getType()));
        return parameter;
    }

}
