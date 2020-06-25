package com.kobylynskyi.graphql.codegen.model;

import java.time.ZonedDateTime;

public interface DateTimeGenerator {

    default ZonedDateTime newDateTime() {
        return ZonedDateTime.now();
    }

}
