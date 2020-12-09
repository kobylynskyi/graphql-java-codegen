package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.mapper.ValueFormatter;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author 梦境迷离
 * @since 2020/12/09
 */
public class KotlinValueFormatter implements ValueFormatter {

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatList(List<String> values, String formatter) {
        // TODO null can not assign to List<T>
        if (formatter == null) {
            if (values.isEmpty()) {
                return "emptyList()";
            } else {
                StringJoiner listJoiner = new StringJoiner(", ", "listOf(", ")");
                values.forEach(listJoiner::add);
                return listJoiner.toString();
            }
        }
        switch (formatter) {
            case FORMATTER_TO_ARRAY_OF_STRINGS:
                StringJoiner arrayOfStringsJoiner = new StringJoiner(", ", "arrayOf(", ")");
                values.forEach(newElement -> arrayOfStringsJoiner.add(ValueFormatter.format(newElement, FORMATTER_TO_STRING)));
                return arrayOfStringsJoiner.toString();
            case FORMATTER_TO_ARRAY:
            default:
                StringJoiner listValuesJoiner = new StringJoiner(", ", "arrayOf(", ")");
                values.forEach(listValuesJoiner::add);
                return listValuesJoiner.toString();
        }
    }
}
