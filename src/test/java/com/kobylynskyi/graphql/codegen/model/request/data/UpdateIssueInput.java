package com.kobylynskyi.graphql.codegen.model.request.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.StringJoiner;

public class UpdateIssueInput {

    private Double floatVal = 1.23;
    private Boolean booleanVal = false;
    private Integer intVal = 42;
    private String stringVal = "my-default";
    private Status enumVal = Status.OPEN;
    private UpdateIssueInput objectWithNullDefault = null;
    private Collection<Integer> intList = Arrays.asList(1, 2, 3);
    private Collection<Integer> intListEmptyDefault = Collections.emptyList();

    public UpdateIssueInput() {
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
        if (floatVal != null) {
            joiner.add("floatVal: " + floatVal);
        }
        if (booleanVal != null) {
            joiner.add("booleanVal: " + booleanVal);
        }
        if (intVal != null) {
            joiner.add("intVal: " + intVal);
        }
        if (stringVal != null) {
            joiner.add("stringVal: \"" + stringVal + "\"");
        }
        if (enumVal != null) {
            joiner.add("enumVal: " + enumVal);
        }
        if (objectWithNullDefault != null) {
            joiner.add("objectWithNullDefault: " + objectWithNullDefault);
        }
        if (intList != null) {
            joiner.add("intList: " + intList);
        }
        if (intListEmptyDefault != null) {
            joiner.add("intListEmptyDefault: " + intListEmptyDefault);
        }
        return joiner.toString();
    }

}
