package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.ProjectionParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.FieldDefinition;

import java.util.List;
import java.util.Set;

import static com.kobylynskyi.graphql.codegen.mapper.GraphqlTypeToJavaTypeMapper.*;
import static java.util.stream.Collectors.toList;

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
    public static List<ParameterDefinition> mapFields(MappingConfig mappingConfig,
                                                      List<ExtendedFieldDefinition> fieldDefinitions,
                                                      String parentTypeName) {
        return fieldDefinitions.stream()
                .filter(fieldDef -> !generateResolversForField(mappingConfig, fieldDef, parentTypeName))
                .map(fieldDef -> mapField(mappingConfig, fieldDef, parentTypeName))
                .collect(toList());
    }

    /**
     * Map field definition to a Freemarker-understandable data model type
     *
     * @param mappingConfig    Global mapping configuration
     * @param fieldDefinitions List of GraphQL field definitions
     * @param parentTypeName   Name of the parent GraphQL type
     * @param typeNames        Names of all GraphQL types
     * @return Freemarker data model of the GraphQL field definition
     */
    public static List<ProjectionParameterDefinition> mapProjectionFields(MappingConfig mappingConfig,
                                                                          List<ExtendedFieldDefinition> fieldDefinitions,
                                                                          String parentTypeName, Set<String> typeNames) {
        return fieldDefinitions.stream()
                .map(fieldDef -> mapProjectionField(mappingConfig, fieldDef, parentTypeName, typeNames))
                .collect(toList());
    }

    /**
     * Map GraphQL's FieldDefinition to a Freemarker-understandable format of parameter
     *
     * @param mappingConfig  Global mapping configuration
     * @param fieldDef       GraphQL field definition
     * @param parentTypeName Name of the parent type
     * @return Freemarker-understandable format of parameter (field)
     */
    private static ParameterDefinition mapField(MappingConfig mappingConfig, ExtendedFieldDefinition fieldDef,
                                                String parentTypeName) {
        ParameterDefinition parameter = new ParameterDefinition();
        parameter.setName(MapperUtils.capitalizeIfRestricted(fieldDef.getName()));
        parameter.setType(getJavaType(mappingConfig, fieldDef.getType(), fieldDef.getName(), parentTypeName));
        parameter.setAnnotations(getAnnotations(mappingConfig, fieldDef.getType(), fieldDef.getName(), parentTypeName, false));
        parameter.setJavaDoc(fieldDef.getJavaDoc());
        return parameter;
    }

    /**
     * Map GraphQL's FieldDefinition to a Freemarker-understandable format of parameter
     *
     * @param mappingConfig  Global mapping configuration
     * @param fieldDef       GraphQL field definition
     * @param parentTypeName Name of the parent type
     * @param typeNames      Names of all GraphQL types
     * @return Freemarker-understandable format of parameter (field)
     */
    private static ProjectionParameterDefinition mapProjectionField(MappingConfig mappingConfig, FieldDefinition fieldDef, String parentTypeName, Set<String> typeNames) {
        ProjectionParameterDefinition parameter = new ProjectionParameterDefinition();
        parameter.setName(MapperUtils.capitalizeIfRestricted(fieldDef.getName()));
        String nestedType = getNestedTypeName(fieldDef.getType());
        if (typeNames.contains(nestedType)) {
            parameter.setType(nestedType + mappingConfig.getResponseProjectionSuffix());
        }
        return parameter;
    }

    /**
     * Check whether FieldResolver should be generated for a given field.
     *
     * @param mappingConfig  Global mapping configuration
     * @param fieldDef       GraphQL field definition
     * @param parentTypeName Name of the parent type
     * @return <code>true</code> if FieldResolver will be generated for the field. <code>false</code> otherwise
     */
    public static boolean generateResolversForField(MappingConfig mappingConfig,
                                                    ExtendedFieldDefinition fieldDef,
                                                    String parentTypeName) {
        boolean noResolverForWholeType = mappingConfig.getFieldsWithoutResolvers().contains(parentTypeName);
        if (noResolverForWholeType) {
            return false;
        }
        boolean noResolverForSpecificField = mappingConfig.getFieldsWithoutResolvers().contains(parentTypeName + "." + fieldDef.getName());
        if (noResolverForSpecificField) {
            return false;
        }
        boolean resolverForParamField = mappingConfig.getGenerateParameterizedFieldsResolvers() && !Utils.isEmpty(fieldDef.getInputValueDefinitions());
        if (resolverForParamField) {
            return true;
        }
        boolean resolverForExtendedType = mappingConfig.getGenerateExtensionFieldsResolvers() && fieldDef.isFromExtension();
        if (resolverForExtendedType) {
            return true;
        }
        boolean resolverForWholeType = mappingConfig.getFieldsWithResolvers().contains(parentTypeName);
        if (resolverForWholeType) {
            return true;
        }
        return mappingConfig.getFieldsWithResolvers().contains(parentTypeName + "." + fieldDef.getName());
    }

}
