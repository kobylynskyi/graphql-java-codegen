package com.kobylynskyi.graphql.codegen.java;

import java.util.StringJoiner;

import com.kobylynskyi.graphql.codegen.mapper.ValueFormatter;

public class JavaValueFormatter implements ValueFormatter {

    @Override
    public String getEmptyListValue() {
        return "java.util.Collections.emptyList()";
    }

    @Override
    public StringJoiner getListJoiner() {
        return new StringJoiner(", ", "java.util.Arrays.asList(", ")");
    }

    @Override
    public StringJoiner getArrayJoiner() {
        return new StringJoiner(", ", "{", "}");
    }

}
