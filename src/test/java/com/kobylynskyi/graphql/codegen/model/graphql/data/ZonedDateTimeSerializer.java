package com.kobylynskyi.graphql.codegen.model.graphql.data;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class ZonedDateTimeSerializer extends ValueSerializer<ZonedDateTime> {

  @Override
  public void serialize(ZonedDateTime value, JsonGenerator gen, SerializationContext ctxt)
      throws JacksonException {
    ZonedDateTime utcDateTime = value.withZoneSameInstant(ZoneId.of("UTC"));
    gen.writeString(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(utcDateTime));
  }

  @Override
  public Class<ZonedDateTime> handledType() {
    return ZonedDateTime.class;
  }
}