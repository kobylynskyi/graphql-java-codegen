package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.OperationDefinition;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.TypeName;

import java.util.*;
import java.util.stream.Collectors;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Map field definitions to a Freemarker data model representing a resolver for these fields.
 */
public class FieldDefinitionsToResolverDataModelMapper {

    /**
     * Map field definition to a Freemarker data model
     *
     * @param mappingConfig  Global mapping configuration
     * @param fieldDefs      GraphQL field definitions that require resolvers
     * @param parentTypeName Name of the type for which Resolver will be generated
     * @return Freemarker data model of the GraphQL parametrized field
     */
    public static Map<String, Object> mapToTypeResolver(MappingConfig mappingConfig,
                                                        List<ExtendedFieldDefinition> fieldDefs,
                                                        String parentTypeName) {
        // Example: PersonResolver
        String className = parentTypeName + "Resolver";
        return mapToResolverModel(mappingConfig, parentTypeName, className, fieldDefs,
                singletonList("Resolver for " + parentTypeName));
    }

    /**
     * Map field definition to a Freemarker data model
     *
     * @param mappingConfig   Global mapping configuration
     * @param fieldDefinition GraphQL field definition
     * @param rootTypeName    Object type (e.g.: "Query", "Mutation" or "Subscription")
     * @return Freemarker data model of the GraphQL field
     */
    public static Map<String, Object> mapRootTypeField(MappingConfig mappingConfig,
                                                       ExtendedFieldDefinition fieldDefinition,
                                                       String rootTypeName) {
        // Examples: VersionQuery, CreateEventMutation (rootTypeName is "Query" or the likes)
        String className = Utils.capitalize(fieldDefinition.getName()) + rootTypeName;
        List<ExtendedFieldDefinition> fieldDefs = Collections.singletonList(fieldDefinition);
        return mapToResolverModel(mappingConfig, rootTypeName, className, fieldDefs, fieldDefinition.getJavaDoc());
    }

    /**
     * Map a root object type definition to a Freemarker data model for a resolver with all its fields.
     *
     * @param mappingConfig Global mapping configuration
     * @param definition    GraphQL object definition of a root type like Query
     * @return Freemarker data model of the GraphQL object
     */
    public static Map<String, Object> mapRootTypeFields(MappingConfig mappingConfig,
                                                        ExtendedObjectTypeDefinition definition) {
        String parentTypeName = definition.getName();
        String className = Utils.capitalize(parentTypeName);
        // For root types like "Query", we create resolvers for all fields
        return mapToResolverModel(mappingConfig, parentTypeName, className,
                definition.getFieldDefinitions(), definition.getJavaDoc());
    }

    private static Map<String, Object> mapToResolverModel(MappingConfig mappingConfig, String parentTypeName,
                                                          String className,
                                                          List<ExtendedFieldDefinition> fieldDefinitions,
                                                          List<String> javaDoc) {
        String packageName = MapperUtils.getApiPackageName(mappingConfig);
        Set<String> imports = MapperUtils.getImports(mappingConfig, packageName);
        List<OperationDefinition> operations = mapToOperations(mappingConfig, fieldDefinitions, parentTypeName);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, imports);
        dataModel.put(CLASS_NAME, className);
        dataModel.put(OPERATIONS, operations);
        return dataModel;
    }

    /**
     * Builds a list of Freemarker-understandable structures representing operations to resolve the given fields
     * for a given parent type.
     *
     * @param mappingConfig    Global mapping configuration
     * @param fieldDefinitions The GraphQL definition of the fields that the methods should resolve
     * @param parentTypeName   Name of the parent type which the field belongs to
     * @return Freemarker-understandable format of operations
     */
    private static List<OperationDefinition> mapToOperations(MappingConfig mappingConfig,
                                                             List<ExtendedFieldDefinition> fieldDefinitions,
                                                             String parentTypeName) {
        return fieldDefinitions.stream()
                .map(fieldDef -> map(mappingConfig, fieldDef, parentTypeName))
                .collect(Collectors.toList());
    }

    /**
     * Builds a Freemarker-understandable structure representing an operation to resolve a field for a given parent type.
     *
     * @param mappingConfig  Global mapping configuration
     * @param resolvedField  The GraphQL definition of the field that the method should resolve
     * @param parentTypeName Name of the parent type which the field belongs to
     * @return Freemarker-understandable format of operation
     */
    private static OperationDefinition map(MappingConfig mappingConfig, ExtendedFieldDefinition resolvedField,
                                           String parentTypeName) {
        String javaType = GraphqlTypeToJavaTypeMapper.getJavaType(
                mappingConfig, resolvedField.getType(), resolvedField.getName(), parentTypeName);
        OperationDefinition operation = new OperationDefinition();
        operation.setName(resolvedField.getName());
        operation.setType(GraphqlTypeToJavaTypeMapper.wrapIntoAsyncIfRequired(mappingConfig, javaType, parentTypeName));
        operation.setAnnotations(GraphqlTypeToJavaTypeMapper.getAnnotations(mappingConfig,
                resolvedField.getType(), resolvedField.getName(), parentTypeName, false));
        operation.setParameters(getOperationParameters(mappingConfig, resolvedField, parentTypeName));
        operation.setJavaDoc(resolvedField.getJavaDoc());
        operation.setDeprecated(resolvedField.isDeprecated());
        return operation;
    }

    private static List<ParameterDefinition> getOperationParameters(MappingConfig mappingConfig,
                                                                    ExtendedFieldDefinition resolvedField,
                                                                    String parentTypeName) {
        List<ParameterDefinition> parameters = new ArrayList<>();

        // 1. First parameter is the parent object for which we are resolving fields (unless it's the root Query)
        if (!Utils.isGraphqlOperation(parentTypeName)) {
            String parentObjectParamType = GraphqlTypeToJavaTypeMapper.getJavaType(mappingConfig, new TypeName(parentTypeName));
            String parentObjectParamName = MapperUtils.capitalizeIfRestricted(Utils.uncapitalize(parentObjectParamType));
            parameters.add(new ParameterDefinition(parentObjectParamType, parentObjectParamName, null, emptyList(), emptyList(), resolvedField.isDeprecated()));
        }

        // 2. Next parameters are input values
        parameters.addAll(InputValueDefinitionToParameterMapper.map(mappingConfig, resolvedField.getInputValueDefinitions(), resolvedField.getName()));

        // 3. Last parameter (optional) is the DataFetchingEnvironment
        if (mappingConfig.getGenerateDataFetchingEnvironmentArgumentInApis()) {
            parameters.add(ParameterDefinition.DATA_FETCHING_ENVIRONMENT);
        }
        return parameters;
    }
}
