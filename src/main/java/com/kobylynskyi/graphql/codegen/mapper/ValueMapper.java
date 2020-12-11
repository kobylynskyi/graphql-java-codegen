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

    private final ValueFormatter valueFormatter;
    private final DataModelMapper dataModelMapper;

    public ValueMapper(ValueFormatter valueFormatter,
                       DataModelMapper dataModelMapper) {
        this.valueFormatter = valueFormatter;
        this.dataModelMapper = dataModelMapper;
    }

    private static String mapBoolean(BooleanValue value) {
        return value.isValue() ? "true" : "false";
    }

    //TODO It should also be abstracted. Different languages have different default values(It is now implemented in templates (templates are extremely complex))
    private static String mapInt(MappingContext mappingContext, IntValue value, Type<?> graphQLType) {
        //default java basic type is `int`. so, default value like 123 that must wrap or append suffix `L` when it be defined as `int` in graphql schema.
        //`int` cannot assign to `Long`, also `double` cannot assign to `Float`, but graphql Float default mapping is Double in java, so, not modify `mapFloat`.
        if (graphQLType instanceof TypeName) {
            String customType = mappingContext.getCustomTypesMapping().get("Long");
            String typeName = ((TypeName) graphQLType).getName();
            if ("Long".equals(typeName) && ("java.lang.Long".equals(customType) || "Long".equals(customType))) {
                return String.valueOf(value.getValue()).concat("L");
            }
        }

        if (graphQLType instanceof NonNullType) {
            // unwrapping NonNullType
            return mapInt(mappingContext, value, ((NonNullType) graphQLType).getType());
        }
        return String.valueOf(value.getValue());
    }

    private static String mapFloat(FloatValue value) {
        return String.valueOf(value.getValue());
    }

    private static String mapString(StringValue value) {
        return "\"" + value.getValue() + "\"";
    }

    public String map(MappingContext mappingContext, Value<?> value, Type<?> graphQLType) {
        return map(mappingContext, value, graphQLType, null);
    }

    public String map(MappingContext mappingContext, Value<?> value, Type<?> graphQLType,
                      String formatter) {
        if (value instanceof NullValue) {
            return ValueFormatter.format(NULL_STRING, formatter);
        }
        if (value instanceof BooleanValue) {
            return ValueFormatter.format(mapBoolean((BooleanValue) value), formatter);
        }
        if (value instanceof IntValue) {
            return ValueFormatter.format(mapInt(mappingContext, (IntValue) value, graphQLType), formatter);
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

    private String mapEnum(MappingContext mappingContext, EnumValue value, Type<?> graphQLType) {
        if (graphQLType instanceof TypeName) {
            String typeName = ((TypeName) graphQLType).getName();
            typeName = DataModelMapper.getModelClassNameWithPrefixAndSuffix(mappingContext, typeName);
            return typeName + "." + dataModelMapper.capitalizeIfRestricted(mappingContext, value.getName());
        }
        if (graphQLType instanceof NonNullType) {
            return mapEnum(mappingContext, value, ((NonNullType) graphQLType).getType());
        }
        throw new IllegalArgumentException("Unexpected Enum value for list type");
    }

    @SuppressWarnings({"rawtypes", "java:S3740"})
    private String mapArray(MappingContext mappingContext, ArrayValue value, Type<?> graphQLType,
                            String formatter) {
        if (graphQLType == null || graphQLType instanceof ListType) {
            List<Value> values = value.getValues();
            if (values.isEmpty()) {
                return valueFormatter.formatList(Collections.emptyList(), formatter);
            }
            Type<?> elementType = null;
            if (graphQLType != null) {
                elementType = ((ListType) graphQLType).getType();
            }
            Type<?> listElementType = elementType;
            return valueFormatter.formatList(values.stream()
                    .map(v -> map(mappingContext, v, listElementType, formatter))
                    .collect(Collectors.toList()), formatter);
        }
        if (graphQLType instanceof NonNullType) {
            return mapArray(mappingContext, value, ((NonNullType) graphQLType).getType(), formatter);
        }
        throw new IllegalArgumentException("Unexpected array default value for non-list type");
    }

}
