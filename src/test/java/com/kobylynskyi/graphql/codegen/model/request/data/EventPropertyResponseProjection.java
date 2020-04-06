package com.kobylynskyi.graphql.codegen.model.request.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class EventPropertyResponseProjection implements com.kobylynskyi.graphql.codegen.model.request.GraphQLResponseProjection {

    private Map<String, Object> fields = new LinkedHashMap<>();

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

    public EventPropertyResponseProjection parent(EventResponseProjection subProjection) {
        fields.put("parent", subProjection);
        return this;
    }


    @Override
    public String toString() {
        if (fields.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(" ", "{ ", " }");
        for (Map.Entry<String, Object> property : fields.entrySet()) {
            joiner.add(property.getKey());
            if (property.getValue() != null) {
                joiner.add(" ").add(property.getValue().toString());
            }
        }
        return joiner.toString();
    }
}