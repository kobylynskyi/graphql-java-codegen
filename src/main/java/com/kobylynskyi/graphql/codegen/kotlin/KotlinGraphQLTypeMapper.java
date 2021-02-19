package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper;
import com.kobylynskyi.graphql.codegen.mapper.ValueMapper;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.NamedDefinition;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.HashSet;
import java.util.Set;

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
        //null2Query: [Int], mandatory=false
        //null3Query: [Int!], mandatory=false
        //null4Query: [Int!]!, mandatory=true
        //null5Query: [Int]!, mandatory=true
        //So the coercion depends on the outer type, where the inner type is already handled. (due to recursion)
        if (!mandatory) {
            return getGenericsString(mappingContext, KOTLIN_UTIL_LIST, type) + "?";
        }
        return getGenericsString(mappingContext, KOTLIN_UTIL_LIST, type);
    }

    @Override
    public String wrapSuperTypeIntoList(MappingContext mappingContext, String type, boolean mandatory) {
        if (!mandatory) {
            return getGenericsString(mappingContext, KOTLIN_UTIL_LIST, "out " + type) + "?";
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
    public boolean addModelValidationAnnotationForType(String possiblyPrimitiveType) {
        return false;
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
        if (!namedDefinition.isMandatory()) {
            //All `ListType` samples have been processed by `wrapIntoList` and `wrapSuperTypeIntoList`
            if (!computedTypeName.endsWith(KOTLIN_UTIL_NULLABLE) && !computedTypeName.startsWith(KOTLIN_UTIL_LIST)) {
                return computedTypeName + KOTLIN_UTIL_NULLABLE;
            }
            // If it is not processed by 'wrapSuperTypeIntoList' and 'wrapIntoList', the nullable type may be missing here
            // Such as return a query type: ```codesOfConduct: [CodeOfConduct]```
            if (computedTypeName.startsWith(KOTLIN_UTIL_LIST) && !graphqlTypeName.endsWith(KOTLIN_UTIL_NULLABLE)) {
                String modelClassNameWithPrefixAndSuffix = DataModelMapper.getModelClassNameWithPrefixAndSuffix(mappingContext, graphqlTypeName);
                if (computedTypeName.contains(modelClassNameWithPrefixAndSuffix + KOTLIN_UTIL_NULLABLE) ||
                        computedTypeName.contains(graphqlTypeName + KOTLIN_UTIL_NULLABLE)) {
                    return computedTypeName;
                }
                if (!computedTypeName.contains(modelClassNameWithPrefixAndSuffix + KOTLIN_UTIL_NULLABLE) && computedTypeName.contains(modelClassNameWithPrefixAndSuffix)) {
                    return computedTypeName.replace(modelClassNameWithPrefixAndSuffix, modelClassNameWithPrefixAndSuffix + KOTLIN_UTIL_NULLABLE);
                }
                if (!computedTypeName.contains(graphqlTypeName + KOTLIN_UTIL_NULLABLE) && computedTypeName.contains(graphqlTypeName)) {
                    return computedTypeName.replace(graphqlTypeName, graphqlTypeName + KOTLIN_UTIL_NULLABLE);
                }
            }
        }

        return computedTypeName;
    }

    @Override
    public ValueMapper getValueMapper() {
        return valueMapper;
    }

    /**
     * Thi method was used in kotlin template, do not remove it.
     *
     * @param kotlinType type get from Type template
     * @return default value
     */
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

    @Override
    public String getResponseReturnType(MappingContext mappingContext, NamedDefinition namedDefinition, String computedTypeName) {
        // Delegate to getTypeConsideringPrimitive.
        // For kotlin such as XXXXXXResponse do not implement the mandatory function of graphql correctly when returnType is not List. Should fix it when generate response class.
        return getTypeConsideringPrimitive(mappingContext, namedDefinition, computedTypeName);
    }
}
