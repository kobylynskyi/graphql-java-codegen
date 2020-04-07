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

    public UpdateIssueInput(Double floatVal, Boolean booleanVal, Integer intVal, String stringVal, Status enumVal, UpdateIssueInput objectWithNullDefault, Collection<Integer> intList, Collection<Integer> intListEmptyDefault) {
        this.floatVal = floatVal;
        this.booleanVal = booleanVal;
        this.intVal = intVal;
        this.stringVal = stringVal;
        this.enumVal = enumVal;
        this.objectWithNullDefault = objectWithNullDefault;
        this.intList = intList;
        this.intListEmptyDefault = intListEmptyDefault;
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


    public static class Builder {

        private Double floatVal = 1.23;
        private Boolean booleanVal = false;
        private Integer intVal = 42;
        private String stringVal = "my-default";
        private Status enumVal = Status.OPEN;
        private UpdateIssueInput objectWithNullDefault = null;
        private Collection<Integer> intList = Arrays.asList(1, 2, 3);
        private Collection<Integer> intListEmptyDefault = Collections.emptyList();

        public Builder() {
        }

        public Builder setFloatVal(Double floatVal) {
            this.floatVal = floatVal;
            return this;
        }

        public Builder setBooleanVal(Boolean booleanVal) {
            this.booleanVal = booleanVal;
            return this;
        }

        public Builder setIntVal(Integer intVal) {
            this.intVal = intVal;
            return this;
        }

        public Builder setStringVal(String stringVal) {
            this.stringVal = stringVal;
            return this;
        }

        public Builder setEnumVal(Status enumVal) {
            this.enumVal = enumVal;
            return this;
        }

        public Builder setObjectWithNonNullDefault(UpdateIssueInput objectWithNullDefault) {
            this.objectWithNullDefault = objectWithNullDefault;
            return this;
        }

        public Builder setIntList(Collection<Integer> intList) {
            this.intList = intList;
            return this;
        }

        public Builder setIntListEmptyDefault(Collection<Integer> intListEmptyDefault) {
            this.intListEmptyDefault = intListEmptyDefault;
            return this;
        }

        public UpdateIssueInput build() {
            return new UpdateIssueInput(floatVal, booleanVal, intVal, stringVal, enumVal, objectWithNullDefault, intList, intListEmptyDefault);
        }

    }

}
