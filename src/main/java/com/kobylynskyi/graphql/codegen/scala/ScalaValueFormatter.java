package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.mapper.ValueFormatter;

import java.util.StringJoiner;

/**
 * Class contains various formatting logic that is specific only for Scala language
 */
public class ScalaValueFormatter implements ValueFormatter {

    @Override
    public String getEmptyListValue() {
        return "scala.Seq.empty";
    }

    @Override
    public StringJoiner getListJoiner() {
        return new StringJoiner(", ", "scala.Seq(", ")");
    }

    @Override
    public StringJoiner getArrayJoiner() {
        return new StringJoiner(", ", "scala.Array(", ")");
    }

}
