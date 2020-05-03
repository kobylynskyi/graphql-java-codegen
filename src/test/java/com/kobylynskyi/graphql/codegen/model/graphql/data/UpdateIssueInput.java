package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.StringJoiner;

public class UpdateIssueInput {

    private Double floatVal = 1.23;
    private Boolean booleanVal = false;
    private Integer intVal = 42;
    private String stringVal = "default \" \\ \b \f \n \r \t \u1234 ";
    private Status enumVal = Status.OPEN;
    private UpdateIssueInput objectWithNullDefault = null;
    private Collection<Integer> intList = Arrays.asList(1, 2, 3);
    private Collection<String> stringListEmptyDefault = Collections.emptyList();

    public UpdateIssueInput() {
    }

    public UpdateIssueInput(Double floatVal, Boolean booleanVal, Integer intVal, String stringVal, Status enumVal,
                            UpdateIssueInput objectWithNullDefault, Collection<Integer> intList, Collection<String> stringListEmptyDefault) {
        this.floatVal = floatVal;
        this.booleanVal = booleanVal;
        this.intVal = intVal;
        this.stringVal = stringVal;
        this.enumVal = enumVal;
        this.objectWithNullDefault = objectWithNullDefault;
        this.intList = intList;
        this.stringListEmptyDefault = stringListEmptyDefault;
    }

    public Double getFloatVal() {
        return floatVal;
    }

    public void setFloatVal(Double floatVal) {
        this.floatVal = floatVal;
    }

    public Boolean getBooleanVal() {
        return booleanVal;
    }

    public void setBooleanVal(Boolean booleanVal) {
        this.booleanVal = booleanVal;
    }

    public Integer getIntVal() {
        return intVal;
    }

    public void setIntVal(Integer intVal) {
        this.intVal = intVal;
    }

    public String getStringVal() {
        return stringVal;
    }

    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }

    public Status getEnumVal() {
        return enumVal;
    }

    public void setEnumVal(Status enumVal) {
        this.enumVal = enumVal;
    }

    public UpdateIssueInput getObjectWithNullDefault() {
        return objectWithNullDefault;
    }

    public void setObjectWithNullDefault(UpdateIssueInput objectWithNullDefault) {
        this.objectWithNullDefault = objectWithNullDefault;
    }

    public Collection<Integer> getIntList() {
        return intList;
    }

    public void setIntList(Collection<Integer> intList) {
        this.intList = intList;
    }

    public Collection<String> getStringListEmptyDefault() {
        return stringListEmptyDefault;
    }

    public void setStringListEmptyDefault(Collection<String> stringListEmptyDefault) {
        this.stringListEmptyDefault = stringListEmptyDefault;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
        if (floatVal != null) {
            joiner.add("floatVal: " + GraphQLRequestSerializer.getEntry(floatVal));
        }
        if (booleanVal != null) {
            joiner.add("booleanVal: " + GraphQLRequestSerializer.getEntry(booleanVal));
        }
        if (intVal != null) {
            joiner.add("intVal: " + GraphQLRequestSerializer.getEntry(intVal));
        }
        if (stringVal != null) {
            joiner.add("stringVal: " + GraphQLRequestSerializer.getEntry(stringVal));
        }
        if (enumVal != null) {
            joiner.add("enumVal: " + GraphQLRequestSerializer.getEntry(enumVal));
        }
        if (objectWithNullDefault != null) {
            joiner.add("objectWithNullDefault: " + GraphQLRequestSerializer.getEntry(objectWithNullDefault));
        }
        if (intList != null) {
            joiner.add("intList: " + GraphQLRequestSerializer.getEntry(intList));
        }
        if (stringListEmptyDefault != null) {
            joiner.add("intListEmptyDefault: " + GraphQLRequestSerializer.getEntry(stringListEmptyDefault));
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
        private Collection<String> stringListEmptyDefault = Collections.emptyList();

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

        public Builder setStringListEmptyDefault(Collection<String> stringListEmptyDefault) {
            this.stringListEmptyDefault = stringListEmptyDefault;
            return this;
        }

        public UpdateIssueInput build() {
            return new UpdateIssueInput(floatVal, booleanVal, intVal, stringVal, enumVal, objectWithNullDefault, intList, stringListEmptyDefault);
        }

    }

}
