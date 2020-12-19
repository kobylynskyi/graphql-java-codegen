package com.kobylynskyi.graphql.codegen.mapper;

import java.util.List;
import java.util.StringJoiner;

/**
 * As per https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.6.1 following n
 */
public interface ValueFormatter {

    String FORMATTER_TO_STRING = "?toString";
    String FORMATTER_TO_ARRAY = "?toArray";
    String FORMATTER_TO_ARRAY_OF_STRINGS = "?toArrayOfStrings";

    static String format(String value, String formatter) {
        return FORMATTER_TO_STRING.equals(formatter) ? "\"" + value + "\"" : value;
    }

    default String formatList(List<String> values, String formatter) {
        if (values == null) {
            return format(getNullValue(), formatter);
        }
        if (formatter == null) {
            if (values.isEmpty()) {
                return getEmptyListValue();
            } else {
                StringJoiner listJoiner = getListJoiner();
                values.forEach(listJoiner::add);
                return listJoiner.toString();
            }
        }
        switch (formatter) {
            case FORMATTER_TO_ARRAY_OF_STRINGS:
                StringJoiner arrayOfStringsJoiner = getArrayJoiner();
                values.forEach(newElement -> arrayOfStringsJoiner.add(
                        format(newElement, FORMATTER_TO_STRING)));
                return arrayOfStringsJoiner.toString();
            case FORMATTER_TO_ARRAY:
            default:
                StringJoiner arrayJoiner = getArrayJoiner();
                values.forEach(arrayJoiner::add);
                return arrayJoiner.toString();
        }
    }

    default String getNullValue() {
        return "null";
    }

    String getEmptyListValue();

    StringJoiner getListJoiner();

    StringJoiner getArrayJoiner();

}
