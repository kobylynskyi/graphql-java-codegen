package com.kobylynskyi.graphql.codegen.model;

import java.time.format.DateTimeFormatter;

public class GeneratedInformation {

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    private DateTimeGenerator dateTimeGenerator;
    private final String generatedType;

    public GeneratedInformation() {
        this(new DefaultDateTimeGenerator());
    }

    public GeneratedInformation(DateTimeGenerator dateTimeGenerator) {
        this.dateTimeGenerator = dateTimeGenerator;
        this.generatedType = initGeneratedType();
    }

    public void setDateTimeGenerator(DateTimeGenerator dateTimeGenerator) {
        this.dateTimeGenerator = dateTimeGenerator;
    }

    public String getGeneratedType() {
        return generatedType;
    }

    public String getDateTime() {
        return DATE_TIME_FORMAT.format(dateTimeGenerator.newDateTime());
    }

    private static String initGeneratedType() {
        try {
            return Class.forName("javax.annotation.processing.Generated").getCanonicalName();
        } catch (ClassNotFoundException ignored1) {
            try {
                return Class.forName("javax.annotation.Generated").getCanonicalName();
            } catch (ClassNotFoundException ignored2) {
                // class is not available
            }
        }
        return null;
    }

}
