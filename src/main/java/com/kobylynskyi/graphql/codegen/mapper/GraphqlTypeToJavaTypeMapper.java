package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.NamedDefinition;
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
     * @param mappingContext Global mapping context
     * @param type           GraphQL type
     * @return Corresponding Java type
     */
    static String getJavaType(MappingContext mappingContext, Type type) {
        return getJavaType(mappingContext, type, null, null).getName();
    }

    static String getJavaType(MappingContext mappingContext, String graphqlTypeName) {
        return getJavaType(mappingContext, graphqlTypeName, null, null).getName();
    }

    /**
     * Convert GraphQL type to a corresponding Java type
     *
     * @param mappingContext Global mapping context
     * @param graphqlType    GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @return Corresponding Java type
     */
    static NamedDefinition getJavaType(MappingContext mappingContext, Type graphqlType, String name, String parentTypeName) {
        if (graphqlType instanceof TypeName) {
            return getJavaType(mappingContext, ((TypeName) graphqlType).getName(), name, parentTypeName);
        } else if (graphqlType instanceof ListType) {
            NamedDefinition mappedCollectionType = getJavaType(mappingContext, ((ListType) graphqlType).getType(), name, parentTypeName);
            if (mappedCollectionType.isInterface() && mappingContext.getInterfaceNames().contains(parentTypeName)) {
                mappedCollectionType.setName(wrapSuperTypeIntoJavaCollection(mappedCollectionType.getName()));
            } else {
                mappedCollectionType.setName(wrapIntoJavaCollection(mappedCollectionType.getName()));
            }
            return mappedCollectionType;
        } else if (graphqlType instanceof NonNullType) {
            return getJavaType(mappingContext, ((NonNullType) graphqlType).getType(), name, parentTypeName);
        }
        throw new IllegalArgumentException("Unknown type: " + graphqlType);
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
     * @param mappingContext Global mapping context
     * @param graphQLType    GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @return Corresponding Java type
     */
    private static NamedDefinition getJavaType(MappingContext mappingContext, String graphQLType, String name, String parentTypeName) {
        Map<String, String> customTypesMapping = mappingContext.getCustomTypesMapping();
        String javaTypeName;
        if (name != null && parentTypeName != null && customTypesMapping.containsKey(parentTypeName + "." + name)) {
            javaTypeName = customTypesMapping.get(parentTypeName + "." + name);
        } else if (customTypesMapping.containsKey(graphQLType)) {
            javaTypeName = customTypesMapping.get(graphQLType);
        } else {
            javaTypeName = MapperUtils.getClassNameWithPrefixAndSuffix(mappingContext, graphQLType);
        }
        return new NamedDefinition(javaTypeName, mappingContext.getInterfaceNames().contains(graphQLType));
    }

    /**
     * Get annotations for a given GraphQL type
     *
     * @param mappingContext Global mapping context
     * @param type           GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @param mandatory      Type is mandatory
     * @return list of Java annotations for a given GraphQL type
     */
    static List<String> getAnnotations(MappingContext mappingContext, Type<?> type, String name, String parentTypeName,
                                       boolean mandatory) {
        if (type instanceof TypeName) {
            return getAnnotations(mappingContext, ((TypeName) type).getName(), name, parentTypeName, mandatory);
        } else if (type instanceof ListType) {
            return getAnnotations(mappingContext, ((ListType) type).getType(), name, parentTypeName, mandatory);
        } else if (type instanceof NonNullType) {
            return getAnnotations(mappingContext, ((NonNullType) type).getType(), name, parentTypeName, true);
        }
        return Collections.emptyList();
    }

    private static List<String> getAnnotations(MappingContext mappingContext, String graphQLType, String name, String parentTypeName, boolean mandatory) {
        List<String> annotations = new ArrayList<>();
        if (mandatory) {
            String modelValidationAnnotation = mappingContext.getModelValidationAnnotation();
            if (Utils.isNotBlank(modelValidationAnnotation)) {
                annotations.add(modelValidationAnnotation);
            }
        }
        Map<String, String> customAnnotationsMapping = mappingContext.getCustomAnnotationsMapping();
        if (name != null && parentTypeName != null && customAnnotationsMapping.containsKey(parentTypeName + "." + name)) {
            annotations.add(customAnnotationsMapping.get(parentTypeName + "." + name));
        } else if (customAnnotationsMapping.containsKey(graphQLType)) {
            annotations.add(customAnnotationsMapping.get(graphQLType));
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
     * Return upper bounded wildcard for the given interface type:
     * <code>Foo</code> becomes <code>Collection<? extends Foo></code>
     *
     * @param type Anything that will be wrapped into Collection<? extends @type>
     * @return String wrapped into Collection<? extends @type>
     */
    private static String wrapSuperTypeIntoJavaCollection(String type) {
        return String.format("java.util.Collection<? extends %s>", type);
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
     * @param mappingContext Global mapping context
     * @param javaTypeName   The type that will be wrapped into
     * @param parentTypeName Name of the parent type
     * @return Java type wrapped into the subscriptionReturnType
     */
    static String wrapIntoAsyncIfRequired(MappingContext mappingContext, String javaTypeName, String parentTypeName) {
        if (MapperUtils.shouldUseAsyncMethods(mappingContext, parentTypeName)) {
            return wrapIntoJavaCompletableFuture(javaTypeName);
        }

        return wrapIntoSubscriptionIfRequired(mappingContext, javaTypeName, parentTypeName);
    }

    /**
     * Wraps type into subscriptionReturnType (defined in the mapping configuration.
     * Example:
     * Given GraphQL schema:                           type Subscription { eventsCreated: [Event!]! }
     * Given subscriptionReturnType in mapping config: org.reactivestreams.Publisher
     * Return: org.reactivestreams.Publisher<Event>
     *
     * @param mappingContext Global mapping context
     * @param javaTypeName   The type that will be wrapped into
     * @param parentTypeName Name of the parent type
     * @return Java type wrapped into the subscriptionReturnType
     */
    private static String wrapIntoSubscriptionIfRequired(MappingContext mappingContext, String javaTypeName, String parentTypeName) {
        if (parentTypeName.equalsIgnoreCase(GraphQLOperation.SUBSCRIPTION.name())
                && Utils.isNotBlank(mappingContext.getSubscriptionReturnType())) {
            return String.format("%s<%s>", mappingContext.getSubscriptionReturnType(), javaTypeName);
        }
        return javaTypeName;
    }

}
