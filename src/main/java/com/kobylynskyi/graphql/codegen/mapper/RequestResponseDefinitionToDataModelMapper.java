package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.ProjectionParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.BUILDER;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.DEPRECATED;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.EQUALS_AND_HASH_CODE;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.FIELDS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.JAVA_DOC;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.OPERATION_NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.OPERATION_TYPE;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.PACKAGE;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.RETURN_TYPE_NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.TO_STRING;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.TO_STRING_FOR_REQUEST;

/**
 * Map request and response definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class RequestResponseDefinitionToDataModelMapper {

    private RequestResponseDefinitionToDataModelMapper() {
    }

    /**
     * Map type definition to a Freemarker data model of Response Projection.
     *
     * @param mappingContext Global mapping context
     * @param typeDefinition GraphQL type definition
     * @return Freemarker data model of the GraphQL Response Projection
     */
    public static Map<String, Object> mapResponseProjection(MappingContext mappingContext,
                                                            ExtendedObjectTypeDefinition typeDefinition) {
        Map<String, Object> dataModel = new HashMap<>();
        // ResponseProjection classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, Utils.capitalize(typeDefinition.getName()) + mappingContext.getResponseProjectionSuffix());
        dataModel.put(JAVA_DOC, Collections.singletonList("Response projection for " + typeDefinition.getName()));
        dataModel.put(FIELDS, getProjectionFields(mappingContext, typeDefinition));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingContext.getGenerateEqualsAndHashCode());
        // dataModel.put(TO_STRING, mappingConfig.getGenerateToString()); always generated for serialization purposes
        return dataModel;
    }

    /**
     * Map field definition to a Freemarker data model of Parametrized Input.
     *
     * @param mappingContext       Global mapping context
     * @param fieldDefinition      GraphQL field definition
     * @param parentTypeDefinition GraphQL parent type definition
     * @return Freemarker data model of the GraphQL Parametrized Input
     */
    public static Map<String, Object> mapParametrizedInput(MappingContext mappingContext,
                                                           ExtendedFieldDefinition fieldDefinition,
                                                           ExtendedObjectTypeDefinition parentTypeDefinition) {
        Map<String, Object> dataModel = new HashMap<>();
        // ParametrizedInput classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, MapperUtils.getParametrizedInputClassName(mappingContext, fieldDefinition, parentTypeDefinition));
        dataModel.put(JAVA_DOC, Collections.singletonList(String.format("Parametrized input for field %s in type %s",
                fieldDefinition.getName(), parentTypeDefinition.getName())));
        dataModel.put(FIELDS, InputValueDefinitionToParameterMapper.map(
                mappingContext, fieldDefinition.getInputValueDefinitions(), parentTypeDefinition.getName()));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingContext.getGenerateEqualsAndHashCode());
        // dataModel.put(TO_STRING, mappingConfig.getGenerateToString()); always generated for serialization purposes
        return dataModel;
    }

    /**
     * Map field definition to a Response Freemarker data model.
     *
     * @param mappingContext Global mapping context
     * @param operationDef   GraphQL operation definition
     * @param objectTypeName Object type (e.g.: "Query", "Mutation" or "Subscription")
     * @param fieldNames     Names of all fields inside the rootType. Used to detect duplicate
     * @return Freemarker data model of the GraphQL response
     */
    public static Map<String, Object> mapResponse(MappingContext mappingContext,
                                                  ExtendedFieldDefinition operationDef,
                                                  String objectTypeName,
                                                  List<String> fieldNames) {
        String javaType = GraphqlTypeToJavaTypeMapper.getJavaType(
                mappingContext, operationDef.getType(), operationDef.getName(), objectTypeName).getName();
        Map<String, Object> dataModel = new HashMap<>();
        // Response classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, getClassName(operationDef, fieldNames, objectTypeName, mappingContext.getResponseSuffix()));
        dataModel.put(JAVA_DOC, operationDef.getJavaDoc());
        dataModel.put(DEPRECATED, operationDef.isDeprecated());
        dataModel.put(OPERATION_NAME, operationDef.getName());
        dataModel.put(RETURN_TYPE_NAME, javaType);
        return dataModel;
    }

    /**
     * Map field definition to a Request Freemarker data model.
     *
     * @param mappingContext Global mapping context
     * @param operationDef   GraphQL operation definition
     * @param objectTypeName Object type (e.g.: "Query", "Mutation" or "Subscription")
     * @param fieldNames     Names of all fields inside the rootType. Used to detect duplicate
     * @return Freemarker data model of the GraphQL request
     */
    public static Map<String, Object> mapRequest(MappingContext mappingContext,
                                                 ExtendedFieldDefinition operationDef,
                                                 String objectTypeName,
                                                 List<String> fieldNames) {
        Map<String, Object> dataModel = new HashMap<>();
        // Request classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, getClassName(operationDef, fieldNames, objectTypeName, mappingContext.getRequestSuffix()));
        dataModel.put(JAVA_DOC, operationDef.getJavaDoc());
        dataModel.put(OPERATION_NAME, operationDef.getName());
        dataModel.put(OPERATION_TYPE, objectTypeName.toUpperCase());
        dataModel.put(FIELDS, InputValueDefinitionToParameterMapper.map(mappingContext, operationDef.getInputValueDefinitions(), operationDef.getName()));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingContext.getGenerateEqualsAndHashCode());
        dataModel.put(TO_STRING, mappingContext.getGenerateToString());
        dataModel.put(TO_STRING_FOR_REQUEST, mappingContext.getGenerateClient());
        return dataModel;
    }

    /**
     * Examples:
     * - EventsByIdsQueryRequest
     * - EventsByCategoryQueryRequest
     * - CreateEventMutationResponse
     */
    private static String getClassName(ExtendedFieldDefinition operationDef,
                                       List<String> fieldNames,
                                       String objectType,
                                       String suffix) {
        StringBuilder classNameBuilder = new StringBuilder();
        classNameBuilder.append(Utils.capitalize(operationDef.getName()));
        if (Collections.frequency(fieldNames, operationDef.getName()) > 1) {
            classNameBuilder.append(MapperUtils.getClassNameSuffixWithInputValues(operationDef));
        }
        classNameBuilder.append(objectType);
        if (Utils.isNotBlank(suffix)) {
            classNameBuilder.append(suffix);
        }
        return classNameBuilder.toString();
    }

    /**
     * Get merged attributes from the type and attributes from the interface.
     *
     * @param mappingContext Global mapping context
     * @param typeDefinition GraphQL type definition
     * @return Freemarker data model of the GraphQL type
     */
    private static Collection<ProjectionParameterDefinition> getProjectionFields(MappingContext mappingContext,
                                                                                 ExtendedObjectTypeDefinition typeDefinition) {
        // using the map to exclude duplicate fields from the type and interfaces
        Map<String, ProjectionParameterDefinition> allParameters = new LinkedHashMap<>();

        // includes parameters from the base definition and extensions
        FieldDefinitionToParameterMapper.mapProjectionFields(mappingContext, typeDefinition.getFieldDefinitions(), typeDefinition)
                .forEach(p -> allParameters.put(p.getName(), p));
        // includes parameters from the interface
        MapperUtils.getInterfacesOfType(typeDefinition, mappingContext.getDocument()).stream()
                .map(i -> FieldDefinitionToParameterMapper.mapProjectionFields(mappingContext, i.getFieldDefinitions(), i))
                .flatMap(Collection::stream)
                .filter(paramDef -> !allParameters.containsKey(paramDef.getName()))
                .forEach(paramDef -> allParameters.put(paramDef.getName(), paramDef));
        return allParameters.values();
    }

}
