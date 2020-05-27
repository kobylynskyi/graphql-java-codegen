package com.kobylynskyi.graphql.codegen.model.graphql.data;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Response projection for EventProperty
 */
public class EventPropertyResponseProjection extends GraphQLResponseProjection {

    public EventPropertyResponseProjection() {
    }

    public EventPropertyResponseProjection floatVal() {
        fields.put("floatVal", null);
        return this;
    }

    public EventPropertyResponseProjection booleanVal() {
        fields.put("booleanVal", null);
        return this;
    }

    public EventPropertyResponseProjection intVal() {
        fields.put("intVal", null);
        return this;
    }

    public EventPropertyResponseProjection stringVal() {
        fields.put("stringVal", null);
        return this;
    }

    public EventPropertyResponseProjection child(EventPropertyResponseProjection subProjection) {
        fields.put("child", subProjection);
        return this;
    }

    public EventPropertyResponseProjection child(EventPropertyChildParametrizedInput input, EventPropertyResponseProjection subProjection) {
        parametrizedInputs.put("child", input);
        return child(subProjection);
    }

    public EventPropertyResponseProjection parent(EventResponseProjection subProjection) {
        fields.put("parent", subProjection);
        return this;
    }

    public EventPropertyResponseProjection parent(EventPropertyParentParametrizedInput input, EventResponseProjection subProjection) {
        parametrizedInputs.put("parent", input);
        return parent(subProjection);
    }

}