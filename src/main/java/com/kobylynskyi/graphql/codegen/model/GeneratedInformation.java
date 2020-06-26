package com.kobylynskyi.graphql.codegen.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

public class GeneratedInformation {

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    private Supplier<ZonedDateTime> dateTimeSupplier;
    private final String generatedType;

    public GeneratedInformation() {
        this(ZonedDateTime::now);
    }

    public GeneratedInformation(Supplier<ZonedDateTime> dateTimeSupplier) {
        this.dateTimeSupplier = dateTimeSupplier;
        this.generatedType = initGeneratedType();
    }

    public void setDateTimeSupplier(Supplier<ZonedDateTime> dateTimeSupplier) {
        this.dateTimeSupplier = dateTimeSupplier;
    }

    public String getGeneratedType() {
        return generatedType;
    }

    public String getDateTime() {
        return DATE_TIME_FORMAT.format(dateTimeSupplier.get());
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
