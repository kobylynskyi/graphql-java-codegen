package com.kobylynskyi.graphql.codegen.java;

import com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper;
import com.kobylynskyi.graphql.codegen.mapper.ValueMapper;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.NamedDefinition;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Argument;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * {@inheritDoc}
 */
public class JavaGraphQLTypeMapper implements GraphQLTypeMapper {

    public static final String JAVA_UTIL_LIST = "java.util.List";
    private static final String JAVA_UTIL_OPTIONAL = "java.util.Optional";
    private static final Set<String> JAVA_PRIMITIVE_TYPES = new HashSet<>(asList(
            "byte", "short", "int", "long", "float", "double", "char", "boolean"));

    private final ValueMapper valueMapper;

    public JavaGraphQLTypeMapper(ValueMapper valueMapper) {
        this.valueMapper = valueMapper;
    }

    public static boolean isJavaPrimitive(String possiblyPrimitiveType) {
        return JAVA_PRIMITIVE_TYPES.contains(possiblyPrimitiveType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String wrapIntoList(MappingContext mappingContext, String type) {
        return getGenericsString(mappingContext, JAVA_UTIL_LIST, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String wrapSuperTypeIntoList(MappingContext mappingContext, String type) {
        return getGenericsString(mappingContext, JAVA_UTIL_LIST, "? extends " + type);
    }

    /**
     * {@inheritDoc}
     */
    public String wrapApiReturnTypeIfRequired(MappingContext mappingContext,
                                              NamedDefinition namedDefinition,
                                              String parentTypeName) {
        String computedTypeName = namedDefinition.getJavaName();
        if (parentTypeName.equalsIgnoreCase(GraphQLOperation.SUBSCRIPTION.name()) &&
                Utils.isNotBlank(mappingContext.getSubscriptionReturnType())) {
            // in case it is subscription and subscriptionReturnType is set
            return getGenericsString(mappingContext, mappingContext.getSubscriptionReturnType(), computedTypeName);
        }

        if (Boolean.TRUE.equals(mappingContext.getUseOptionalForNullableReturnTypes()) && !namedDefinition.isMandatory()) {
            if (!computedTypeName.startsWith(JAVA_UTIL_LIST)) {
                computedTypeName = getGenericsString(mappingContext, JAVA_UTIL_OPTIONAL, computedTypeName);
            }
        }
        if (computedTypeName.startsWith(JAVA_UTIL_LIST) &&
                Utils.isNotBlank(mappingContext.getApiReturnListType())) {
            // in case it is query/mutation, return type is list and apiReturnListType is set
            return computedTypeName.replace(JAVA_UTIL_LIST, mappingContext.getApiReturnListType());
        }
        if (Utils.isNotBlank(mappingContext.getApiReturnType())) {
            // in case it is query/mutation and apiReturnType is set
            return getGenericsString(mappingContext, mappingContext.getApiReturnType(), computedTypeName);
        }
        return getTypeConsideringPrimitive(mappingContext, namedDefinition, computedTypeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPrimitive(String possiblyPrimitiveType) {
        return isJavaPrimitive(possiblyPrimitiveType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addModelValidationAnnotationForType(String type) {
        return !isPrimitive(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGenericsString(MappingContext mappingContext, String genericType, String typeParameter) {
        return String.format("%s<%s>", genericType, typeParameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String mapDirectiveArgumentValue(MappingContext mappingContext, Argument dirArg, String argumentValueFormatter) {
        return valueMapper.map(mappingContext, dirArg.getValue(), null, argumentValueFormatter);
    }

}
