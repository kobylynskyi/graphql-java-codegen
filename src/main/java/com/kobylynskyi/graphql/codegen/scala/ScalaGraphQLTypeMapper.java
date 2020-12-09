package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLTypeMapper;
import com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper;
import com.kobylynskyi.graphql.codegen.mapper.ValueMapper;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.NamedDefinition;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Argument;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.kobylynskyi.graphql.codegen.java.JavaGraphQLTypeMapper.JAVA_UTIL_LIST;
import static java.util.Arrays.asList;

/**
 * {@inheritDoc}
 */
public class ScalaGraphQLTypeMapper implements GraphQLTypeMapper {

    private static final String SCALA_UTIL_LIST = "Seq";
    private static final String SCALA_UTIL_OPTIONAL = "Option";
    private static final Set<String> SCALA_PRIMITIVE_TYPES = new HashSet<>(asList(
            "Byte", "Short", "Int", "Long", "Float", "Double", "Char", "Boolean"));

    private final ValueMapper valueMapper;

    public ScalaGraphQLTypeMapper(ValueMapper valueMapper) {
        this.valueMapper = valueMapper;
    }

    public static boolean isScalaPrimitive(String scalaType) {
        return SCALA_PRIMITIVE_TYPES.contains(scalaType);
    }

    /**
     * Wrap Scala type into {@link List}.
     * E.g.: {@code String} becomes {@code Seq[String]} in Scala
     *
     * @param type           The name of a type that will be wrapped into {@code List<>} in Java/Kotlin or {@code Seq[]} in Scala
     * @param mappingContext Global mapping context
     * @return String The name of the given type, wrapped into {@code List<>} in Java/Kotlin or {@code Seq[]} in Scala
     */
    @Override
    public String wrapIntoList(MappingContext mappingContext, String type) {
        return getGenericsString(mappingContext, SCALA_UTIL_LIST, type);
    }

    /**
     * Return upper bounded wildcard for the given interface type:
     * {@code "Foo"} becomes {@code "Seq[_ <: Foo]"} in Scala.
     *
     * @param type           The name of a type whose upper bound wildcard will be wrapped into a list.
     * @param mappingContext Global mapping context
     * @return String The name of the the wrapped type.
     */
    @Override
    public String wrapSuperTypeIntoList(MappingContext mappingContext, String type) {
        return getGenericsString(mappingContext, SCALA_UTIL_LIST, "_ <: " + type);
    }

    /**
     * {@inheritDoc}
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

        if (Boolean.TRUE.equals(mappingContext.getUseOptionalForNullableReturnTypes()) && !namedDefinition.isMandatory()) {
            if (!computedTypeName.startsWith(SCALA_UTIL_LIST) && !computedTypeName.startsWith(JAVA_UTIL_LIST)) {
                // wrap the type into scala.Option (except java list and scala list)
                computedTypeName = getGenericsString(mappingContext, SCALA_UTIL_OPTIONAL, computedTypeName);
            }
        }

        if (computedTypeName.startsWith(SCALA_UTIL_LIST) &&
                Utils.isNotBlank(mappingContext.getApiReturnListType())) {
            // in case it is query/mutation, return type is list and apiReturnListType is set
            return computedTypeName.replace(SCALA_UTIL_LIST, mappingContext.getApiReturnListType());
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
    public boolean isPrimitive(String scalaType) {
        return isScalaPrimitive(scalaType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGenericsString(MappingContext mappingContext, String genericType, String typeParameter) {
        return String.format("%s[%s]", genericType, typeParameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String mapDirectiveArgumentValue(MappingContext mappingContext, Argument dirArg, String argumentValueFormatter) {
        return valueMapper.map(mappingContext, dirArg.getValue(), null, argumentValueFormatter);
    }

    @Override
    public boolean addModelValidationAnnotationForType(String possiblyPrimitiveType) {
        return !ScalaGraphQLTypeMapper.isScalaPrimitive(possiblyPrimitiveType);
    }

}
