package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

/**
 * Response projection for Event
 */
public class EventResponseProjection extends GraphQLResponseProjection {

    public EventResponseProjection() {
    }

    public EventResponseProjection id() {
        fields.put("id", null);
        return this;
    }

    public EventResponseProjection categoryId() {
        fields.put("categoryId", null);
        return this;
    }

    public EventResponseProjection properties(EventPropertyResponseProjection subProjection) {
        fields.put("properties", subProjection);
        return this;
    }

    public EventResponseProjection status() {
        fields.put("status", null);
        return this;
    }

    public EventResponseProjection createdBy() {
        fields.put("createdBy", null);
        return this;
    }

    public EventResponseProjection createdDateTime() {
        fields.put("createdDateTime", null);
        return this;
    }

    public EventResponseProjection active() {
        fields.put("active", null);
        return this;
    }

    public EventResponseProjection rating() {
        fields.put("rating", null);
        return this;
    }

}