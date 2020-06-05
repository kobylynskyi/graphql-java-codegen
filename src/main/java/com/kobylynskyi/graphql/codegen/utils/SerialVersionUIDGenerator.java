package com.kobylynskyi.graphql.codegen.utils;

import java.util.concurrent.ThreadLocalRandom;

public class SerialVersionUIDGenerator {

    /**
     * Generate random long
     *
     * @return random long
     */
    public long randomLong() {
        return ThreadLocalRandom.current().nextLong();
    }

}
