package com.kobylynskyi.graphql.codegen.java;

import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper;
import com.kobylynskyi.graphql.codegen.model.MappingConfigConstants;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.NamedDefinition;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.InputValueDefinition;
import graphql.language.NullValue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * Mapper class for converting GraphQL types to Java types
 */
public class JavaGraphQLTypeMapper extends GraphQLTypeMapper {

    public static final String JAVA_UTIL_LIST = "java.util.List";
    public static final Pattern JAVA_UTIL_LIST_ELEMENT_REGEX = Pattern.compile("java\\.util\\.List<(.+)>");
    private static final String JAVA_UTIL_OPTIONAL = "java.util.Optional";
    private static final String INPUT_WRAPPER_CLASS = "GraphQLInputParameter";
    private static final Set<String> JAVA_PRIMITIVE_TYPES = new HashSet<>(asList(
            "byte", "short", "int", "long", "float", "double", "char", "boolean"));

    public static boolean isJavaPrimitive(String possiblyPrimitiveType) {
        return JAVA_PRIMITIVE_TYPES.contains(possiblyPrimitiveType);
    }

    @Override
    public String wrapIntoList(MappingContext mappingContext, String type, boolean mandatory) {
        return getGenericsString(mappingContext, JAVA_UTIL_LIST, type);
    }

    @Override
    public String wrapSuperTypeIntoList(MappingContext mappingContext, String type, boolean mandatory) {
        return getGenericsString(mappingContext, JAVA_UTIL_LIST, "? extends " + type);
    }

    /**
     * Wrap return type of the API interface with generics and/or Optional and/or apiReturnType
     * (as specified in the mapping configuration)
     *
     * @param mappingContext  Global mapping context
     * @param namedDefinition Named definition
     * @param parentTypeName  Name of the parent type
     * @return API interface name
     */
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

        if (Boolean.TRUE.equals(mappingContext.getUseOptionalForNullableReturnTypes())
            && !namedDefinition.isMandatory()
            && !computedTypeName.startsWith(JAVA_UTIL_LIST)) {
            computedTypeName = getGenericsString(mappingContext, JAVA_UTIL_OPTIONAL, computedTypeName);
        }
        if (computedTypeName.startsWith(JAVA_UTIL_LIST) &&
            Utils.isNotBlank(mappingContext.getApiReturnListType())) {
            // in case it is query/mutation, return type is list and apiReturnListType is set
            if (mappingContext.getApiReturnListType().contains(MappingConfigConstants.API_RETURN_NAME_PLACEHOLDER)) {
                Matcher matcher = JAVA_UTIL_LIST_ELEMENT_REGEX.matcher(computedTypeName);
                if (matcher.find()) {
                    String listElement = matcher.group(1);
                    return mappingContext.getApiReturnListType().replace(
                            MappingConfigConstants.API_RETURN_NAME_PLACEHOLDER,
                            listElement);
                } else {
                    throw new IllegalStateException();
                }
            } else {
                return computedTypeName.replace(JAVA_UTIL_LIST, mappingContext.getApiReturnListType());
            }
        }
        if (Utils.isNotBlank(mappingContext.getApiReturnType())) {
            // in case it is query/mutation and apiReturnType is set
            if (mappingContext.getApiReturnType().contains(MappingConfigConstants.API_RETURN_NAME_PLACEHOLDER)) {
                return mappingContext.getApiReturnType()
                        .replace(MappingConfigConstants.API_RETURN_NAME_PLACEHOLDER, computedTypeName);
            } else {
                return getGenericsString(mappingContext, mappingContext.getApiReturnType(), computedTypeName);
            }
        }
        return getTypeConsideringPrimitive(mappingContext, namedDefinition, computedTypeName);
    }

    @Override
    public boolean isPrimitive(String possiblyPrimitiveType) {
        return isJavaPrimitive(possiblyPrimitiveType);
    }

    @Override
    public NamedDefinition getLanguageType(MappingContext mappingContext, String graphQLType, String name,
                                           String parentTypeName, boolean mandatory, boolean collection) {
        Map<String, String> customTypesMapping = mappingContext.getCustomTypesMapping();
        Set<String> serializeFieldsUsingObjectMapper = mappingContext.getUseObjectMapperForRequestSerialization();
        String langTypeName;
        boolean primitiveCanBeUsed = !collection;
        boolean serializeUsingObjectMapper = false;
        if (name != null && parentTypeName != null && customTypesMapping.containsKey(parentTypeName + "." + name)) {
            langTypeName = customTypesMapping.get(parentTypeName + "." + name);
            primitiveCanBeUsed = false;
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

        return new NamedDefinition(langTypeName, graphQLType, isInterfaceOrUnion(mappingContext, graphQLType),
                mandatory, primitiveCanBeUsed, serializeUsingObjectMapper);
    }

    @Override
    public String wrapApiInputTypeIfRequired(MappingContext mappingContext, NamedDefinition namedDefinition,
                                             String parentTypeName) {
        String computedTypeName = namedDefinition.getJavaName();
        if (Boolean.TRUE.equals(mappingContext.getUseWrapperForNullableInputTypes()) &&
            mappingContext.getInputsName().contains(parentTypeName) &&
            !namedDefinition.isMandatory() && !computedTypeName.startsWith(JAVA_UTIL_LIST)) {
            return getGenericsString(mappingContext, INPUT_WRAPPER_CLASS, computedTypeName);
        }

        return getTypeConsideringPrimitive(mappingContext, namedDefinition, computedTypeName);
    }

    @Override
    public String wrapApiDefaultValueIfRequired(MappingContext mappingContext, NamedDefinition namedDefinition,
                                                InputValueDefinition inputValueDefinition, String defaultValue,
                                                String parentTypeName) {
        if (Boolean.TRUE.equals(mappingContext.getUseWrapperForNullableInputTypes()) &&
            mappingContext.getInputsName().contains(parentTypeName) &&
            !namedDefinition.isMandatory() && !namedDefinition.getJavaName().startsWith(JAVA_UTIL_LIST)) {
            if (defaultValue == null) {
                return INPUT_WRAPPER_CLASS + ".undefined()";
            } else if (inputValueDefinition.getDefaultValue() instanceof NullValue) {
                return INPUT_WRAPPER_CLASS + ".withNull()";
            } else {
                return INPUT_WRAPPER_CLASS + ".withValue(" + defaultValue + ")";
            }
        } else {
            return defaultValue;
        }
    }

}