package com.kobylynskyi.graphql.codegen.mapper;

import java.util.List;

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

    String formatList(List<String> values, String formatter);

}
