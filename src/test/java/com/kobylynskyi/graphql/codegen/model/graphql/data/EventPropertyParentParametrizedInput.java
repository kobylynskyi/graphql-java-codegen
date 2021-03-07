package com.kobylynskyi.graphql.codegen.model.graphql.data;

import java.util.Objects;
import java.util.StringJoiner;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer;

/**
 * Parametrized input for field parent in type EventProperty
 */
public class EventPropertyParentParametrizedInput implements GraphQLParametrizedInput {

    private Status withStatus;
    private java.time.ZonedDateTime createdAfter;

    public EventPropertyParentParametrizedInput() {
    }

    public EventPropertyParentParametrizedInput(Status withStatus, java.time.ZonedDateTime createdAfter) {
        this.withStatus = withStatus;
        this.createdAfter = createdAfter;
    }

    public EventPropertyParentParametrizedInput withStatus(Status withStatus) {
        this.withStatus = withStatus;
        return this;
    }

    public EventPropertyParentParametrizedInput createdAfter(java.time.ZonedDateTime createdAfter) {
        this.createdAfter = createdAfter;
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
        return Objects.equals(withStatus, that.withStatus)
               && Objects.equals(createdAfter, that.createdAfter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(withStatus, createdAfter);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        if (withStatus != null) {
            joiner.add("withStatus: " + GraphQLRequestSerializer.getEntry(withStatus));
        }
        if (createdAfter != null) {
            joiner.add("createdAfter: " + GraphQLRequestSerializer.getEntry(createdAfter, true));
        }
        return joiner.toString();
    }

}
