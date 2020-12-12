package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper;
import com.kobylynskyi.graphql.codegen.mapper.ValueMapper;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.NamedDefinition;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Argument;

import java.util.HashSet;
import java.util.Set;

import static com.kobylynskyi.graphql.codegen.java.JavaGraphQLTypeMapper.JAVA_UTIL_LIST;
import static java.util.Arrays.asList;

/**
 * @author 梦境迷离
 * @since 2020/12/09
 */
public class KotlinGraphQLTypeMapper implements GraphQLTypeMapper {

    private static final String KOTLIN_UTIL_LIST = "List";
    private static final String KOTLIN_UTIL_NULLABLE = "?";
    // Char Boolean are not primitive type, but non null equivalent jvm primitive types.
    private static final Set<String> KOTLIN_PRIMITIVE_TYPES = new HashSet<>(asList("Byte", "Short", "Int", "Long", "Float", "Double", "Char", "Boolean"));

    private final ValueMapper valueMapper;

    public KotlinGraphQLTypeMapper(ValueMapper valueMapper) {
        this.valueMapper = valueMapper;
    }

    public static boolean isKotlinPrimitive(String scalaType) {
        return KOTLIN_PRIMITIVE_TYPES.contains(scalaType);
    }

    @Override
    public String wrapIntoList(MappingContext mappingContext, String type, boolean mandatory) {
        if (!mandatory && !type.endsWith("?")) {
            return getGenericsString(mappingContext, KOTLIN_UTIL_LIST, type + "?");
        }
        return getGenericsString(mappingContext, KOTLIN_UTIL_LIST, type);
    }

    @Override
    public String wrapSuperTypeIntoList(MappingContext mappingContext, String type, boolean mandatory) {
        if (!mandatory && !type.endsWith("?")) {
            return getGenericsString(mappingContext, KOTLIN_UTIL_LIST, "out " + type + "?");
        }
        return getGenericsString(mappingContext, KOTLIN_UTIL_LIST, "out " + type);
    }

    @Override
    public String wrapApiReturnTypeIfRequired(MappingContext mappingContext,
                                              NamedDefinition namedDefinition,
                                              String parentTypeName) {
        String computedTypeName = namedDefinition.getJavaName();
        if (parentTypeName.equalsIgnoreCase(GraphQLOperation.SUBSCRIPTION.name()) &&
                Utils.isNotBlank(mappingContext.getSubscriptionReturnType())) {
            // in case it is subscription and subscriptionReturnType is set
            return getGenericsString(mappingContext, mappingContext.getSubscriptionReturnType(), computedTypeName);
        }
        // Consider only Java-kotlin OR Java-Scala cross language calls
        if (Boolean.TRUE.equals(mappingContext.getUseOptionalForNullableReturnTypes()) && !namedDefinition.isMandatory()) {
            if (!computedTypeName.startsWith(KOTLIN_UTIL_LIST) && !computedTypeName.startsWith(JAVA_UTIL_LIST)) {
                // append `?` (except java list and kotlin list)
                computedTypeName = getNullableString(mappingContext, computedTypeName);
            }
        }

        if (computedTypeName.startsWith(KOTLIN_UTIL_LIST) &&
                Utils.isNotBlank(mappingContext.getApiReturnListType())) {
            // in case it is query/mutation, return type is list and apiReturnListType is set
            return computedTypeName.replace(KOTLIN_UTIL_LIST, mappingContext.getApiReturnListType());
        }
        if (Utils.isNotBlank(mappingContext.getApiReturnType())) {
            // in case it is query/mutation and apiReturnType is set
            return getGenericsString(mappingContext, mappingContext.getApiReturnType(), computedTypeName);
        }
        return getTypeConsideringPrimitive(mappingContext, namedDefinition, computedTypeName);
    }

    @Override
    public boolean isPrimitive(String kotlinType) {
        return isKotlinPrimitive(kotlinType);
    }

    @Override
    public String getGenericsString(MappingContext mappingContext, String genericType, String typeParameter) {
        return String.format("%s<%s>", genericType, typeParameter);
    }

    @Override
    public String mapDirectiveArgumentValue(MappingContext mappingContext, Argument dirArg, String argumentValueFormatter) {
        return valueMapper.map(mappingContext, dirArg.getValue(), null, argumentValueFormatter);
    }

    @Override
    public boolean addModelValidationAnnotationForType(String possiblyPrimitiveType) {
        return false;
    }

    public static String defaultValueKotlinPrimitive(String kotlinType) {
        switch (kotlinType) {
            case "Long":
                return "0L";
            case "Float":
                return "0F";
            case "Double":
                return "0D";
            case "Char":
                return "0.toChar()";
            case "Boolean":
                return "false";
            case "Int":
            case "Byte":
            case "Short":
            default:
                return "0";
        }
    }

    private String getNullableString(MappingContext mappingContext, String typeParameter) {
        return typeParameter + KotlinGraphQLTypeMapper.KOTLIN_UTIL_NULLABLE;
    }

    @Override
    public String getTypeConsideringPrimitive(MappingContext mappingContext,
                                              NamedDefinition namedDefinition,
                                              String computedTypeName) {
        String graphqlTypeName = namedDefinition.getGraphqlTypeName();
        if (namedDefinition.isMandatory() && namedDefinition.isPrimitiveCanBeUsed()) {
            String possiblyPrimitiveType = mappingContext.getCustomTypesMapping().get(GraphQLTypeMapper.getMandatoryType(graphqlTypeName));
            if (isPrimitive(possiblyPrimitiveType)) {
                return possiblyPrimitiveType;
            }
        }
        // It is possible that the user has already used `useOptionalForNullableReturnTypes`
        if (!namedDefinition.isMandatory() && !computedTypeName.endsWith("?")) {
            return computedTypeName + "?";
        }
        return computedTypeName;
    }
}
