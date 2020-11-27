package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingContext;

import java.util.List;
import java.util.StringJoiner;

/**
 * As per https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.6.1 following n
 */
public class ValueFormatter {

    public static final String FORMATTER_TO_STRING = "?toString";
    public static final String FORMATTER_TO_ARRAY = "?toArray";
    public static final String FORMATTER_TO_ARRAY_OF_STRINGS = "?toArrayOfStrings";

    private ValueFormatter() {
    }

    public static String formatList(MappingContext mappingContext, List<String> values, String formatter) {
        if (values == null) {
            return format("null", formatter);
        }
        if (mappingContext.getGeneratedLanguage() == GeneratedLanguage.SCALA) {
            return formatListScala(values, formatter);
        }
        return formatListJava(values, formatter);
    }

    private static String formatListJava(List<String> values, String formatter) {
        if (formatter == null) {
            if (values.isEmpty()) {
                return "java.util.Collections.emptyList()";
            } else {
                StringJoiner listJoiner = new StringJoiner(", ", "java.util.Arrays.asList(", ")");
                values.forEach(listJoiner::add);
                return listJoiner.toString();
            }
        }
        switch (formatter) {
            case FORMATTER_TO_ARRAY_OF_STRINGS:
                // samples: `{}`, `{"1", "2"}`
                StringJoiner arrayOfStringsJoiner = new StringJoiner(", ", "{", "}");
                values.forEach(newElement -> arrayOfStringsJoiner.add(format(newElement, FORMATTER_TO_STRING)));
                return arrayOfStringsJoiner.toString();
            case FORMATTER_TO_ARRAY:
            default:
                // samples: ``, `"1", "2"`
                StringJoiner listValuesJoiner = new StringJoiner(", ", "{", "}");
                values.forEach(listValuesJoiner::add);
                return listValuesJoiner.toString();
        }
    }

    private static String formatListScala(List<String> values, String formatter) {
        if (formatter == null) {
            if (values.isEmpty()) {
                return "Seq.empty";
            } else {
                StringJoiner listJoiner = new StringJoiner(", ", "Seq(", ")");
                values.forEach(listJoiner::add);
                return listJoiner.toString();
            }
        }
        switch (formatter) {
            case FORMATTER_TO_ARRAY_OF_STRINGS:
                StringJoiner arrayOfStringsJoiner = new StringJoiner(", ", "Array(", ")");
                values.forEach(newElement -> arrayOfStringsJoiner.add(format(newElement, FORMATTER_TO_STRING)));
                return arrayOfStringsJoiner.toString();
            case FORMATTER_TO_ARRAY:
            default:
                StringJoiner listValuesJoiner = new StringJoiner(", ", "Array(", ")");
                values.forEach(listValuesJoiner::add);
                return listValuesJoiner.toString();
        }
    }

    public static String format(String value, String formatter) {
        return FORMATTER_TO_STRING.equals(formatter) ? "\"" + value + "\"" : value;
    }

}
