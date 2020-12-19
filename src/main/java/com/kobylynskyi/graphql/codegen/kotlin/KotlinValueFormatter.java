package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.mapper.ValueFormatter;

import java.util.StringJoiner;

/**
 * @author 梦境迷离
 * @since 2020/12/09
 */
public class KotlinValueFormatter implements ValueFormatter {

    @Override
    public String getNullValue() {
        return getEmptyListValue();
    }

    @Override
    public String getEmptyListValue() {
        return "emptyList()";
    }

    @Override
    public StringJoiner getListJoiner() {
        return new StringJoiner(", ", "listOf(", ")");
    }

    @Override
    public StringJoiner getArrayJoiner() {
        return new StringJoiner(", ", "arrayOf(", ")");
    }
}
