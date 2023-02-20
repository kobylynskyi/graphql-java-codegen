package com.kobylynskyi.graphql.codegen.model.graphql;

import com.kobylynskyi.graphql.codegen.model.graphql.data.EventPropertyChildParametrizedInput;
import com.kobylynskyi.graphql.codegen.model.graphql.data.EventPropertyResponseProjection;
import com.kobylynskyi.graphql.codegen.model.graphql.data.EventResponseProjection;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class GraphQLResponseProjectionTest {

    @Test
    public void deepCopy_empty() {
        EventResponseProjection original = new EventResponseProjection();
        EventResponseProjection deepCopy = original.deepCopy$();
        assertTrue(deepCopy.fields.isEmpty());
    }

    @Test
    public void deepCopy_null() {
        assertTrue(new EventResponseProjection((EventResponseProjection) null).fields.isEmpty());
    }

    @Test
    public void deepCopy_null_list() {
        assertTrue(new EventResponseProjection((List<EventResponseProjection>) null).fields.isEmpty());
    }

    @Test
    public void deepCopy_empty_list() {
        assertTrue(new EventResponseProjection(new ArrayList<>()).fields.isEmpty());
    }

    @Test
    public void deepCopy_list_with_null_element() {
        assertTrue(new EventResponseProjection(new ArrayList<>(singletonList(null))).fields.isEmpty());
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

        // check that original and deepcopy are not modified

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
        projection1.rating();
        projection1.properties(new EventPropertyResponseProjection()
                .intVal()
                .stringVal()
                .child("child12", new EventPropertyChildParametrizedInput(1, 2),
                        new EventPropertyResponseProjection()
                                .booleanVal()));

        EventResponseProjection projection2 = new EventResponseProjection();
        projection2.id("uid");
        projection2.status();
        projection2.active();
        projection2.properties(new EventPropertyResponseProjection()
                .floatVal()
                .child("child34", new EventPropertyChildParametrizedInput(3, 4),
                        new EventPropertyResponseProjection()
                                .intVal()));

        EventResponseProjection projection12 = new EventResponseProjection(asList(projection1, projection2));
        assertEquals("{ id state : status rating properties { intVal stringVal child12 : child (first: 1, last: 2) { booleanVal } floatVal child34 : child (first: 3, last: 4) { intVal } } uid : id status active }",
                projection12.toString());
    }

    @Test
    public void join_same_aliases_different_inputs() {
        EventResponseProjection projection1 = new EventResponseProjection()
                .properties(new EventPropertyResponseProjection()
                        .child(new EventPropertyChildParametrizedInput(1, 2),
                                new EventPropertyResponseProjection().intVal()));

        EventResponseProjection projection2 = new EventResponseProjection()
                .properties(new EventPropertyResponseProjection()
                        .child(new EventPropertyChildParametrizedInput(3, 4),
                                new EventPropertyResponseProjection().intVal()));

        try {
            new EventResponseProjection(asList(projection1, projection2));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Field 'child' has an argument conflict", e.getMessage());
        }
    }

    @Test
    public void join_same_aliases_different_inputs2() {
        EventResponseProjection projection1 = new EventResponseProjection()
                .properties(new EventPropertyResponseProjection()
                        .child("children", new EventPropertyChildParametrizedInput(1, 2),
                                new EventPropertyResponseProjection().intVal()));

        EventResponseProjection projection2 = new EventResponseProjection()
                .properties(new EventPropertyResponseProjection()
                        .child("children", new EventPropertyChildParametrizedInput(3, 4),
                                new EventPropertyResponseProjection().intVal()));

        try {
            new EventResponseProjection(asList(projection1, projection2));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Field 'child' has an argument conflict", e.getMessage());
        }
    }

    @Test
    public void join_same_aliases_different_inputs3() {
        EventResponseProjection projection1 = new EventResponseProjection()
                .properties(new EventPropertyResponseProjection()
                        .child("children", new EventPropertyChildParametrizedInput(1, 2),
                                null));

        EventResponseProjection projection2 = new EventResponseProjection()
                .properties(new EventPropertyResponseProjection()
                        .child("children", new EventPropertyChildParametrizedInput(3, 4),
                                new EventPropertyResponseProjection().intVal()));

        try {
            new EventResponseProjection(asList(projection1, projection2));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Field 'child' has an argument conflict", e.getMessage());
        }
    }

    @Test
    public void join_same_aliases_different_inputs4() {
        EventResponseProjection projection1 = new EventResponseProjection()
                .properties(new EventPropertyResponseProjection()
                        .child("children", new EventPropertyChildParametrizedInput(1, 2),
                                new EventPropertyResponseProjection().intVal()));

        EventResponseProjection projection2 = new EventResponseProjection()
                .properties(new EventPropertyResponseProjection()
                        .child("children", new EventPropertyChildParametrizedInput(3, 4),
                                null));

        try {
            new EventResponseProjection(asList(projection1, projection2));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Field 'child' has an argument conflict", e.getMessage());
        }
    }

    @Test
    public void join_null_values() {
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

        EventResponseProjection projection123 = new EventResponseProjection(asList(projection1, projection2, projection3));
        assertEquals("{ id properties { child { child } } }",
                projection123.toString());
    }

}