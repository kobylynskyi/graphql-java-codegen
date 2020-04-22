package com.kobylynskyi.graphql.codegen.mapper;

import graphql.language.*;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultValueMapper {

    public static String map(Value<?> defaultValue, Type<?> graphQLType) {
        if (defaultValue instanceof NullValue) {
            return mapNullValue();
        }
        if (defaultValue instanceof BooleanValue) {
            return mapBoolean((BooleanValue) defaultValue);
        }
        if (defaultValue instanceof IntValue) {
            return mapInt((IntValue) defaultValue);
        }
        if (defaultValue instanceof FloatValue) {
            return mapFloat((FloatValue) defaultValue);
        }
        if (defaultValue instanceof StringValue) {
            return mapString((StringValue) defaultValue);
        }
        if (defaultValue instanceof EnumValue) {
            return mapEnum(graphQLType, (EnumValue) defaultValue);
        }
        if (defaultValue instanceof ObjectValue) {
            return mapObject((ObjectValue) defaultValue);
        }
        if (defaultValue instanceof ArrayValue) {
            return mapArray((ArrayValue) defaultValue, graphQLType);
        }
        // no default value, or not a known type
        return null;
    }

    private static String mapNullValue() {
        return "null";
    }

    private static String mapBoolean(BooleanValue defaultValue) {
        return defaultValue.isValue() ? "true" : "false";
    }

    private static String mapInt(IntValue defaultValue) {
        return String.valueOf(defaultValue.getValue());
    }

    private static String mapFloat(FloatValue defaultValue) {
        return String.valueOf(defaultValue.getValue());
    }

    private static String mapString(StringValue defaultValue) {
        return "\"" + defaultValue.getValue() + "\"";
    }

    private static String mapEnum(Type<?> graphQLType, EnumValue defaultValue) {
        if (graphQLType instanceof TypeName) {
            return ((TypeName) graphQLType).getName() + "." + defaultValue.getName();
        }
        if (graphQLType instanceof NonNullType) {
            return mapEnum(((NonNullType) graphQLType).getType(), defaultValue);
        }
        throw new IllegalArgumentException("Unexpected Enum default value for list type");
    }

    private static String mapObject(ObjectValue defaultValue) {
        // default object values are not supported yet, same behaviour as before for those
        return null;
    }

    private static String mapArray(ArrayValue defaultValue, Type<?> graphQLType) {
        if (!(graphQLType instanceof ListType)) {
            throw new IllegalArgumentException("Unexpected array default value for non-list type");
        }
        List<Value> values = defaultValue.getValues();
        if (values.isEmpty()) {
            return "Collections.emptyList()";
        }
        Type<?> elementType = ((ListType) graphQLType).getType();
        return values.stream()
                .map(v -> map(v, elementType))
                .collect(Collectors.joining(", ", "Arrays.asList(", ")"));
    }
}
