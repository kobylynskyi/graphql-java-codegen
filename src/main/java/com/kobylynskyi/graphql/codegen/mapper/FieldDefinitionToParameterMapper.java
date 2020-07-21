package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.ProjectionParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.List;

import static com.kobylynskyi.graphql.codegen.mapper.GraphqlTypeToJavaTypeMapper.getAnnotations;
import static com.kobylynskyi.graphql.codegen.mapper.GraphqlTypeToJavaTypeMapper.getJavaType;
import static com.kobylynskyi.graphql.codegen.mapper.GraphqlTypeToJavaTypeMapper.getNestedTypeName;
import static java.util.stream.Collectors.toList;

/**
 * Mapper from GraphQL's FieldDefinition to a Freemarker-understandable format
 *
 * @author kobylynskyi
 */
public class FieldDefinitionToParameterMapper {

    private FieldDefinitionToParameterMapper() {
    }

    /**
     * Map field definition to a Freemarker-understandable data model type
     *
     * @param mappingContext   Global mapping context
     * @param fieldDefinitions List of GraphQL field definitions
     * @param parentTypeName   Name of the parent GraphQL type
     * @return Freemarker data model of the GraphQL field definition
     */
    public static List<ParameterDefinition> mapFields(MappingContext mappingContext,
                                                      List<ExtendedFieldDefinition> fieldDefinitions,
                                                      String parentTypeName) {
        return fieldDefinitions.stream()
                .filter(fieldDef -> !generateResolversForField(mappingContext, fieldDef, parentTypeName))
                .map(fieldDef -> mapField(mappingContext, fieldDef, parentTypeName))
                .collect(toList());
    }

    /**
     * Map field definition to a Freemarker-understandable data model type
     *
     * @param mappingContext       Global mapping context
     * @param fieldDefinitions     List of GraphQL field definitions
     * @param parentTypeDefinition Parent GraphQL type definition
     * @return Freemarker data model of the GraphQL field definition
     */
    public static List<ProjectionParameterDefinition> mapProjectionFields(MappingContext mappingContext,
                                                                          List<ExtendedFieldDefinition> fieldDefinitions,
                                                                          ExtendedDefinition<?, ?> parentTypeDefinition) {
        return fieldDefinitions.stream()
                .map(fieldDef -> mapProjectionField(mappingContext, fieldDef, parentTypeDefinition))
                .collect(toList());
    }

    /**
     * Map GraphQL's FieldDefinition to a Freemarker-understandable format of parameter
     *
     * @param mappingContext Global mapping context
     * @param fieldDef       GraphQL field definition
     * @param parentTypeName Name of the parent type
     * @return Freemarker-understandable format of parameter (field)
     */
    private static ParameterDefinition mapField(MappingContext mappingContext, ExtendedFieldDefinition fieldDef,
                                                String parentTypeName) {
        ParameterDefinition parameter = new ParameterDefinition();
        parameter.setName(MapperUtils.capitalizeIfRestricted(fieldDef.getName()));
        parameter.setOriginalName(fieldDef.getName());
        parameter.setType(getJavaType(mappingContext, fieldDef.getType(), fieldDef.getName(), parentTypeName).getName());
        parameter.setAnnotations(getAnnotations(mappingContext, fieldDef.getType(), fieldDef.getName(), parentTypeName, false));
        parameter.setJavaDoc(fieldDef.getJavaDoc());
        parameter.setDeprecated(fieldDef.isDeprecated());
        return parameter;
    }

    /**
     * Map GraphQL's FieldDefinition to a Freemarker-understandable format of parameter
     *
     * @param mappingContext Global mapping context
     * @param fieldDef       GraphQL field definition
     * @param parentTypeDef  GraphQL definition which is a parent to provided field definition
     * @return Freemarker-understandable format of parameter (field)
     */
    private static ProjectionParameterDefinition mapProjectionField(MappingContext mappingContext,
                                                                    ExtendedFieldDefinition fieldDef,
                                                                    ExtendedDefinition<?, ?> parentTypeDef) {
        ProjectionParameterDefinition parameter = new ProjectionParameterDefinition();
        parameter.setName(fieldDef.getName());
        parameter.setMethodName(MapperUtils.capitalizeMethodNameIfRestricted(parameter.getName()));
        String nestedType = getNestedTypeName(fieldDef.getType());
        if (mappingContext.getTypesUnionsInterfacesNames().contains(nestedType)) {
            parameter.setType(Utils.capitalize(nestedType + mappingContext.getResponseProjectionSuffix()));
        }
        if (!Utils.isEmpty(fieldDef.getInputValueDefinitions())) {
            parameter.setParametrizedInputClassName(
                    MapperUtils.getParametrizedInputClassName(mappingContext, fieldDef, parentTypeDef));
        }
        parameter.setDeprecated(fieldDef.isDeprecated());
        return parameter;
    }

    /**
     * Check whether FieldResolver should be generated for a given field.
     *
     * @param mappingContext Global mapping context
     * @param fieldDef       GraphQL field definition
     * @param parentTypeName Name of the parent type
     * @return <code>true</code> if FieldResolver will be generated for the field. <code>false</code> otherwise
     */
    public static boolean generateResolversForField(MappingContext mappingContext,
                                                    ExtendedFieldDefinition fieldDef,
                                                    String parentTypeName) {
        boolean noResolverForWholeType = mappingContext.getFieldsWithoutResolvers().contains(parentTypeName);
        if (noResolverForWholeType) {
            return false;
        }
        boolean noResolverForSpecificField = mappingContext.getFieldsWithoutResolvers().contains(parentTypeName + "." + fieldDef.getName());
        if (noResolverForSpecificField) {
            return false;
        }
        boolean resolverForParamField = mappingContext.getGenerateParameterizedFieldsResolvers() && !Utils.isEmpty(fieldDef.getInputValueDefinitions());
        if (resolverForParamField) {
            return true;
        }
        boolean resolverForExtendedType = mappingContext.getGenerateExtensionFieldsResolvers() && fieldDef.isFromExtension();
        if (resolverForExtendedType) {
            return true;
        }
        boolean resolverForWholeType = mappingContext.getFieldsWithResolvers().contains(parentTypeName);
        if (resolverForWholeType) {
            return true;
        }
        return mappingContext.getFieldsWithResolvers().contains(parentTypeName + "." + fieldDef.getName());
    }

}
