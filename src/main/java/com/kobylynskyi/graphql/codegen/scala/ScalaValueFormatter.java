package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.mapper.ValueFormatter;

import java.util.List;
import java.util.StringJoiner;

public class ScalaValueFormatter implements ValueFormatter {

    @Override
    public String formatList(List<String> values, String formatter) {
        if (values == null) {
            return ValueFormatter.format("null", formatter);
        }
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
                values.forEach(newElement -> arrayOfStringsJoiner.add(
                        ValueFormatter.format(newElement, FORMATTER_TO_STRING)));
                return arrayOfStringsJoiner.toString();
            case FORMATTER_TO_ARRAY:
            default:
                StringJoiner listValuesJoiner = new StringJoiner(", ", "Array(", ")");
                values.forEach(listValuesJoiner::add);
                return listValuesJoiner.toString();
        }
    }

}
