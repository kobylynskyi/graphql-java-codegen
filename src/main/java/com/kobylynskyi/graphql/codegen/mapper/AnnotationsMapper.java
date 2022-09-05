package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfigConstants;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Argument;
import graphql.language.Directive;
import graphql.language.InputValueDefinition;
import graphql.language.ListType;
import graphql.language.NamedNode;
import graphql.language.NonNullType;
import graphql.language.Type;
import graphql.language.TypeName;
import graphql.language.UnionTypeDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper.getDirectives;
import static com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper.getMandatoryType;

/**
 * Mapper for annotations
 */
public abstract class AnnotationsMapper {

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
    public List<String> getAnnotations(MappingContext mappingContext, Type<?> type,
                                       NamedNode<?> def, String parentTypeName, boolean mandatory) {
        if (type instanceof ListType) {
            Type<?> subType = ((ListType) type).getType();
            return getAnnotations(mappingContext, subType, def, parentTypeName, mandatory);
        } else if (type instanceof NonNullType) {
            Type<?> parentType = null;
            if (def instanceof ExtendedFieldDefinition) {
                parentType = ((ExtendedFieldDefinition) def).getType();
            } else if (def instanceof InputValueDefinition) {
                parentType = ((InputValueDefinition) def).getType();
            }
            // if parent is a list, then pass a mandatory flag as is (do not override it)
            if (!(parentType instanceof ListType)) {
                mandatory = true;
            }
            return getAnnotations(mappingContext, ((NonNullType) type).getType(), def, parentTypeName, mandatory);
        } else if (type instanceof TypeName) {
            return getAnnotations(mappingContext, ((TypeName) type).getName(), def.getName(), parentTypeName,
                    getDirectives(def), mandatory, def);
        }
        return Collections.emptyList();
    }

    public List<String> getAnnotations(MappingContext mappingContext, ExtendedDefinition<?, ?> extendedDefinition) {
        if (extendedDefinition == null) {
            return Collections.emptyList();
        }

        NamedNode<?> def = extendedDefinition.getDefinition();
        return getAnnotations(mappingContext, extendedDefinition.getName(), extendedDefinition.getName(), null,
                extendedDefinition.getDirectives(), false, def);
    }

    public List<String> getAnnotations(MappingContext mappingContext, String name) {
        return getAnnotations(mappingContext, name, name, null, Collections.emptyList(), false, null);
    }

    /**
     * Get annotations for a given GraphQL type
     *
     * @param mappingContext  Global mapping context
     * @param graphQLTypeName GraphQL type
     * @param name            Name of the GraphQL type
     * @param parentTypeName  Name of the parent type
     * @param directives      List of GraphQL directive
     * @param mandatory       Type is mandatory
     * @param def             GraphQL definition
     * @return list of Java annotations for a given GraphQL type
     */
    public List<String> getAnnotations(MappingContext mappingContext, String graphQLTypeName, String name,
                                       String parentTypeName, List<Directive> directives, boolean mandatory,
                                       NamedNode<?> def) {
        // 1. Add model validation annotation
        List<String> annotations = new ArrayList<>();
        if (mandatory) {
            String modelValidationAnnotation = getModelValidationAnnotation(mappingContext, graphQLTypeName);
            if (modelValidationAnnotation != null) {
                annotations.add(modelValidationAnnotation);
            }
        }

        // 2.1. Add custom annotations from the configuration for: GraphQLObjectName.fieldName
        annotations.addAll(getTypeAnnotationsForKey(mappingContext, parentTypeName + "." + name));
        // 2.2. Add custom annotations from the configuration for: GraphQLTypeName
        annotations.addAll(getTypeAnnotationsForKey(mappingContext, graphQLTypeName));

        // 3. Add Jackson-related annotations
        annotations.addAll(getJacksonTypeIdAnnotations(mappingContext, def));

        // 4. Get additional language-specific annotations
        if (def instanceof ExtendedFieldDefinition) {
            annotations.addAll(getAdditionalAnnotations(mappingContext, graphQLTypeName));
        }

        // 5. Add annotations according to directives configurations
        Map<String, List<String>> directiveAnnotationsMapping = mappingContext.getDirectiveAnnotationsMapping();
        for (Directive directive : directives) {
            List<String> directiveAnnotations = directiveAnnotationsMapping.get(directive.getName());
            if (!Utils.isEmpty(directiveAnnotations)) {
                annotations.addAll(getAnnotationsForDirective(mappingContext, directiveAnnotations, directive));
            }
        }
        // 6. Add annotations for resolver arguments
        if (!Utils.isEmpty(mappingContext.getResolverArgumentAnnotations())
                && mappingContext.getOperationsName().contains(parentTypeName)) {
            annotations.addAll(mappingContext.getResolverArgumentAnnotations());
        }
        // 7. Add annotations for parametrized resolvers
        if (!Utils.isEmpty(mappingContext.getParametrizedResolverAnnotations())
                && mappingContext.getFieldNamesWithResolvers().contains(parentTypeName + "." + name)) {
            for (String annotation : mappingContext.getParametrizedResolverAnnotations()) {
                annotations.add(annotation.replace(MappingConfigConstants.TYPE_NAME_PLACEHOLDER, parentTypeName));
            }
        }
        return annotations;
    }

