package com.kobylynskyi.graphql.codegen.model.graphql.data;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        ZonedDateTime utcDateTime = value.withZoneSameInstant(ZoneId.of("UTC"));
        gen.writeString(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(utcDateTime));
    }

    @Override
    public Class<ZonedDateTime> handledType() {
        return ZonedDateTime.class;
    }
}