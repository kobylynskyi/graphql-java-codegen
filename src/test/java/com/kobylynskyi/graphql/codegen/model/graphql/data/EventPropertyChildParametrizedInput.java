package com.kobylynskyi.graphql.codegen.model.graphql.data;

import java.util.Objects;
import java.util.StringJoiner;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer;

/**
 * Parametrized input for field child in type EventProperty
 */
public class EventPropertyChildParametrizedInput implements GraphQLParametrizedInput {

    private Integer first;
    private Integer last;

    public EventPropertyChildParametrizedInput() {
    }

    public EventPropertyChildParametrizedInput(Integer first, Integer last) {
        this.first = first;
        this.last = last;
    }

    public EventPropertyChildParametrizedInput first(Integer first) {
        this.first = first;
        return this;
    }

    public EventPropertyChildParametrizedInput last(Integer last) {
        this.last = last;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final EventPropertyChildParametrizedInput that = (EventPropertyChildParametrizedInput) obj;
        return Objects.equals(first, that.first)
               && Objects.equals(last, that.last);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, last);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        if (first != null) {
            joiner.add("first: " + GraphQLRequestSerializer.getEntry(first));
        }
        if (last != null) {
            joiner.add("last: " + GraphQLRequestSerializer.getEntry(last));
        }
        return joiner.toString();
    }

}