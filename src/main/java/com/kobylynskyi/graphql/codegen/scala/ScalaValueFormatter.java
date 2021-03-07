package com.kobylynskyi.graphql.codegen.scala;

import java.util.StringJoiner;

import com.kobylynskyi.graphql.codegen.mapper.ValueFormatter;

public class ScalaValueFormatter implements ValueFormatter {

    @Override
    public String getEmptyListValue() {
        return "Seq.empty";
    }

    @Override
    public StringJoiner getListJoiner() {
        return new StringJoiner(", ", "Seq(", ")");
    }

    @Override
    public StringJoiner getArrayJoiner() {
        return new StringJoiner(", ", "Array(", ")");
    }

}
