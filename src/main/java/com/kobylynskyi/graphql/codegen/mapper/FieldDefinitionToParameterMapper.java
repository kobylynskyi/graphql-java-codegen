package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.NamedDefinition;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.ProjectionParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Mapper from GraphQL's FieldDefinition to a Freemarker-understandable format
 *
 * @author kobylynskyi
 */
public class FieldDefinitionToParameterMapper {

    private final GraphQLTypeMapper graphQLTypeMapper;
    private final DataModelMapper dataModelMapper;

    public FieldDefinitionToParameterMapper(GraphQLTypeMapper graphQLTypeMapper,
                                            DataModelMapper dataModelMapper) {
        this.graphQLTypeMapper = graphQLTypeMapper;
        this.dataModelMapper = dataModelMapper;
    }

    /**
     * Check whether FieldResolver should be generated for a given field.
     *
     * @param mappingContext   Global mapping context
     * @param fieldDef         GraphQL field definition
     * @param parentDefinition Parent GraphQL definition
     * @return <code>true</code> if FieldResolver will be generated for the field. <code>false</code> otherwise
     */
    public static boolean generateResolversForField(MappingContext mappingContext,
                                                    ExtendedFieldDefinition fieldDef,
                                                    ExtendedDefinition<?, ?> parentDefinition) {
        String parentTypeName = parentDefinition.getName();
        boolean noResolverForWholeType = mappingContext.getFieldsWithoutResolvers().contains(parentTypeName);
        boolean noResolverForSpecificField = mappingContext.getFieldsWithoutResolvers().contains(parentTypeName + "." + fieldDef.getName());
        if (noResolverForWholeType || noResolverForSpecificField) {
            return false;
        }
        for (String fieldWithoutResolver : mappingContext.getFieldsWithoutResolvers()) {
            boolean noResolverForWholeTypeViaDirective = parentDefinition.getDirectiveNames().stream().anyMatch(fd -> fieldWithoutResolver.equals("@" + fd));
            boolean noResolverForSpecificFieldViaDirective = fieldDef.getDirectives().stream().anyMatch(fd -> fieldWithoutResolver.equals("@" + fd.getName()));
            if (noResolverForWholeTypeViaDirective || noResolverForSpecificFieldViaDirective) {
                return false;
            }
        }
        boolean resolverForParamField = mappingContext.getGenerateParameterizedFieldsResolvers() && !Utils.isEmpty(fieldDef.getInputValueDefinitions());
        boolean resolverForExtendedType = mappingContext.getGenerateExtensionFieldsResolvers() && fieldDef.isFromExtension();
        boolean resolverForWholeType = mappingContext.getFieldsWithResolvers().contains(parentTypeName);
        boolean resolverForTypeField = mappingContext.getFieldsWithResolvers().contains(parentTypeName + "." + fieldDef.getName());
        if (resolverForParamField || resolverForExtendedType || resolverForWholeType || resolverForTypeField) {
            return true;
        }
        for (String fieldWithResolver : mappingContext.getFieldsWithResolvers()) {
            boolean resolverForWholeTypeViaDirective = parentDefinition.getDirectiveNames().stream().anyMatch(fd -> fieldWithResolver.equals("@" + fd));
            boolean resolverForSpecificFieldViaDirective = fieldDef.getDirectives().stream().anyMatch(fd -> fieldWithResolver.equals("@" + fd.getName()));
            if (resolverForWholeTypeViaDirective || resolverForSpecificFieldViaDirective) {
                return true;
            }
        }
        return false;
    }

    /**
     * Map field definition to a Freemarker-understandable data model type
     *
     * @param mappingContext   Global mapping context
     * @param fieldDefinitions List of GraphQL field definitions
     * @param parentDefinition Parent GraphQL definition
     * @return Freemarker data model of the GraphQL field definition
     */
    public List<ParameterDefinition> mapFields(MappingContext mappingContext,
                                               List<ExtendedFieldDefinition> fieldDefinitions,
                                               ExtendedDefinition<?, ?> parentDefinition) {
        return fieldDefinitions.stream()
                .filter(fieldDef -> !generateResolversForField(mappingContext, fieldDef, parentDefinition))
                .map(fieldDef -> mapField(mappingContext, fieldDef, parentDefinition.getName()))
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
    public List<ProjectionParameterDefinition> mapProjectionFields(MappingContext mappingContext,
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
    private ParameterDefinition mapField(MappingContext mappingContext, ExtendedFieldDefinition fieldDef,
                                         String parentTypeName) {
        NamedDefinition namedDefinition = graphQLTypeMapper.getLanguageType(mappingContext, fieldDef.getType(), fieldDef.getName(), parentTypeName);

        ParameterDefinition parameter = new ParameterDefinition();
        parameter.setName(dataModelMapper.capitalizeIfRestricted(mappingContext, fieldDef.getName()));
        parameter.setOriginalName(fieldDef.getName());
        parameter.setType(graphQLTypeMapper.getTypeConsideringPrimitive(mappingContext, namedDefinition, namedDefinition.getJavaName()));
        parameter.setAnnotations(graphQLTypeMapper.getAnnotations(mappingContext, fieldDef.getType(), fieldDef, parentTypeName, false));
        parameter.setJavaDoc(fieldDef.getJavaDoc());
        parameter.setDeprecated(fieldDef.getDeprecated(mappingContext));
        parameter.setMandatory(namedDefinition.isMandatory());
        parameter.setSerializeUsingObjectMapper(namedDefinition.isSerializeUsingObjectMapper());
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
    private ProjectionParameterDefinition mapProjectionField(MappingContext mappingContext,
                                                             ExtendedFieldDefinition fieldDef,
                                                             ExtendedDefinition<?, ?> parentTypeDef) {
        ProjectionParameterDefinition parameter = new ProjectionParameterDefinition();
        parameter.setName(fieldDef.getName());
        parameter.setMethodName(dataModelMapper.capitalizeMethodNameIfRestricted(mappingContext, parameter.getName()));
        String nestedType = GraphQLTypeMapper.getNestedTypeName(fieldDef.getType());
        if (mappingContext.getTypesUnionsInterfacesNames().contains(nestedType)) {
            parameter.setType(Utils.capitalize(nestedType + mappingContext.getResponseProjectionSuffix()));
        }
        if (!Utils.isEmpty(fieldDef.getInputValueDefinitions())) {
            parameter.setParametrizedInputClassName(
                    DataModelMapper.getParametrizedInputClassName(mappingContext, fieldDef, parentTypeDef));
        }
        parameter.setDeprecated(fieldDef.getDeprecated(mappingContext));
        return parameter;
    }

}
