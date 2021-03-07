package com.kobylynskyi.graphql.codegen.model.graphql.data;

import java.time.ZonedDateTime;
import java.util.StringJoiner;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer;

public class DateInput {

    private ZonedDateTime dateTime;

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
        if (dateTime != null) {
            joiner.add("dateTime: " + GraphQLRequestSerializer.getEntry(dateTime, true));
        }
        return joiner.toString();
    }

}
