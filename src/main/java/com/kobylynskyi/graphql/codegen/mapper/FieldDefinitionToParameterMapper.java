package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
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
     * @return Freemarker data model of the GraphQL field definition
     */
    public static List<ParameterDefinition> map(MappingConfig mappingConfig,
                                                List<FieldDefinition> fieldDefinitions,
                                                String parentTypeName) {
        if (fieldDefinitions == null) {
            return Collections.emptyList();
        }
        return fieldDefinitions.stream()
                .map(fieldDefinition -> GraphqlTypeToJavaTypeMapper.map(mappingConfig, fieldDefinition, parentTypeName))
                .collect(Collectors.toList());
    }

}
