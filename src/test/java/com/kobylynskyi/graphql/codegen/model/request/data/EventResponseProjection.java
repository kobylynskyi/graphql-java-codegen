package com.kobylynskyi.graphql.codegen.model.request.data;

import java.util.*;

public class EventResponseProjection implements com.kobylynskyi.graphql.codegen.model.request.GraphQLResponseProjection {

    private Map<String, Object> fields = new LinkedHashMap<>();

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