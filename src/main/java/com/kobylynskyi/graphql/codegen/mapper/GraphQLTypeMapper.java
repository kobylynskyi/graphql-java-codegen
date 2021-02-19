package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.DeprecatedDefinition;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.MultiLanguageDeprecated;
import com.kobylynskyi.graphql.codegen.model.NamedDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
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
import java.util.Set;

/**
 * Map GraphQL type to language-specific type (java/scala/kotlin/etc)
 *
 * @author kobylynskyi
 */
public interface GraphQLTypeMapper {

    /**
     * Get nested type of GraphQL Type. Example:
     * {@code Event -> Event}
     * {@code Event! -> Event}
     * {@code [Event!]! -> Event}
     * {@code [[Event]] -> Event}
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

    static String getMandatoryType(String typeName) {
        return typeName + "!";
    }

    static List<Directive> getDirectives(NamedNode<?> def) {
        if (def instanceof DirectivesContainer) {
            return ((DirectivesContainer<?>) def).getDirectives();
        }
        return Collections.emptyList();
    }

    /**
     * Wrap language-specific type (java/scala/kotlin/etc) into {@link List}.
     * E.g.: {@code String} becomes {@code List<String>}
     *
     * @param type           The name of a type that will be wrapped into {@code List<>}
     * @param mappingContext Global mapping context
     * @param mandatory      Type is it mandatory
     * @return String The name of the given type, wrapped into {@code List<>}
     */
    String wrapIntoList(MappingContext mappingContext, String type, boolean mandatory);

    /**
     * Return upper bounded wildcard for the given interface type:
     * {@code "Foo"} becomes {@code "List<? extends Foo>"}.
     *
     * @param type           The name of a type whose upper bound wildcard will be wrapped into a list.
     * @param mappingContext Global mapping context
     * @param mandatory      Type is it mandatory
     * @return String The name of the the wrapped type.
     */
    String wrapSuperTypeIntoList(MappingContext mappingContext, String type, boolean mandatory);

    /**
     * Wraps type into apiReturnType or subscriptionReturnType (defined in the mapping configuration).
     * Examples:
     * <p>
     * - Given GraphQL schema:                   {@code type Query { events: [Event!]! }}
     * - Given config:                           {@code useOptionalForNullableReturnTypes = true}
     * - Return:                                 {@code java.util.Optional<Event>}
     * <p>
     * - Given GraphQL schema:                   {@code type Subscription { eventsCreated: [Event!]! }}
     * - Given subscriptionReturnType in config: {@code org.reactivestreams.Publisher}
     * - Return:                                 {@code org.reactivestreams.Publisher<Event>}
     * <p>
     * - Given GraphQL schema:                   {@code type Mutation { createEvent(inp: Inp): Event }}
     * - Given apiReturnType in config:          {@code reactor.core.publisher.Mono}
     * - Return:                                 {@code reactor.core.publisher.Mono<Event>}
     * <p>
     * - Given GraphQL schema:                   {@code type Query { events: [Event!]! }}
     * - Given apiReturnListType in config:      {@code reactor.core.publisher.Flux}
     * - Return:                                 {@code reactor.core.publisher.Flux<Event>}
     *
     * @param mappingContext  Global mapping context
     * @param namedDefinition Named definition
     * @param parentTypeName  Name of the parent type
     * @return Java/Scala type wrapped into the subscriptionReturnType
     */
    String wrapApiReturnTypeIfRequired(MappingContext mappingContext,
                                       NamedDefinition namedDefinition,
                                       String parentTypeName);

    /**
     * Check if the time is primitive.
     *
     * @param possiblyPrimitiveType type to check
     * @return true if the provided type is primitive, false if the provided type is not primitive
     */
    boolean isPrimitive(String possiblyPrimitiveType);

    /**
     * Wrap string into generics type
     *
     * @param mappingContext Global mapping context
     * @param genericType    Generics type
     * @param typeParameter  Parameter of generics type
     * @return type wrapped into generics
     */
    default String getGenericsString(MappingContext mappingContext, String genericType, String typeParameter) {
        if (genericType.contains("%s")) {
            return String.format(genericType, typeParameter);
        } else {
            return String.format("%s<%s>", genericType, typeParameter);
        }
    }

    /**
     * Convert GraphQL type to a corresponding language-specific type
     *
     * @param mappingContext Global mapping context
     * @param type           GraphQL type
     * @return Corresponding language-specific type (java/scala/kotlin/etc)
     */
    default String getLanguageType(MappingContext mappingContext, Type<?> type) {
        return getLanguageType(mappingContext, type, null, null).getJavaName();
    }

    /**
     * Convert GraphQL type to a corresponding language-specific type (java/scala/kotlin/etc)
     *
     * @param mappingContext Global mapping context
     * @param graphqlType    GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @return Corresponding language-specific type (java/scala/kotlin/etc)
     */
    default NamedDefinition getLanguageType(MappingContext mappingContext, Type<?> graphqlType, String name, String parentTypeName) {
        return getLanguageType(mappingContext, graphqlType, name, parentTypeName, false, false);
    }

