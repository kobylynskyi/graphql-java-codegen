package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.OperationDefinition;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.TypeName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.IMPLEMENTS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.IMPORTS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.JAVA_DOC;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.OPERATIONS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.PACKAGE;
import static com.kobylynskyi.graphql.codegen.model.MappingConfigConstants.PARENT_INTERFACE_TYPE_PLACEHOLDER;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Map field definitions to a Freemarker data model representing a resolver for these fields.
 */
public class FieldDefinitionsToResolverDataModelMapper {

    private FieldDefinitionsToResolverDataModelMapper() {
    }

    /**
     * Map field definition to a Freemarker data model
     *
     * @param mappingContext Global mapping context
     * @param fieldDefs      GraphQL field definitions that require resolvers
     * @param parentTypeName Name of the type for which Resolver will be generated
     * @return Freemarker data model of the GraphQL parametrized field
     */
    public static Map<String, Object> mapToTypeResolver(MappingContext mappingContext,
                                                        List<ExtendedFieldDefinition> fieldDefs,
                                                        String parentTypeName) {
        // Example: PersonResolver
        String className = parentTypeName + "Resolver";
        return mapToResolverModel(mappingContext, parentTypeName, className, fieldDefs,
                singletonList("Resolver for " + parentTypeName),
                getParentInterface(mappingContext, parentTypeName));
    }

    /**
     * Map field definition to a Freemarker data model
     *
     * @param mappingContext  Global mapping context
     * @param fieldDefinition GraphQL field definition
     * @param rootTypeName    Object type (e.g.: "Query", "Mutation" or "Subscription")
     * @param fieldNames      Names of all fields inside the rootType. Used to detect duplicate
     * @return Freemarker data model of the GraphQL field
     */
    public static Map<String, Object> mapRootTypeField(MappingContext mappingContext,
                                                       ExtendedFieldDefinition fieldDefinition,
                                                       String rootTypeName,
                                                       List<String> fieldNames) {
        String className = MapperUtils.getApiClassNameWithPrefixAndSuffix(mappingContext, fieldDefinition, rootTypeName, fieldNames);
        List<ExtendedFieldDefinition> fieldDefs = Collections.singletonList(fieldDefinition);
        return mapToResolverModel(mappingContext, rootTypeName, className, fieldDefs, fieldDefinition.getJavaDoc(),
                getParentInterface(mappingContext, rootTypeName));
    }

    /**
     * Map a root object type definition to a Freemarker data model for a resolver with all its fields.
     *
     * @param mappingContext Global mapping context
     * @param definition     GraphQL object definition of a root type like Query
     * @return Freemarker data model of the GraphQL object
     */
    public static Map<String, Object> mapRootTypeFields(MappingContext mappingContext,
                                                        ExtendedObjectTypeDefinition definition) {
        String className = MapperUtils.getApiClassNameWithPrefixAndSuffix(mappingContext, definition);
        // For root types like "Query", we create resolvers for all fields
        return mapToResolverModel(mappingContext, definition.getName(), className,
                definition.getFieldDefinitions(), definition.getJavaDoc(),
                getParentInterface(mappingContext, definition.getName()));
    }

