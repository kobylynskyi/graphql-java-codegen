package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.NamedDefinition;
import com.kobylynskyi.graphql.codegen.model.OperationDefinition;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.RelayConfig;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Argument;
import graphql.language.Directive;
import graphql.language.StringValue;
import graphql.language.TypeName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.ENUM_IMPORT_IT_SELF_IN_SCALA;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.GENERATED_INFO;
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
        String className = MapperUtils.getTypeResolverClassNameWithPrefixAndSuffix(mappingContext, parentTypeName);
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
        dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
        dataModel.put(ENUM_IMPORT_IT_SELF_IN_SCALA, mappingContext.getEnumImportItSelfInScala());
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
     * @param fieldDef       The GraphQL definition of the field that the method should resolve
     * @param parentTypeName Name of the parent type which the field belongs to
     * @return Freemarker-understandable format of operation
     */
    private static OperationDefinition map(MappingContext mappingContext, ExtendedFieldDefinition fieldDef,
                                           String parentTypeName) {
        String name = MapperUtils.capitalizeIfRestricted(mappingContext, fieldDef.getName());
        NamedDefinition javaType = GraphqlTypeToJavaTypeMapper.getJavaType(mappingContext, fieldDef.getType(), fieldDef.getName(), parentTypeName);
        String returnType = getReturnType(mappingContext, fieldDef, javaType, parentTypeName);
        List<String> annotations = GraphqlTypeToJavaTypeMapper.getAnnotations(mappingContext, fieldDef.getType(), fieldDef, parentTypeName, false);
        List<ParameterDefinition> parameters = getOperationParameters(mappingContext, fieldDef, parentTypeName);

        OperationDefinition operation = new OperationDefinition();
        operation.setName(name);
        operation.setOriginalName(fieldDef.getName());
        operation.setType(returnType);
        operation.setAnnotations(annotations);
        operation.setParameters(parameters);
        operation.setJavaDoc(fieldDef.getJavaDoc());
        operation.setDeprecated(fieldDef.isDeprecated());
        operation.setThrowsException(mappingContext.getGenerateApisWithThrowsException());
        return operation;
    }

    private static List<ParameterDefinition> getOperationParameters(MappingContext mappingContext,
                                                                    ExtendedFieldDefinition resolvedField,
                                                                    String parentTypeName) {
        List<ParameterDefinition> parameters = new ArrayList<>();

        // 1. First parameter is the parent object for which we are resolving fields (unless it's the root Query)
        if (!Utils.isGraphqlOperation(parentTypeName)) {
            String parentObjectParamType = GraphqlTypeToJavaTypeMapper.getJavaType(mappingContext, new TypeName(parentTypeName));
            String parentObjectParamName = MapperUtils.capitalizeIfRestricted(mappingContext, Utils.uncapitalize(parentObjectParamType));
            parameters.add(new ParameterDefinition(parentObjectParamType, parentObjectParamName, parentObjectParamName, null, emptyList(), emptyList(), resolvedField.isDeprecated(), false));
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

    private static String getReturnType(MappingContext mappingContext, ExtendedFieldDefinition fieldDef,
                                        NamedDefinition namedDefinition, String parentTypeName) {
        RelayConfig relayConfig = mappingContext.getRelayConfig();
        if (relayConfig != null && relayConfig.getDirectiveName() != null) {
            Directive connectionDirective = fieldDef.getDirective(relayConfig.getDirectiveName());
            if (connectionDirective != null) {
                Argument argument = connectionDirective.getArgument(relayConfig.getDirectiveArgumentName());
                // as of now supporting only string value of directive argument
                if (argument != null && argument.getValue() instanceof StringValue) {
                    String graphqlTypeName = ((StringValue) argument.getValue()).getValue();
                    String javaTypeName = GraphqlTypeToJavaTypeMapper.getJavaType(mappingContext,
                            new TypeName(graphqlTypeName), graphqlTypeName, parentTypeName, false, false).getJavaName();
                    return GraphqlTypeToJavaTypeMapper.getGenericsString(mappingContext, relayConfig.getConnectionType(), javaTypeName);
                }
            }
        }
        return GraphqlTypeToJavaTypeMapper.wrapApiReturnTypeIfRequired(mappingContext, namedDefinition, parentTypeName);
    }

}
