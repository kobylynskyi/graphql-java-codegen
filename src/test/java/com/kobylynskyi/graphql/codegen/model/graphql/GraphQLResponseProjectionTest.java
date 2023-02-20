package com.kobylynskyi.graphql.codegen.model.graphql;

import com.kobylynskyi.graphql.codegen.model.graphql.data.EventPropertyChildParametrizedInput;
import com.kobylynskyi.graphql.codegen.model.graphql.data.EventPropertyResponseProjection;
import com.kobylynskyi.graphql.codegen.model.graphql.data.EventResponseProjection;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphQLResponseProjectionTest {

    @Test
    public void deepCopy_empty() {
        EventResponseProjection original = new EventResponseProjection();

        EventResponseProjection deepCopy = original.deepCopy$();

        assertTrue(deepCopy.fields.isEmpty());
    }

    @Test
    public void deepCopy() {
        EventResponseProjection original = new EventResponseProjection();
        original.id();
        original.status("state");
        original.properties(new EventPropertyResponseProjection()
                .intVal()
                .stringVal()
                .child(new EventPropertyChildParametrizedInput(1, 2),
                        new EventPropertyResponseProjection()
                                .booleanVal()));

        EventResponseProjection deepCopy = original.deepCopy$();

        assertEquals("{ id state : status properties { intVal stringVal child (first: 1, last: 2) { booleanVal } } }",
                original.toString());
        assertEquals("{ id state : status properties { intVal stringVal child (first: 1, last: 2) { booleanVal } } }",
                deepCopy.toString());

        original.active();
        deepCopy.rating();

        assertEquals("{ id state : status properties { intVal stringVal child (first: 1, last: 2) { booleanVal } } active }",
                original.toString());
        assertEquals("{ id state : status properties { intVal stringVal child (first: 1, last: 2) { booleanVal } } rating }",
                deepCopy.toString());
    }

    @Test
    public void join() {
        EventResponseProjection projection1 = new EventResponseProjection();
        projection1.id();
        projection1.status("state");
        projection1.properties(new EventPropertyResponseProjection()
                .intVal()
                .stringVal()
                .child(new EventPropertyChildParametrizedInput(1, 2),
                        new EventPropertyResponseProjection()
                                .booleanVal()));

        EventResponseProjection projection2 = new EventResponseProjection();
        projection2.id("uid");
        projection2.active();
        projection2.properties(new EventPropertyResponseProjection()
                .floatVal()
                .child(new EventPropertyChildParametrizedInput(3, 4),
                        new EventPropertyResponseProjection()
                                .intVal()));

        EventResponseProjection projection12 = new EventResponseProjection(Arrays.asList(projection1, projection2));
        assertEquals("{ uid : id state : status properties { intVal stringVal child (first: 3, last: 4) { booleanVal intVal } floatVal } active }",
                projection12.toString());
    }

    @Test
    public void join_safe() {
        EventResponseProjection projection1 = new EventResponseProjection();
        projection1.id(null);
        projection1.properties(null);

        EventResponseProjection projection2 = new EventResponseProjection();
        projection2.properties(new EventPropertyResponseProjection()
                .child(null));

        EventResponseProjection projection3 = new EventResponseProjection();
        projection2.properties(new EventPropertyResponseProjection()
                .child(null, null, new EventPropertyResponseProjection()
                        .child(null)));

        EventResponseProjection projection123 = new EventResponseProjection(Arrays.asList(projection1, projection2, projection3));
        assertEquals("{ id properties { child { child } } }",
                projection123.toString());
    }

}