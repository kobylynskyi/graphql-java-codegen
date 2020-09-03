package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.NamedDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Argument;
import graphql.language.Directive;
import graphql.language.DirectivesContainer;
import graphql.language.ListType;
import graphql.language.NamedNode;
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

    private static final String JAVA_UTIL_LIST = "java.util.List";
    private static final String JAVA_UTIL_OPTIONAL = "java.util.Optional";

    private GraphqlTypeToJavaTypeMapper() {
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
    static String getNestedTypeName(Type<?> graphqlType) {
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
     * @param type           GraphQL type
     * @return Corresponding Java type
     */
    static String getJavaType(MappingContext mappingContext, Type<?> type) {
        return getJavaType(mappingContext, type, null, null).getName();
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
    static NamedDefinition getJavaType(MappingContext mappingContext, Type<?> graphqlType, String name, String parentTypeName) {
        return getJavaType(mappingContext, graphqlType, name, parentTypeName, false);
    }

    /**
     * Convert GraphQL type to a corresponding Java type
     *
     * @param mappingContext Global mapping context
     * @param graphqlType    GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @param mandatory      GraphQL type is non-null
     * @return Corresponding Java type
     */
    static NamedDefinition getJavaType(MappingContext mappingContext, Type<?> graphqlType, String name, String parentTypeName,
                                       boolean mandatory) {
        if (graphqlType instanceof TypeName) {
            return getJavaType(mappingContext, ((TypeName) graphqlType).getName(), name, parentTypeName, mandatory);
        } else if (graphqlType instanceof ListType) {
            NamedDefinition mappedCollectionType = getJavaType(mappingContext, ((ListType) graphqlType).getType(), name, parentTypeName, false);
            if (mappedCollectionType.isInterface() && mappingContext.getInterfacesName().contains(parentTypeName)) {
                mappedCollectionType.setName(wrapSuperTypeIntoJavaList(mappedCollectionType.getName()));
            } else {
                mappedCollectionType.setName(wrapIntoJavaList(mappedCollectionType.getName()));
            }
            return mappedCollectionType;
        } else if (graphqlType instanceof NonNullType) {
            return getJavaType(mappingContext, ((NonNullType) graphqlType).getType(), name, parentTypeName, true);
        }
        throw new IllegalArgumentException("Unknown type: " + graphqlType);
    }

    /**
     * Convert GraphQL type to a corresponding Java type
     *
     * @param mappingContext Global mapping context
     * @param graphQLType    GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @param mandatory      GraphQL type is non-null
     * @return Corresponding Java type
     */
    private static NamedDefinition getJavaType(MappingContext mappingContext, String graphQLType, String name,
                                               String parentTypeName, boolean mandatory) {
        Map<String, String> customTypesMapping = mappingContext.getCustomTypesMapping();
        String javaTypeName;
        if (name != null && parentTypeName != null && customTypesMapping.containsKey(parentTypeName + "." + name)) {
            javaTypeName = customTypesMapping.get(parentTypeName + "." + name);
        } else if (customTypesMapping.containsKey(graphQLType)) {
            javaTypeName = customTypesMapping.get(graphQLType);
        } else {
            javaTypeName = MapperUtils.getModelClassNameWithPrefixAndSuffix(mappingContext, graphQLType);
        }
        return new NamedDefinition(javaTypeName, mappingContext.getInterfacesName().contains(graphQLType), mandatory);
    }

    /**
     * Get annotations for a given GraphQL type
     *
     * @param mappingContext Global mapping context
     * @param type           GraphQL type
     * @param def            GraphQL definition
     * @param parentTypeName Name of the parent type
     * @param mandatory      Type is mandatory
     * @return list of Java annotations for a given GraphQL type
     */
    static List<String> getAnnotations(MappingContext mappingContext, Type<?> type,
                                       NamedNode<?> def, String parentTypeName, boolean mandatory) {
        if (type instanceof ListType) {
            return getAnnotations(mappingContext, ((ListType) type).getType(), def, parentTypeName, mandatory);
        } else if (type instanceof NonNullType) {
            return getAnnotations(mappingContext, ((NonNullType) type).getType(), def, parentTypeName, true);
        } else if (type instanceof TypeName) {
            return getAnnotations(mappingContext, ((TypeName) type).getName(), def.getName(), parentTypeName, getDirectives(def), mandatory);
        }
        return Collections.emptyList();
    }

    static List<String> getAnnotations(MappingContext mappingContext, ExtendedDefinition<?, ?> extendedDefinition) {
        return getAnnotations(mappingContext, extendedDefinition.getName(), extendedDefinition.getName(), null, Collections.emptyList(), false);
    }

    static List<String> getAnnotations(MappingContext mappingContext, String name) {
        return getAnnotations(mappingContext, name, name, null, Collections.emptyList(), false);
    }

    private static List<String> getAnnotations(MappingContext mappingContext, String graphQLTypeName, String name,
                                               String parentTypeName, List<Directive> directives, boolean mandatory) {
        List<String> annotations = new ArrayList<>();
        if (mandatory) {
            String modelValidationAnnotation = mappingContext.getModelValidationAnnotation();
            if (Utils.isNotBlank(modelValidationAnnotation)) {
                annotations.add(modelValidationAnnotation);
            }
        }
        Map<String, List<String>> customAnnotationsMapping = mappingContext.getCustomAnnotationsMapping();
        if (name != null && parentTypeName != null && customAnnotationsMapping.containsKey(parentTypeName + "." + name)) {
            List<String> annotationsToAdd = customAnnotationsMapping.get(parentTypeName + "." + name);
            if (!Utils.isEmpty(annotationsToAdd)) {
                annotations.addAll(annotationsToAdd);
            }
        } else if (customAnnotationsMapping.containsKey(graphQLTypeName)) {
            List<String> annotationsToAdd = customAnnotationsMapping.get(graphQLTypeName);
            if (!Utils.isEmpty(annotationsToAdd)) {
                annotations.addAll(annotationsToAdd);
            }
        }
        Map<String, List<String>> directiveAnnotationsMapping = mappingContext.getDirectiveAnnotationsMapping();
        for (Directive directive : directives) {
            if (directiveAnnotationsMapping.containsKey(directive.getName())) {
                annotations.addAll(getAnnotationForDirective(mappingContext, directiveAnnotationsMapping.get(directive.getName()), directive));
            }
        }
        return annotations;
    }

    private static List<String> getAnnotationForDirective(MappingContext mappingContext,
                                                          List<String> directiveAnnotations,
                                                          Directive directive) {
        List<String> directiveAnnotationsMapped = new ArrayList<>();
        for (String annotation : directiveAnnotations) {
            String directiveAnnotationMapped = annotation;
            for (Argument dirArg : directive.getArguments()) {
                String argumentValueFormatter = Utils.substringBetween(annotation, "{{" + dirArg.getName(), "}}");
                // if argumentValueFormatter == null then the placeholder {{dirArg.getName()}} does not exist
                if (argumentValueFormatter != null) {
                    directiveAnnotationMapped = directiveAnnotationMapped.replace(
                            String.format("{{%s%s}}", dirArg.getName(), argumentValueFormatter),
                            ValueMapper.map(mappingContext, dirArg.getValue(), null, argumentValueFormatter));
                }
            }
            directiveAnnotationsMapped.add(directiveAnnotationMapped);
        }
        return directiveAnnotationsMapped;
    }

    /**
     * Wrap Java type into {@link java.util.List}. E.g.: {@code "String"} becomes {@code "List<String>"}
     *
     * @param type The name of a type that will be wrapped into List<>
     * @return String The name of the given type, wrapped into List<>
     */
    private static String wrapIntoJavaList(String type) {
        return getGenericsString(JAVA_UTIL_LIST, type);
    }

    /**
     * Return upper bounded wildcard for the given interface type:
     * {@code "Foo"} becomes {@code "List<? extends Foo>"}.
     *
     * @param type The name of a type whose upper bound wildcard will be wrapped into a list.
     * @return String The name of the the wrapped type.
     */
    private static String wrapSuperTypeIntoJavaList(String type) {
        return getGenericsString(JAVA_UTIL_LIST, "? extends " + type);
    }

    /**
     * Wraps type into apiReturnType or subscriptionReturnType (defined in the mapping configuration).
     * Examples:
     * <p>
     * - Given GraphQL schema:                   type Query { events: [Event!]! }
     * - Given config:                           useOptionalForNullableReturnTypes = true
     * - Return:                                 java.util.Optional<Event>
     * <p>
     * - Given GraphQL schema:                   type Subscription { eventsCreated: [Event!]! }
     * - Given subscriptionReturnType in config: org.reactivestreams.Publisher
     * - Return:                                 org.reactivestreams.Publisher<Event>
     * <p>
     * - Given GraphQL schema:                   type Mutation { createEvent(inp: Inp): Event }
     * - Given apiReturnType in config:          reactor.core.publisher.Mono
     * - Return:                                 reactor.core.publisher.Mono<Event>
     * <p>
     * - Given GraphQL schema:                   type Query { events: [Event!]! }
     * - Given apiReturnListType in config:      reactor.core.publisher.Flux
     * - Return:                                 reactor.core.publisher.Flux<Event>
     *
     * @param mappingContext  Global mapping context
     * @param namedDefinition Named definition
     * @param parentTypeName  Name of the parent type
     * @return Java type wrapped into the subscriptionReturnType
     */
    static String wrapApiReturnTypeIfRequired(MappingContext mappingContext, NamedDefinition namedDefinition, String parentTypeName) {
        String javaTypeName = namedDefinition.getName();
        if (parentTypeName.equalsIgnoreCase(GraphQLOperation.SUBSCRIPTION.name())) {
            if (Utils.isNotBlank(mappingContext.getSubscriptionReturnType())) {
                // in case it is subscription and subscriptionReturnType is set
                return getGenericsString(mappingContext.getSubscriptionReturnType(), javaTypeName);
            }
        } else if (Boolean.TRUE.equals(mappingContext.getUseOptionalForNullableReturnTypes())) {
            // wrap the type into java.util.Optional (except lists)
            if (!namedDefinition.isMandatory() && !javaTypeName.startsWith(JAVA_UTIL_LIST)) {
                return getGenericsString(JAVA_UTIL_OPTIONAL, javaTypeName);
            }
        } else {
            if (javaTypeName.startsWith(JAVA_UTIL_LIST) &&
                    Utils.isNotBlank(mappingContext.getApiReturnListType())) {
                // in case it is query/mutation, return type is list and apiReturnListType is set
                return javaTypeName.replace(JAVA_UTIL_LIST, mappingContext.getApiReturnListType());
            }
            if (Utils.isNotBlank(mappingContext.getApiReturnType())) {
                // in case it is query/mutation and apiReturnType is set
                return getGenericsString(mappingContext.getApiReturnType(), javaTypeName);
            }
        }
        return javaTypeName;
    }

    static String getGenericsString(String genericType, String typeParameter) {
        return String.format("%s<%s>", genericType, typeParameter);
    }

    private static List<Directive> getDirectives(NamedNode<?> def) {
        if (def instanceof DirectivesContainer) {
            return ((DirectivesContainer<?>) def).getDirectives();
        }
        return Collections.emptyList();
    }

}
