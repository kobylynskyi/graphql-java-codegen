package com.kobylynskyi.graphql.codegen;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class UnknownFieldsTest {

    public static class Pojo{
        private String firstField;
        private long secondField;
        private Boolean thirdField;

        @JsonAnyGetter
        @JsonAnySetter
        private Map<String,Object> unknownFields;

        public Pojo(){
        }

        public Pojo(String firstField, long secondField, Boolean thirdField, Map<String, Object> unknownFields) {
            this.firstField = firstField;
            this.secondField = secondField;
            this.thirdField = thirdField;
            this.unknownFields = unknownFields;
        }

        public String getFirstField() {
            return firstField;
        }

        public void setFirstField(String firstField) {
            this.firstField = firstField;
        }

        public long getSecondField() {
            return secondField;
        }

        public void setSecondField(long secondField) {
            this.secondField = secondField;
        }

        public Boolean getThirdField() {
            return thirdField;
        }

        public void setThirdField(Boolean thirdField) {
            this.thirdField = thirdField;
        }

        public void add(String key, Object value) {
            if (this.unknownFields == null) {
                this.unknownFields = new HashMap<>();
            }
            this.unknownFields.put(key, value);
        }
    }

    public static void main(String[] args) throws Exception{
        Pojo pojo = new Pojo("firstField",123456l,true, null);
        pojo.add("forthField", "forthField");
        pojo.add("fifthField", 89);
        pojo.add("sixthField", true);

        ObjectMapper om = new ObjectMapper();
        String jsonString = om.writeValueAsString(pojo);
        System.out.println("-- after serialization --");
        System.out.println(jsonString);
    }
}
