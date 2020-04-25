package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.Type;
import graphql.language.TypeName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Map GraphQL type to Java type
 *
 * @author kobylynskyi
 */
class GraphqlTypeToJavaTypeMapper {

    /**
     * Convert GraphQL type to a corresponding Java type
     *
     * @param mappingConfig Global mapping configuration
     * @param type          GraphQL type
     * @return Corresponding Java type
     */
    static String getJavaType(MappingConfig mappingConfig, Type type) {
        return getJavaType(mappingConfig, type, null, null);
    }

    static String getJavaType(MappingConfig mappingConfig, String graphqlTypeName) {
        return getJavaType(mappingConfig, graphqlTypeName, null, null);
    }

    /**
     * Convert GraphQL type to a corresponding Java type
     *
     * @param mappingConfig  Global mapping configuration
     * @param graphqlType    GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @return Corresponding Java type
     */
    static String getJavaType(MappingConfig mappingConfig, Type graphqlType, String name, String parentTypeName) {
        if (graphqlType instanceof TypeName) {
            return getJavaType(mappingConfig, ((TypeName) graphqlType).getName(), name, parentTypeName);
        } else if (graphqlType instanceof ListType) {
            String mappedCollectionType = getJavaType(mappingConfig, ((ListType) graphqlType).getType(), name, parentTypeName);
            return wrapIntoJavaCollection(mappedCollectionType);
        } else if (graphqlType instanceof NonNullType) {
            return getJavaType(mappingConfig, ((NonNullType) graphqlType).getType(), name, parentTypeName);
        }
        return null;
    }

    /**
     * Get nested type of GraphQL Type. Example:
     * Event -> Event
     * Event! -> Event
     * [Event!]! -> Event
     * [[Event]] -> Event
     *
     * @param graphqlType GraphQL type
     * @return GraphQL type without List/NonNull wrapping
     */
    static String getNestedTypeName(Type graphqlType) {
        if (graphqlType instanceof TypeName) {
            return ((TypeName) graphqlType).getName();
        } else if (graphqlType instanceof ListType) {
            return getNestedTypeName(((ListType) graphqlType).getType());
        } else if (graphqlType instanceof NonNullType) {
            return getNestedTypeName(((NonNullType) graphqlType).getType());
        }
        return null;
    }

    /**
     * Convert GraphQL type to a corresponding Java type
     *
     * @param mappingConfig  Global mapping configuration
     * @param graphlType     GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @return Corresponding Java type
     */
    private static String getJavaType(MappingConfig mappingConfig, String graphlType, String name, String parentTypeName) {
        Map<String, String> customTypesMapping = mappingConfig.getCustomTypesMapping();
        if (name != null && parentTypeName != null && customTypesMapping.containsKey(parentTypeName + "." + name)) {
            return customTypesMapping.get(parentTypeName + "." + name);
        } else if (customTypesMapping.containsKey(graphlType)) {
            return customTypesMapping.get(graphlType);
        }
        return MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, graphlType);
    }

    /**
     * Get annotations for a given GraphQL type
     *
     * @param mappingConfig  Global mapping configuration
     * @param type           GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @param mandatory      Type is mandatory
     * @return list of Java annotations for a given GraphQL type
     */
    static List<String> getAnnotations(MappingConfig mappingConfig, Type<?> type, String name, String parentTypeName,
                                       boolean mandatory) {
        if (type instanceof TypeName) {
            return getAnnotations(mappingConfig, ((TypeName) type).getName(), name, parentTypeName, mandatory);
        } else if (type instanceof ListType) {
            return getAnnotations(mappingConfig, ((ListType) type).getType(), name, parentTypeName, mandatory);
        } else if (type instanceof NonNullType) {
            return getAnnotations(mappingConfig, ((NonNullType) type).getType(), name, parentTypeName, true);
        }
        return Collections.emptyList();
    }

    private static List<String> getAnnotations(MappingConfig mappingConfig, String graphlType, String name, String parentTypeName, boolean mandatory) {
        List<String> annotations = new ArrayList<>();
        if (mandatory) {
            String modelValidationAnnotation = mappingConfig.getModelValidationAnnotation();
            if (Utils.isNotBlank(modelValidationAnnotation)) {
                annotations.add(modelValidationAnnotation);
            }
        }
        Map<String, String> customAnnotationsMapping = mappingConfig.getCustomAnnotationsMapping();
        if (name != null && parentTypeName != null && customAnnotationsMapping.containsKey(parentTypeName + "." + name)) {
            annotations.add(customAnnotationsMapping.get(parentTypeName + "." + name));
        } else if (customAnnotationsMapping.containsKey(graphlType)) {
            annotations.add(customAnnotationsMapping.get(graphlType));
        }
        return annotations;
    }

    /**
     * Wrap java type into collection. E.g.: "String" becomes "Collection<String"
     *
     * @param type Anything that will be wrapped into Collection<>
     * @return String wrapped into Collection<>
     */
    private static String wrapIntoJavaCollection(String type) {
        return String.format("java.util.Collection<%s>", type);
    }

    /**
     * Wrap java type into {@link java.util.concurrent.CompletableFuture}. E.g.: "String" becomes "CompletableFuture<String>"
     *
     * @param type Anything that will be wrapped into CompletableFuture<>
     * @return String wrapped into CompletableFuture<>
     */
    private static String wrapIntoJavaCompletableFuture(String type) {
        return String.format("java.util.concurrent.CompletableFuture<%s>", type);
    }

    /**
     * Wraps type taking into account if an async api is needed, whether it is a Query, Mutation or Subscription
     *
     * @param mappingConfig  Global mapping configuration
     * @param javaTypeName   The type that will be wrapped into
     * @param parentTypeName Name of the parent type
     * @return Java type wrapped into the subscriptionReturnType
     */
    static String wrapIntoAsyncIfRequired(MappingConfig mappingConfig, String javaTypeName, String parentTypeName) {
        if (MapperUtils.shouldUseAsyncMethods(mappingConfig, parentTypeName)) {
            return wrapIntoJavaCompletableFuture(javaTypeName);
        }

        return wrapIntoSubscriptionIfRequired(mappingConfig, javaTypeName, parentTypeName);
    }

    /**
     * Wraps type into subscriptionReturnType (defined in the mapping configuration.
     * Example:
     * Given GraphQL schema:                           type Subscription { eventsCreated: [Event!]! }
     * Given subscriptionReturnType in mapping config: org.reactivestreams.Publisher
     * Return: org.reactivestreams.Publisher<Event>
     *
     * @param mappingConfig  Global mapping configuration
     * @param javaTypeName   The type that will be wrapped into
     * @param parentTypeName Name of the parent type
     * @return Java type wrapped into the subscriptionReturnType
     */
    private static String wrapIntoSubscriptionIfRequired(MappingConfig mappingConfig, String javaTypeName, String parentTypeName) {
        if (parentTypeName.equalsIgnoreCase(GraphQLOperation.SUBSCRIPTION.name())
                && Utils.isNotBlank(mappingConfig.getSubscriptionReturnType())) {
            return String.format("%s<%s>", mappingConfig.getSubscriptionReturnType(), javaTypeName);
        }
        return javaTypeName;
    }

}
