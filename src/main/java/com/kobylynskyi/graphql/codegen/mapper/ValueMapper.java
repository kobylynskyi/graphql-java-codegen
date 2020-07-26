package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import graphql.language.ArrayValue;
import graphql.language.BooleanValue;
import graphql.language.EnumValue;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.NullValue;
import graphql.language.ObjectValue;
import graphql.language.StringValue;
import graphql.language.Type;
import graphql.language.TypeName;
import graphql.language.Value;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ValueMapper {

    private static final String NULL_STRING = "null";

    private ValueMapper() {
    }

    public static String map(MappingContext mappingContext, Value<?> value, Type<?> graphQLType) {
        return map(mappingContext, value, graphQLType, null);
    }

    public static String map(MappingContext mappingContext, Value<?> value, Type<?> graphQLType,
                             String formatter) {
        if (value instanceof NullValue) {
            return ValueFormatter.format(NULL_STRING, formatter);
        }
        if (value instanceof BooleanValue) {
            return ValueFormatter.format(mapBoolean((BooleanValue) value), formatter);
        }
        if (value instanceof IntValue) {
            return ValueFormatter.format(mapInt((IntValue) value), formatter);
        }
        if (value instanceof FloatValue) {
            return ValueFormatter.format(mapFloat((FloatValue) value), formatter);
        }
        if (value instanceof StringValue) {
            return ValueFormatter.format(mapString((StringValue) value), formatter);
        }
        if (value instanceof EnumValue) {
            return ValueFormatter.format(mapEnum(mappingContext, (EnumValue) value, graphQLType), formatter);
        }
        if (value instanceof ObjectValue) {
            // default object values are not supported yet, same behaviour as before for those
            return null;
        }
        if (value instanceof ArrayValue) {
            return mapArray(mappingContext, (ArrayValue) value, graphQLType, formatter);
        }
        // no value, or not a known type
        return null;
    }

    private static String mapBoolean(BooleanValue value) {
        return value.isValue() ? "true" : "false";
    }

    private static String mapInt(IntValue value) {
        return String.valueOf(value.getValue());
    }

    private static String mapFloat(FloatValue value) {
        return String.valueOf(value.getValue());
    }

    private static String mapString(StringValue value) {
        return "\"" + value.getValue() + "\"";
    }

    private static String mapEnum(MappingContext mappingContext, EnumValue value, Type<?> graphQLType) {
        if (graphQLType instanceof TypeName) {
            String typeName = ((TypeName) graphQLType).getName();
            typeName = MapperUtils.getModelClassNameWithPrefixAndSuffix(mappingContext, typeName);
            return typeName + "." + MapperUtils.capitalizeIfRestricted(value.getName());
        }
        if (graphQLType instanceof NonNullType) {
            return mapEnum(mappingContext, value, ((NonNullType) graphQLType).getType());
        }
        throw new IllegalArgumentException("Unexpected Enum value for list type");
    }

    @SuppressWarnings({"rawtypes", "java:S3740"})
    private static String mapArray(MappingContext mappingContext, ArrayValue value, Type<?> graphQLType,
                                   String formatter) {
        if (graphQLType == null || graphQLType instanceof ListType) {
            List<Value> values = value.getValues();
            if (values.isEmpty()) {
                return ValueFormatter.formatList(Collections.emptyList(), formatter);
            }
            Type<?> elementType = null;
            if (graphQLType != null) {
                elementType = ((ListType) graphQLType).getType();
            }
            Type<?> listElementType = elementType;
            return ValueFormatter.formatList(values.stream()
                    .map(v -> map(mappingContext, v, listElementType, formatter))
                    .collect(Collectors.toList()), formatter);
        }
        if (graphQLType instanceof NonNullType) {
            return mapArray(mappingContext, value, ((NonNullType) graphQLType).getType(), formatter);
        }
        throw new IllegalArgumentException("Unexpected array default value for non-list type");
    }

}
