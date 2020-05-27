package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Parametrized input for field parent in type EventProperty
 */
public class EventPropertyParentParametrizedInput implements GraphQLParametrizedInput {

    private Status withStatus;

    public EventPropertyParentParametrizedInput() {
    }

    public EventPropertyParentParametrizedInput(Status withStatus) {
        this.withStatus = withStatus;
    }

    public EventPropertyParentParametrizedInput withStatus(Status withStatus) {
        this.withStatus = withStatus;
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
        final EventPropertyParentParametrizedInput that = (EventPropertyParentParametrizedInput) obj;
        return Objects.equals(withStatus, that.withStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(withStatus);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        if (withStatus != null) {
            joiner.add("withStatus: " + GraphQLRequestSerializer.getEntry(withStatus));
        }
        return joiner.toString();
    }

}