    /**
     * Convert GraphQL type to a corresponding language-specific type (java/scala/kotlin/etc)
     *
     * @param mappingContext Global mapping context
     * @param graphqlType    GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @param mandatory      GraphQL type is non-null
     * @param collection     GraphQL type is collection
     * @return Corresponding language-specific type (java/scala/kotlin/etc)
     */
    default NamedDefinition getLanguageType(MappingContext mappingContext, Type<?> graphqlType,
                                            String name, String parentTypeName,
                                            boolean mandatory, boolean collection) {
        if (graphqlType instanceof TypeName) {
            return getLanguageType(mappingContext, ((TypeName) graphqlType).getName(), name, parentTypeName, mandatory, collection);
        } else if (graphqlType instanceof ListType) {
            NamedDefinition mappedCollectionType = getLanguageType(mappingContext, ((ListType) graphqlType).getType(), name, parentTypeName, false, true);
            if (mappedCollectionType.isInterface() && mappingContext.getInterfacesName().contains(parentTypeName)) {
                mappedCollectionType.setJavaName(wrapSuperTypeIntoList(mappingContext, mappedCollectionType.getJavaName(), mandatory));
            } else {
                mappedCollectionType.setJavaName(wrapIntoList(mappingContext, mappedCollectionType.getJavaName(), mandatory));
            }
            return mappedCollectionType;
        } else if (graphqlType instanceof NonNullType) {
            return getLanguageType(mappingContext, ((NonNullType) graphqlType).getType(), name, parentTypeName, true, collection);
        }
        throw new IllegalArgumentException("Unknown type: " + graphqlType);
    }

    /**
     * Convert GraphQL type to a corresponding language-specific type (java/scala/kotlin/etc)
     *
     * @param mappingContext Global mapping context
     * @param graphQLType    GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @param mandatory      GraphQL type is non-null
     * @param collection     GraphQL type is collection
     * @return Corresponding language-specific type (java/scala/kotlin/etc)
     */
    default NamedDefinition getLanguageType(MappingContext mappingContext, String graphQLType, String name,
                                            String parentTypeName, boolean mandatory, boolean collection) {
        Map<String, String> customTypesMapping = mappingContext.getCustomTypesMapping();
        Set<String> serializeFieldsUsingObjectMapper = mappingContext.getUseObjectMapperForRequestSerialization();
        String langTypeName;
        boolean primitiveCanBeUsed = !collection;
        boolean serializeUsingObjectMapper = false;
        if (name != null && parentTypeName != null && customTypesMapping.containsKey(parentTypeName + "." + name)) {
            langTypeName = customTypesMapping.get(parentTypeName + "." + name);
            primitiveCanBeUsed = false;
        } else if (mandatory && customTypesMapping.containsKey(getMandatoryType(graphQLType))) {
            langTypeName = customTypesMapping.get(getMandatoryType(graphQLType));
        } else if (customTypesMapping.containsKey(graphQLType)) {
            langTypeName = customTypesMapping.get(graphQLType);
        } else {
            langTypeName = DataModelMapper.getModelClassNameWithPrefixAndSuffix(mappingContext, graphQLType);
        }
        if (serializeFieldsUsingObjectMapper.contains(graphQLType) ||
                (name != null && parentTypeName != null &&
                        serializeFieldsUsingObjectMapper.contains(parentTypeName + "." + name))) {
            serializeUsingObjectMapper = true;
        }

        return new NamedDefinition(langTypeName, graphQLType, mappingContext.getInterfacesName().contains(graphQLType),
                mandatory, primitiveCanBeUsed, serializeUsingObjectMapper);
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
    default List<String> getAnnotations(MappingContext mappingContext, Type<?> type,
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

    default List<String> getAnnotations(MappingContext mappingContext, ExtendedDefinition<?, ?> extendedDefinition) {
        return getAnnotations(mappingContext, extendedDefinition.getName(), extendedDefinition.getName(), null, Collections.emptyList(), false);
    }

    default List<String> getAnnotations(MappingContext mappingContext, String name) {
        return getAnnotations(mappingContext, name, name, null, Collections.emptyList(), false);
    }

    default List<String> getAnnotations(MappingContext mappingContext, String graphQLTypeName, String name,
                                        String parentTypeName, List<Directive> directives, boolean mandatory) {
        List<String> annotations = new ArrayList<>();
        if (mandatory) {
            String possiblyPrimitiveType = mappingContext.getCustomTypesMapping().get(getMandatoryType(graphQLTypeName));
            String modelValidationAnnotation = mappingContext.getModelValidationAnnotation();
            if (Utils.isNotBlank(modelValidationAnnotation) && addModelValidationAnnotationForType(possiblyPrimitiveType)) {
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

    boolean addModelValidationAnnotationForType(String possiblyPrimitiveType);

    default List<String> getAnnotationForDirective(MappingContext mappingContext,
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
                            getValueMapper().map(mappingContext, dirArg.getValue(), null, argumentValueFormatter));
                }
            }
            directiveAnnotationsMapped.add(directiveAnnotationMapped);
        }
        return directiveAnnotationsMapped;
    }

    default String getTypeConsideringPrimitive(MappingContext mappingContext,
                                               NamedDefinition namedDefinition,
                                               String computedTypeName) {
        String graphqlTypeName = namedDefinition.getGraphqlTypeName();
        if (namedDefinition.isMandatory() && namedDefinition.isPrimitiveCanBeUsed()) {
            String possiblyPrimitiveType = mappingContext.getCustomTypesMapping().get(getMandatoryType(graphqlTypeName));
            if (isPrimitive(possiblyPrimitiveType)) {
                return possiblyPrimitiveType;
            }
        }
        return computedTypeName;
    }

    default String getResponseReturnType(MappingContext mappingContext,
                                         NamedDefinition namedDefinition,
                                         String computedTypeName) {
        return computedTypeName;
    }

    default DeprecatedDefinition getDeprecated(MappingContext mappingContext, DirectivesContainer<?> directivesContainer) {
        return directivesContainer.getDirectives()
                .stream()
                .filter(d -> d.getName().equalsIgnoreCase(Deprecated.class.getSimpleName()))
                .findFirst()
                .map(directive -> MultiLanguageDeprecated.getLanguageDeprecated(mappingContext.getGeneratedLanguage(), directive))
                .orElse(null);
    }

    ValueMapper getValueMapper();

}
