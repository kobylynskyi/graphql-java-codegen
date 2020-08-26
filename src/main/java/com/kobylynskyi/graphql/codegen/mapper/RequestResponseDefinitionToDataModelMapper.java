package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.ProjectionParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInterfaceTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedUnionTypeDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;

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
     * @param definition     GraphQL definition (type or union)
     * @return Freemarker data model of the GraphQL Response Projection
     */
    public static Map<String, Object> mapResponseProjection(MappingContext mappingContext,
                                                            ExtendedDefinition<?, ?> definition) {
        String className = Utils.capitalize(definition.getName()) + mappingContext.getResponseProjectionSuffix();
        Map<String, Object> dataModel = new HashMap<>();
        // ResponseProjection classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, className);
        dataModel.put(ANNOTATIONS, GraphqlTypeToJavaTypeMapper.getAnnotations(mappingContext, className));
        dataModel.put(JAVA_DOC, Collections.singletonList("Response projection for " + definition.getName()));
        dataModel.put(FIELDS, getProjectionFields(mappingContext, definition));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingContext.getGenerateEqualsAndHashCode());
        dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
        dataModel.put(MAX_DEPTH, mappingContext.getMaxDepth());
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
        String className = MapperUtils.getParametrizedInputClassName(mappingContext, fieldDefinition, parentTypeDefinition);
        Map<String, Object> dataModel = new HashMap<>();
        // ParametrizedInput classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, className);
        dataModel.put(ANNOTATIONS, GraphqlTypeToJavaTypeMapper.getAnnotations(mappingContext, className));
        dataModel.put(JAVA_DOC, Collections.singletonList(String.format("Parametrized input for field %s in type %s",
                fieldDefinition.getName(), parentTypeDefinition.getName())));
        dataModel.put(FIELDS, InputValueDefinitionToParameterMapper.map(
                mappingContext, fieldDefinition.getInputValueDefinitions(), parentTypeDefinition.getName()));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingContext.getGenerateEqualsAndHashCode());
        dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
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
        String className = getClassName(operationDef, fieldNames, objectTypeName, mappingContext.getResponseSuffix());
        String javaType = GraphqlTypeToJavaTypeMapper.getJavaType(
                mappingContext, operationDef.getType(), operationDef.getName(), objectTypeName).getName();
        Map<String, Object> dataModel = new HashMap<>();
        // Response classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingContext));
        dataModel.put(ANNOTATIONS, GraphqlTypeToJavaTypeMapper.getAnnotations(mappingContext, className));
        dataModel.put(CLASS_NAME, className);
        dataModel.put(JAVA_DOC, operationDef.getJavaDoc());
        dataModel.put(DEPRECATED, operationDef.isDeprecated());
        dataModel.put(OPERATION_NAME, operationDef.getName());
        dataModel.put(METHOD_NAME, MapperUtils.capitalizeMethodNameIfRestricted(operationDef.getName()));
        dataModel.put(RETURN_TYPE_NAME, javaType);
        dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
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
        String className = getClassName(operationDef, fieldNames, objectTypeName, mappingContext.getRequestSuffix());

        Map<String, Object> dataModel = new HashMap<>();
        // Request classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingContext));
        dataModel.put(ANNOTATIONS, GraphqlTypeToJavaTypeMapper.getAnnotations(mappingContext, className));
        dataModel.put(CLASS_NAME, className);
        dataModel.put(JAVA_DOC, operationDef.getJavaDoc());
        dataModel.put(OPERATION_NAME, operationDef.getName());
        dataModel.put(OPERATION_TYPE, objectTypeName.toUpperCase());
        dataModel.put(FIELDS, InputValueDefinitionToParameterMapper.map(mappingContext, operationDef.getInputValueDefinitions(), operationDef.getName()));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingContext.getGenerateEqualsAndHashCode());
        dataModel.put(TO_STRING, mappingContext.getGenerateToString());
        dataModel.put(TO_STRING_FOR_REQUEST, mappingContext.getGenerateClient());
        dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
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
     * @param definition     GraphQL definition (type / union / interface)
     * @return Freemarker data model of the GraphQL type
     */
    private static Collection<ProjectionParameterDefinition> getProjectionFields(MappingContext mappingContext,
                                                                                 ExtendedDefinition<?, ?> definition) {

        if (definition instanceof ExtendedObjectTypeDefinition) {
            return getProjectionFields(mappingContext, (ExtendedObjectTypeDefinition) definition);
        } else if (definition instanceof ExtendedUnionTypeDefinition) {
            return getProjectionFields(mappingContext, (ExtendedUnionTypeDefinition) definition);
        } else if (definition instanceof ExtendedInterfaceTypeDefinition) {
            return getProjectionFields(mappingContext, (ExtendedInterfaceTypeDefinition) definition);
        }
        return Collections.emptyList();
    }

    /**
     * Get merged attributes from the type and attributes from the interface.
     *
     * @param mappingContext Global mapping context
     * @param typeDefinition GraphQL type definition
     * @return Freemarker data model for response projection of the GraphQL type
     */
    private static Collection<ProjectionParameterDefinition> getProjectionFields(
            MappingContext mappingContext, ExtendedObjectTypeDefinition typeDefinition) {
        // using the map to exclude duplicate fields from the type and interfaces
        Map<String, ProjectionParameterDefinition> allParameters = new LinkedHashMap<>();
        // includes parameters from the base definition and extensions
        FieldDefinitionToParameterMapper.mapProjectionFields(mappingContext, typeDefinition.getFieldDefinitions(), typeDefinition)
                .forEach(p -> allParameters.put(p.getMethodName(), p));
        // includes parameters from the interface
        List<ExtendedInterfaceTypeDefinition> interfacesOfType = MapperUtils.getInterfacesOfType(typeDefinition, mappingContext.getDocument());
        interfacesOfType.stream()
                .map(i -> FieldDefinitionToParameterMapper.mapProjectionFields(mappingContext, i.getFieldDefinitions(), i))
                .flatMap(Collection::stream)
                .filter(paramDef -> !allParameters.containsKey(paramDef.getMethodName()))
                .forEach(paramDef -> allParameters.put(paramDef.getMethodName(), paramDef));
        ProjectionParameterDefinition typeNameProjParamDef = getTypeNameProjectionParameterDefinition();
        allParameters.put(typeNameProjParamDef.getMethodName(), typeNameProjParamDef);
        return allParameters.values();
    }

    /**
     * Get merged attributes from the type and attributes from the interface.
     *
     * @param mappingContext      Global mapping context
     * @param interfaceDefinition GraphQL interface definition
     * @return Freemarker data model for response projection of the GraphQL interface
     */
    private static Collection<ProjectionParameterDefinition> getProjectionFields(
            MappingContext mappingContext, ExtendedInterfaceTypeDefinition interfaceDefinition) {
        // using the map to exclude duplicate fields from the type and interfaces
        Map<String, ProjectionParameterDefinition> allParameters = new LinkedHashMap<>();
        // includes parameters from the base definition and extensions
        FieldDefinitionToParameterMapper.mapProjectionFields(mappingContext, interfaceDefinition.getFieldDefinitions(), interfaceDefinition)
                .forEach(p -> allParameters.put(p.getMethodName(), p));
        // includes parameters from the interface
        MapperUtils.getInterfacesOfType(interfaceDefinition, mappingContext.getDocument()).stream()
                .map(i -> FieldDefinitionToParameterMapper.mapProjectionFields(mappingContext, i.getFieldDefinitions(), i))
                .flatMap(Collection::stream)
                .filter(paramDef -> !allParameters.containsKey(paramDef.getMethodName()))
                .forEach(paramDef -> allParameters.put(paramDef.getMethodName(), paramDef));

        Set<String> interfaceChildren = mappingContext.getInterfaceChildren()
                .getOrDefault(interfaceDefinition.getName(), Collections.emptySet());
        for (String childName : interfaceChildren) {
            ProjectionParameterDefinition childDef = getChildDefinition(mappingContext, childName);
            allParameters.put(childDef.getMethodName(), childDef);
        }
        ProjectionParameterDefinition typeNameProjParamDef = getTypeNameProjectionParameterDefinition();
        allParameters.put(typeNameProjParamDef.getMethodName(), typeNameProjParamDef);
        return allParameters.values();
    }

    /**
     * Get merged attributes from the type and attributes from the interface.
     *
     * @param mappingContext  Global mapping context
     * @param unionDefinition GraphQL union definition
     * @return Freemarker data model for response projection of the GraphQL union
     */
    private static Collection<ProjectionParameterDefinition> getProjectionFields(
            MappingContext mappingContext, ExtendedUnionTypeDefinition unionDefinition) {
        // using the map to exclude duplicate fields from the type and interfaces
        Map<String, ProjectionParameterDefinition> allParameters = new LinkedHashMap<>();
        for (String memberTypeName : unionDefinition.getMemberTypeNames()) {
            ProjectionParameterDefinition memberDef = getChildDefinition(mappingContext, memberTypeName);
            allParameters.put(memberDef.getMethodName(), memberDef);
        }
        ProjectionParameterDefinition typeNameProjParamDef = getTypeNameProjectionParameterDefinition();
        allParameters.put(typeNameProjParamDef.getMethodName(), typeNameProjParamDef);
        return allParameters.values();
    }

    private static ProjectionParameterDefinition getChildDefinition(MappingContext mappingContext,
                                                                    String childName) {
        ProjectionParameterDefinition parameter = new ProjectionParameterDefinition();
        parameter.setName("...on " + childName);
        parameter.setMethodName("on" + childName);
        parameter.setType(Utils.capitalize(childName + mappingContext.getResponseProjectionSuffix()));
        return parameter;
    }

    private static ProjectionParameterDefinition getTypeNameProjectionParameterDefinition() {
        ProjectionParameterDefinition typeNameProjParamDef = new ProjectionParameterDefinition();
        typeNameProjParamDef.setName("__typename");
        typeNameProjParamDef.setMethodName("typename");
        return typeNameProjParamDef;
    }

}