    private static List<String> getTypeAnnotationsForKey(MappingContext mappingContext,
                                                         String key) {
        List<String> result = new ArrayList<>();
        Map<String, List<String>> customAnnotationsMapping = mappingContext.getCustomAnnotationsMapping();
        List<String> typeAnnotations = customAnnotationsMapping.get(key);
        if (!Utils.isEmpty(typeAnnotations)) {
            result.addAll(typeAnnotations);
        } else {
            // try finding if there's a RegEx present for this type
            for (Map.Entry<String, List<String>> entry : customAnnotationsMapping.entrySet()) {
                if (key.matches(entry.getKey()) && !Utils.isEmpty(entry.getValue())) {
                    result.addAll(entry.getValue());
                }
            }
        }
        return result;
    }

    private String getModelValidationAnnotation(MappingContext mappingContext, String graphQLTypeName) {
        String possiblyPrimitiveType = mappingContext.getCustomTypesMapping()
                .get(getMandatoryType(graphQLTypeName));
        String modelValidationAnnotation = mappingContext.getModelValidationAnnotation();
        if (Utils.isNotBlank(modelValidationAnnotation) &&
                addModelValidationAnnotationForType(possiblyPrimitiveType)) {
            return modelValidationAnnotation;
        }
        return null;
    }

    /**
     * Get a list of annotations for a given directive based on mapping config
     *
     * @param mappingContext       Global mapping context
     * @param directiveAnnotations List of directive annotations
     * @param directive            GraphQL Directive
     * @return a list of annotations to be added for all types that have given directive
     */
    public List<String> getAnnotationsForDirective(MappingContext mappingContext,
                                                   List<String> directiveAnnotations,
                                                   Directive directive) {
        List<String> directiveAnnotationsMapped = new ArrayList<>();
        for (String annotation : directiveAnnotations) {
            String directiveAnnotationMapped = annotation;
            for (Argument dirArg : directive.getArguments()) {
                String argumentValueFormatter = Utils.substringBetween(annotation, "{{" + dirArg.getName(), "}}");
                // if argumentValueFormatter == null then the placeholder {{dirArg.getName()}} does not exist
                if (argumentValueFormatter != null) {
                    String replace = String.format("{{%s%s}}", dirArg.getName(), argumentValueFormatter);
                    String replaceWith = getValueMapper().map(
                            mappingContext, dirArg.getValue(), null, argumentValueFormatter);
                    directiveAnnotationMapped = directiveAnnotationMapped.replace(replace, replaceWith);
                }
            }
            directiveAnnotationsMapped.add(directiveAnnotationMapped);
        }
        return directiveAnnotationsMapped;
    }

    /**
     * Get Jackson type id resolver annotations
     *
     * @param mappingContext Global mapping context
     * @param def            GraphQL definition
     * @return list of Jackson type id resolver annotations
     */
    public List<String> getJacksonTypeIdAnnotations(MappingContext mappingContext, NamedNode<?> def) {
        List<String> defaults = new ArrayList<>();
        if (Boolean.TRUE.equals(mappingContext.getGenerateJacksonTypeIdResolver())
                && def instanceof UnionTypeDefinition) {
            defaults.add("com.fasterxml.jackson.annotation.JsonTypeInfo(use = " +
                    "com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = \"__typename\")");
            String modelPackageName = DataModelMapper.getModelPackageName(mappingContext);
            if (modelPackageName == null) {
                modelPackageName = "";
            } else if (Utils.isNotBlank(modelPackageName)) {
                modelPackageName += ".";
            }
            defaults.add(getJacksonResolverTypeIdAnnotation(modelPackageName));
        }
        return defaults;
    }

    /**
     * Get language specific Jackson type id resolver annotation
     *
     * @param modelPackageName Model package name property
     * @return language specific Jackson type id resolver annotation
     */
    public abstract String getJacksonResolverTypeIdAnnotation(String modelPackageName);

    protected abstract List<String> getAdditionalAnnotations(MappingContext mappingContext, String typeName);

    /**
     * Whether to add model validation annotation to a type
     *
     * @param type GraphQL type name
     * @return true if model validation annotation should be added to a type
     */
    public abstract boolean addModelValidationAnnotationForType(String type);

    public abstract ValueMapper getValueMapper();

}
