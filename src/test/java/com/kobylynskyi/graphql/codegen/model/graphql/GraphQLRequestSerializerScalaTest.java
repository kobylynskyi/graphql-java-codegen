package com.kobylynskyi.graphql.codegen.model.graphql;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GraphQLRequestSerializerScalaTest {

    @Test
    void scalaOptionToString() {
        assertEquals("1", getEntry(new Some()));
        assertNull(getEntry(new None$()));
    }

    //Original method cannot test in Java
    public static String getEntry(Object input) {
        if (input.getClass().getSimpleName().equals("Some")) {
            return input.toString().replace("Some(", "").replace(")", "");
        } else if (input.getClass().getSimpleName().equals("None$")) {
            return null;
        }
        return input.toString();
    }

    interface Option {
    }

    static class Some implements Option {
        @Override
        public String toString() {
            return "Some(1)";
        }
    }

    static class None$ implements Option {
        @Override
        public String toString() {
            return "None";
        }
    }
}