package com.kobylynskyi.graphql.codegen.java;

import com.kobylynskyi.graphql.codegen.mapper.ValueFormatter;

import java.util.List;
import java.util.StringJoiner;

/**
 * {@inheritDoc}
 */
public class JavaValueFormatter implements ValueFormatter {

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatList(List<String> values, String formatter) {
        if (values == null) {
            return ValueFormatter.format("null", formatter);
        }
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
                values.forEach(newElement -> arrayOfStringsJoiner.add(
                        ValueFormatter.format(newElement, FORMATTER_TO_STRING)));
                return arrayOfStringsJoiner.toString();
            case FORMATTER_TO_ARRAY:
            default:
                // samples: ``, `"1", "2"`
                StringJoiner listValuesJoiner = new StringJoiner(", ", "{", "}");
                values.forEach(listValuesJoiner::add);
                return listValuesJoiner.toString();
        }
    }

}
