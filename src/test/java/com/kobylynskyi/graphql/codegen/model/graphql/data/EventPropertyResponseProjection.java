package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Response projection for EventProperty
 */
public class EventPropertyResponseProjection extends GraphQLResponseProjection {

    private final Map<String, Integer> projectionDepthOnFields = new HashMap<>();

    public EventPropertyResponseProjection() {
        super();
    }

    public EventPropertyResponseProjection(EventPropertyResponseProjection projection) {
        super(projection);
    }

    public EventPropertyResponseProjection(List<EventPropertyResponseProjection> projections) {
        super(projections);
    }

    public EventPropertyResponseProjection floatVal() {
        return floatVal(null);
    }

    public EventPropertyResponseProjection floatVal(String alias) {
        add$(new GraphQLResponseField("floatVal").alias(alias));
        return this;
    }

    public EventPropertyResponseProjection booleanVal() {
        return booleanVal(null);
    }

    public EventPropertyResponseProjection booleanVal(String alias) {
        add$(new GraphQLResponseField("booleanVal").alias(alias));
        return this;
    }

    public EventPropertyResponseProjection intVal() {
        return intVal(null);
    }

    public EventPropertyResponseProjection intVal(String alias) {
        add$(new GraphQLResponseField("intVal").alias(alias));
        return this;
    }

    public EventPropertyResponseProjection stringVal() {
        return stringVal(null);
    }

    public EventPropertyResponseProjection stringVal(String alias) {
        add$(new GraphQLResponseField("stringVal").alias(alias));
        return this;
    }

    public EventPropertyResponseProjection child(EventPropertyResponseProjection subProjection) {
        return child((String) null, subProjection);
    }

    public EventPropertyResponseProjection child(String alias, EventPropertyResponseProjection subProjection) {
        add$(new GraphQLResponseField("child").alias(alias).projection(subProjection));
        return this;
    }

    public EventPropertyResponseProjection child(EventPropertyChildParametrizedInput input,
                                                 EventPropertyResponseProjection subProjection) {
        return child(null, input, subProjection);
    }

    public EventPropertyResponseProjection child(String alias, EventPropertyChildParametrizedInput input,
                                                 EventPropertyResponseProjection subProjection) {
        add$(new GraphQLResponseField("child").alias(alias).parameters(input).projection(subProjection));
        return this;
    }

    public EventPropertyResponseProjection parent(EventResponseProjection subProjection) {
        return parent((String) null, subProjection);
    }

    public EventPropertyResponseProjection parent(String alias, EventResponseProjection subProjection) {
        add$(new GraphQLResponseField("parent").alias(alias).projection(subProjection));
        return this;
    }

    public EventPropertyResponseProjection parent(EventPropertyParentParametrizedInput input,
                                                  EventResponseProjection subProjection) {
        return parent(null, input, subProjection);
    }

    public EventPropertyResponseProjection parent(String alias, EventPropertyParentParametrizedInput input,
                                                  EventResponseProjection subProjection) {
        add$(new GraphQLResponseField("parent").alias(alias).parameters(input).projection(subProjection));
        return this;
    }

    public GraphQLResponseProjection all$() {
        return null;
    }

    public GraphQLResponseProjection all$(int maxDepth) {
        return null;
    }

    @Override
    public GraphQLResponseProjection deepCopy$() {
        return new EventPropertyResponseProjection(this);
    }
}
