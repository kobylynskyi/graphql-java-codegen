package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.NamedDefinition;
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

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.ANNOTATIONS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.BUILDER;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.DEPRECATED;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.ENUM_IMPORT_IT_SELF_IN_SCALA;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.EQUALS_AND_HASH_CODE;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.FIELDS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.GENERATED_INFO;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.GENERATED_ANNOTATION;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.JAVA_DOC;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.METHOD_NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.OPERATION_NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.OPERATION_TYPE;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.PACKAGE;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.RESPONSE_PROJECTION_MAX_DEPTH;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.RETURN_TYPE_NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.TO_STRING;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.TO_STRING_FOR_REQUEST;

/**
 * Map request and response definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class RequestResponseDefinitionToDataModelMapper {

    private final GraphQLTypeMapper graphQLTypeMapper;
    private final DataModelMapper dataModelMapper;
    private final FieldDefinitionToParameterMapper fieldDefinitionToParameterMapper;
    private final InputValueDefinitionToParameterMapper inputValueDefinitionToParameterMapper;

    public RequestResponseDefinitionToDataModelMapper(GraphQLTypeMapper graphQLTypeMapper,
                                                      DataModelMapper dataModelMapper,
                                                      FieldDefinitionToParameterMapper fieldDefinitionToParameterMapper,
                                                      InputValueDefinitionToParameterMapper inputValueDefinitionToParameterMapper) {
        this.graphQLTypeMapper = graphQLTypeMapper;
        this.dataModelMapper = dataModelMapper;
        this.fieldDefinitionToParameterMapper = fieldDefinitionToParameterMapper;
        this.inputValueDefinitionToParameterMapper = inputValueDefinitionToParameterMapper;
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
            classNameBuilder.append(DataModelMapper.getClassNameSuffixWithInputValues(operationDef));
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

    /**
     * Map type definition to a Freemarker data model of Response Projection.
     *
     * @param mappingContext Global mapping context
     * @param definition     GraphQL definition (type or union)
     * @return Freemarker data model of the GraphQL Response Projection
     */
    public Map<String, Object> mapResponseProjection(MappingContext mappingContext,
                                                     ExtendedDefinition<?, ?> definition) {
        String className = Utils.capitalize(definition.getName()) + mappingContext.getResponseProjectionSuffix();
        Map<String, Object> dataModel = new HashMap<>();
        // ResponseProjection classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, className);
        dataModel.put(ANNOTATIONS, graphQLTypeMapper.getAnnotations(mappingContext, className));
        dataModel.put(JAVA_DOC, Collections.singletonList("Response projection for " + definition.getName()));
        dataModel.put(FIELDS, getProjectionFields(mappingContext, definition));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingContext.getGenerateEqualsAndHashCode());
        dataModel.put(GENERATED_ANNOTATION, mappingContext.getAddGeneratedAnnotation());
        dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
        dataModel.put(RESPONSE_PROJECTION_MAX_DEPTH, mappingContext.getResponseProjectionMaxDepth());
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
    public Map<String, Object> mapParametrizedInput(MappingContext mappingContext,
                                                    ExtendedFieldDefinition fieldDefinition,
                                                    ExtendedDefinition<?, ?> parentTypeDefinition) {
        String className = DataModelMapper.getParametrizedInputClassName(mappingContext, fieldDefinition, parentTypeDefinition);
        Map<String, Object> dataModel = new HashMap<>();
        // ParametrizedInput classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, className);
        dataModel.put(ANNOTATIONS, graphQLTypeMapper.getAnnotations(mappingContext, className));
        dataModel.put(JAVA_DOC, Collections.singletonList(String.format("Parametrized input for field %s in type %s",
                fieldDefinition.getName(), parentTypeDefinition.getName())));
        dataModel.put(FIELDS, inputValueDefinitionToParameterMapper.map(
                mappingContext, fieldDefinition.getInputValueDefinitions(), parentTypeDefinition.getName()));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingContext.getGenerateEqualsAndHashCode());
        dataModel.put(GENERATED_ANNOTATION, mappingContext.getAddGeneratedAnnotation());
        dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
        dataModel.put(ENUM_IMPORT_IT_SELF_IN_SCALA, mappingContext.getEnumImportItSelfInScala());
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
    public Map<String, Object> mapResponse(MappingContext mappingContext,
                                           ExtendedFieldDefinition operationDef,
                                           String objectTypeName,
                                           List<String> fieldNames) {
        String className = getClassName(operationDef, fieldNames, objectTypeName, mappingContext.getResponseSuffix());
        NamedDefinition namedDefinition = graphQLTypeMapper.getLanguageType(
                mappingContext, operationDef.getType(), operationDef.getName(), objectTypeName);
        String returnType = graphQLTypeMapper.getResponseReturnType(mappingContext, namedDefinition, namedDefinition.getJavaName());
        Map<String, Object> dataModel = new HashMap<>();
        // Response classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(ANNOTATIONS, graphQLTypeMapper.getAnnotations(mappingContext, className));
        dataModel.put(CLASS_NAME, className);
        dataModel.put(JAVA_DOC, operationDef.getJavaDoc());
        dataModel.put(DEPRECATED, operationDef.getDeprecated(mappingContext));
        dataModel.put(OPERATION_NAME, operationDef.getName());
        dataModel.put(METHOD_NAME, dataModelMapper.capitalizeMethodNameIfRestricted(mappingContext, operationDef.getName()));
        dataModel.put(RETURN_TYPE_NAME, returnType);
        dataModel.put(GENERATED_ANNOTATION, mappingContext.getAddGeneratedAnnotation());
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
    public Map<String, Object> mapRequest(MappingContext mappingContext,
                                          ExtendedFieldDefinition operationDef,
                                          String objectTypeName,
                                          List<String> fieldNames) {
        String className = getClassName(operationDef, fieldNames, objectTypeName, mappingContext.getRequestSuffix());

        Map<String, Object> dataModel = new HashMap<>();
        // Request classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(ANNOTATIONS, graphQLTypeMapper.getAnnotations(mappingContext, className));
        dataModel.put(CLASS_NAME, className);
        dataModel.put(JAVA_DOC, operationDef.getJavaDoc());
        dataModel.put(OPERATION_NAME, operationDef.getName());
        dataModel.put(OPERATION_TYPE, objectTypeName.toUpperCase());
        dataModel.put(FIELDS, inputValueDefinitionToParameterMapper.map(mappingContext, operationDef.getInputValueDefinitions(), operationDef.getName()));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingContext.getGenerateEqualsAndHashCode());
        dataModel.put(TO_STRING, mappingContext.getGenerateToString());
        dataModel.put(TO_STRING_FOR_REQUEST, mappingContext.getGenerateClient());
        dataModel.put(GENERATED_ANNOTATION, mappingContext.getAddGeneratedAnnotation());
        dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
        dataModel.put(ENUM_IMPORT_IT_SELF_IN_SCALA, mappingContext.getEnumImportItSelfInScala());
        return dataModel;
    }

    /**
     * Get merged attributes from the type and attributes from the interface.
     *
     * @param mappingContext Global mapping context
     * @param definition     GraphQL definition (type / union / interface)
     * @return Freemarker data model of the GraphQL type
     */
    private Collection<ProjectionParameterDefinition> getProjectionFields(MappingContext mappingContext,
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
    private Collection<ProjectionParameterDefinition> getProjectionFields(
            MappingContext mappingContext, ExtendedObjectTypeDefinition typeDefinition) {
        // using the map to exclude duplicate fields from the type and interfaces
        Map<String, ProjectionParameterDefinition> allParameters = new LinkedHashMap<>();
        // includes parameters from the base definition and extensions
        fieldDefinitionToParameterMapper.mapProjectionFields(mappingContext, typeDefinition.getFieldDefinitions(), typeDefinition)
                .forEach(p -> allParameters.put(p.getMethodName(), p));
        // includes parameters from the interface
        List<ExtendedInterfaceTypeDefinition> interfacesOfType = DataModelMapper.getInterfacesOfType(typeDefinition, mappingContext.getDocument());
        interfacesOfType.stream()
                .map(i -> fieldDefinitionToParameterMapper.mapProjectionFields(mappingContext, i.getFieldDefinitions(), i))
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
    private Collection<ProjectionParameterDefinition> getProjectionFields(
            MappingContext mappingContext, ExtendedInterfaceTypeDefinition interfaceDefinition) {
        // using the map to exclude duplicate fields from the type and interfaces
        Map<String, ProjectionParameterDefinition> allParameters = new LinkedHashMap<>();
        // includes parameters from the base definition and extensions
        fieldDefinitionToParameterMapper.mapProjectionFields(mappingContext, interfaceDefinition.getFieldDefinitions(), interfaceDefinition)
                .forEach(p -> allParameters.put(p.getMethodName(), p));
        // includes parameters from the interface
        DataModelMapper.getInterfacesOfType(interfaceDefinition, mappingContext.getDocument()).stream()
                .map(i -> fieldDefinitionToParameterMapper.mapProjectionFields(mappingContext, i.getFieldDefinitions(), i))
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

}