    private static Map<String, Object> mapToResolverModel(MappingContext mappingContext, String parentTypeName,
                                                          String className,
                                                          List<ExtendedFieldDefinition> fieldDefinitions,
                                                          List<String> javaDoc,
                                                          String parentInterface) {
        String packageName = MapperUtils.getApiPackageName(mappingContext);
        Set<String> imports = MapperUtils.getImports(mappingContext, packageName);
        List<OperationDefinition> operations = mapToOperations(mappingContext, fieldDefinitions, parentTypeName);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, imports);
        dataModel.put(CLASS_NAME, className);
        dataModel.put(OPERATIONS, operations);
        dataModel.put(JAVA_DOC, javaDoc);
        dataModel.put(IMPLEMENTS, parentInterface != null ? singletonList(parentInterface) : null);
        return dataModel;
    }

    /**
     * Builds a list of Freemarker-understandable structures representing operations to resolve the given fields
     * for a given parent type.
     *
     * @param mappingContext   Global mapping context
     * @param fieldDefinitions The GraphQL definition of the fields that the methods should resolve
     * @param parentTypeName   Name of the parent type which the field belongs to
     * @return Freemarker-understandable format of operations
     */
    private static List<OperationDefinition> mapToOperations(MappingContext mappingContext,
                                                             List<ExtendedFieldDefinition> fieldDefinitions,
                                                             String parentTypeName) {
        return fieldDefinitions.stream()
                .map(fieldDef -> map(mappingContext, fieldDef, parentTypeName))
                .collect(Collectors.toList());
    }

    /**
     * Builds a Freemarker-understandable structure representing an operation to resolve a field for a given parent type.
     *
     * @param mappingContext Global mapping context
     * @param resolvedField  The GraphQL definition of the field that the method should resolve
     * @param parentTypeName Name of the parent type which the field belongs to
     * @return Freemarker-understandable format of operation
     */
    private static OperationDefinition map(MappingContext mappingContext, ExtendedFieldDefinition resolvedField,
                                           String parentTypeName) {
        String javaType = GraphqlTypeToJavaTypeMapper.getJavaType(
                mappingContext, resolvedField.getType(), resolvedField.getName(), parentTypeName).getName();
        OperationDefinition operation = new OperationDefinition();
        operation.setName(resolvedField.getName());
        operation.setType(GraphqlTypeToJavaTypeMapper.wrapIntoAsyncIfRequired(mappingContext, javaType, parentTypeName));
        operation.setAnnotations(GraphqlTypeToJavaTypeMapper.getAnnotations(mappingContext,
                resolvedField.getType(), resolvedField.getName(), parentTypeName, false));
        operation.setParameters(getOperationParameters(mappingContext, resolvedField, parentTypeName));
        operation.setJavaDoc(resolvedField.getJavaDoc());
        operation.setDeprecated(resolvedField.isDeprecated());
        return operation;
    }

    private static List<ParameterDefinition> getOperationParameters(MappingContext mappingContext,
                                                                    ExtendedFieldDefinition resolvedField,
                                                                    String parentTypeName) {
        List<ParameterDefinition> parameters = new ArrayList<>();

        // 1. First parameter is the parent object for which we are resolving fields (unless it's the root Query)
        if (!Utils.isGraphqlOperation(parentTypeName)) {
            String parentObjectParamType = GraphqlTypeToJavaTypeMapper.getJavaType(mappingContext, new TypeName(parentTypeName));
            String parentObjectParamName = MapperUtils.capitalizeIfRestricted(Utils.uncapitalize(parentObjectParamType));
            parameters.add(new ParameterDefinition(parentObjectParamType, parentObjectParamName, null, emptyList(), emptyList(), resolvedField.isDeprecated()));
        }

        // 2. Next parameters are input values
        parameters.addAll(InputValueDefinitionToParameterMapper.map(mappingContext, resolvedField.getInputValueDefinitions(), resolvedField.getName()));

        // 3. Last parameter (optional) is the DataFetchingEnvironment
        if (Boolean.TRUE.equals(mappingContext.getGenerateDataFetchingEnvironmentArgumentInApis())) {
            parameters.add(ParameterDefinition.DATA_FETCHING_ENVIRONMENT);
        }
        return parameters;
    }

    public static String getParentInterface(MappingContext mappingContext, String typeName) {
        // 1. check if provided type name is GraphQL root type
        try {
            switch (GraphQLOperation.valueOf(typeName.toUpperCase())) {
                case QUERY:
                    return mappingContext.getQueryResolverParentInterface();
                case MUTATION:
                    return mappingContext.getMutationResolverParentInterface();
                case SUBSCRIPTION:
                    return mappingContext.getSubscriptionResolverParentInterface();
            }
        } catch (Exception ignored) {
        }

        // 2. if provided type name is GraphQL root type then assume that it is GraphQL type
        if (mappingContext.getResolverParentInterface() == null) {
            return null;
        }
        return mappingContext.getResolverParentInterface()
                .replace(PARENT_INTERFACE_TYPE_PLACEHOLDER,
                        MapperUtils.getModelClassNameWithPrefixAndSuffix(mappingContext, typeName));
    }

}
