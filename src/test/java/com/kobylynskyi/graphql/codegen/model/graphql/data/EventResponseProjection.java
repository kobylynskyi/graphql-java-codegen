package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

import java.util.List;

/**
 * Response projection for Event
 */
public class EventResponseProjection extends GraphQLResponseProjection {

    public EventResponseProjection() {
    }

    public EventResponseProjection(EventResponseProjection projection) {
        super(projection);
    }

    public EventResponseProjection(List<EventResponseProjection> projections) {
        super(projections);
    }

    public EventResponseProjection id() {
        return id(null);
    }

    public EventResponseProjection id(String alias) {
        add$(new GraphQLResponseField("id").alias(alias));
        return this;
    }

    public EventResponseProjection categoryId() {
        return categoryId(null);
    }

    public EventResponseProjection categoryId(String alias) {
        add$(new GraphQLResponseField("categoryId").alias(alias));
        return this;
    }

    public EventResponseProjection properties(EventPropertyResponseProjection subProjection) {
        return properties(null, subProjection);
    }

    public EventResponseProjection properties(String alias, EventPropertyResponseProjection subProjection) {
        add$(new GraphQLResponseField("properties").alias(alias).projection(subProjection));
        return this;
    }

    public EventResponseProjection status() {
        return status(null);
    }

    public EventResponseProjection status(String alias) {
        add$(new GraphQLResponseField("status").alias(alias));
        return this;
    }

    public EventResponseProjection createdBy() {
        return createdBy(null);
    }

    public EventResponseProjection createdBy(String alias) {
        add$(new GraphQLResponseField("createdBy").alias(alias));
        return this;
    }

    public EventResponseProjection createdDateTime() {
        return createdDateTime(null);
    }

    public EventResponseProjection createdDateTime(String alias) {
        add$(new GraphQLResponseField("createdDateTime").alias(alias));
        return this;
    }

    public EventResponseProjection active() {
        return active(null);
    }

    public EventResponseProjection active(String alias) {
        add$(new GraphQLResponseField("active").alias(alias));
        return this;
    }

    public EventResponseProjection rating() {
        return rating(null);
    }

    public EventResponseProjection rating(String alias) {
        add$(new GraphQLResponseField("rating").alias(alias));
        return this;
    }

    public GraphQLResponseProjection all$() {
        return null;
    }

    public GraphQLResponseProjection all$(int maxDepth) {
        return null;
    }

    @Override
    public EventResponseProjection deepCopy$() {
        return new EventResponseProjection(this);
    }
}
