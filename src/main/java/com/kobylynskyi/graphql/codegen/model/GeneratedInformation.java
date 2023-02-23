package com.kobylynskyi.graphql.codegen.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

/**
 * A class used for building a @Generated annotation on top of each generated class
 */
public class GeneratedInformation {

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    private final String generatedType;
    private Supplier<ZonedDateTime> dateTimeSupplier;

    public GeneratedInformation(MappingConfig mappingConfig) {
        this(ZonedDateTime::now, mappingConfig);
    }

    public GeneratedInformation(Supplier<ZonedDateTime> dateTimeSupplier, MappingConfig mappingConfig) {
        this.dateTimeSupplier = dateTimeSupplier;
        this.generatedType = initGeneratedType(mappingConfig);
    }

    private static String initGeneratedType(MappingConfig mappingConfig) {
        if (mappingConfig != null &&
                mappingConfig.getGeneratedAnnotation() != null &&
                !mappingConfig.getGeneratedAnnotation().isEmpty()) {
            return mappingConfig.getGeneratedAnnotation();
        }
        // default logic if mapping config doesn't have a specific annotation
        try {
            return Class.forName("jakarta.annotation.Generated").getCanonicalName();
        } catch (ClassNotFoundException ignored0) {
            try {
                return Class.forName("javax.annotation.processing.Generated").getCanonicalName();
            } catch (ClassNotFoundException ignored1) {
                try {
                    return Class.forName("javax.annotation.Generated").getCanonicalName();
                } catch (ClassNotFoundException ignored2) {
                    // class is not available
                }
            }
        }
        return null;
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

}
