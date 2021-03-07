package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer;

import java.time.ZonedDateTime;
import java.util.StringJoiner;

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
