package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper;
import com.kobylynskyi.graphql.codegen.model.MappingConfigConstants;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.NamedDefinition;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kobylynskyi.graphql.codegen.java.JavaGraphQLTypeMapper.JAVA_UTIL_LIST;
import static java.util.Arrays.asList;

/**
 * Mapper class for converting GraphQL types to Scala types
 */
public class ScalaGraphQLTypeMapper extends GraphQLTypeMapper {

    private static final String SCALA_UTIL_LIST = "scala.Seq";
    private static final Pattern SCALA_UTIL_LIST_ELEMENT_REGEX = Pattern.compile("scala\\.Seq\\[(.+)]");
    private static final String SCALA_UTIL_OPTIONAL = "scala.Option";
    private static final Set<String> SCALA_PRIMITIVE_TYPES = new HashSet<>(asList(
            "Byte", "Short", "Int", "Long", "Float", "Double", "Char", "Boolean"));

    public static boolean isScalaPrimitive(String scalaType) {
        return SCALA_PRIMITIVE_TYPES.contains(scalaType);
    }

    public static boolean isScalaOption(String scalaType) {
        return scalaType.startsWith(SCALA_UTIL_OPTIONAL + "[") && scalaType.endsWith("]");
    }

    public static boolean isScalaCollection(String scalaType) {
        return scalaType.startsWith(SCALA_UTIL_LIST + "[") && scalaType.endsWith("]");
    }

    public static String getGenericParameter(String scalaType) {
        return scalaType.substring(SCALA_UTIL_LIST.length() + 1, scalaType.length() - 1);
    }

    @Override
    public String wrapIntoList(MappingContext mappingContext, String type, boolean mandatory) {
        return getGenericsString(mappingContext, SCALA_UTIL_LIST, type);
    }

    @Override
    public String wrapSuperTypeIntoList(MappingContext mappingContext, String type, boolean mandatory) {
        return getGenericsString(mappingContext, SCALA_UTIL_LIST, "_ <: " + type);
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

        if (Boolean.TRUE.equals(mappingContext.getUseOptionalForNullableReturnTypes())
                && !namedDefinition.isMandatory()
                && !computedTypeName.startsWith(SCALA_UTIL_LIST)
                && !computedTypeName.startsWith(JAVA_UTIL_LIST)
                && !computedTypeName.startsWith(SCALA_UTIL_OPTIONAL)) {
            // Kotlin/Scala: primitive types is Option by default
            // wrap the type into scala.Option (except java list and scala list)
            computedTypeName = getGenericsString(mappingContext, SCALA_UTIL_OPTIONAL, computedTypeName);
        }

        if (computedTypeName.startsWith(SCALA_UTIL_LIST) &&
                Utils.isNotBlank(mappingContext.getApiReturnListType())) {
            // in case it is query/mutation, return type is list and apiReturnListType is set
            if (mappingContext.getApiReturnListType().contains(MappingConfigConstants.API_RETURN_NAME_PLACEHOLDER)) {
                System.out.println("computedTypeName = " + computedTypeName);
                Matcher matcher = SCALA_UTIL_LIST_ELEMENT_REGEX.matcher(computedTypeName);
                matcher.find();
                String listElement = matcher.group(1);
                return mappingContext.getApiReturnListType().replace(
                        MappingConfigConstants.API_RETURN_NAME_PLACEHOLDER,
                        listElement);
            } else {
                return computedTypeName.replace(SCALA_UTIL_LIST, mappingContext.getApiReturnListType());
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
    public boolean isPrimitive(String scalaType) {
        return isScalaPrimitive(scalaType);
    }

    @Override
    public String getGenericsString(MappingContext mappingContext, String genericType, String typeParameter) {
        if (genericType.contains("%s")) {
            return String.format(genericType, typeParameter);
        } else {
            return String.format("%s[%s]", genericType, typeParameter);
        }
    }

}
