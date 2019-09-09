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

    /**
     * Map field definition to a Freemarker-understandable data model type
     *
     * @param mappingConfig    Global mapping configuration
     * @param fieldDefinitions List of GraphQL field definitions
     * @param parentTypeName   Name of the parent GraphQL type
     * @return Freemarker data model of the GraphQL interface
     */
    public static List<Parameter> map(MappingConfig mappingConfig,
                                      List<FieldDefinition> fieldDefinitions,
                                      String parentTypeName) {
        if (fieldDefinitions == null) {
            return Collections.emptyList();
        }
        return fieldDefinitions.stream()
                .map(fieldDefinition -> map(mappingConfig, fieldDefinition, parentTypeName))
                .collect(Collectors.toList());
    }

    private static Parameter map(MappingConfig mappingConfig, FieldDefinition fieldDef, String parentTypeName) {
        Parameter parameter = new Parameter();
        parameter.setName(MapperUtils.capitalizeIfRestricted(fieldDef.getName()));
        parameter.setType(GraphqlTypeToJavaTypeMapper.mapToJavaType(mappingConfig,
                fieldDef.getType(), fieldDef.getName(), parentTypeName));
        return parameter;
    }

}
