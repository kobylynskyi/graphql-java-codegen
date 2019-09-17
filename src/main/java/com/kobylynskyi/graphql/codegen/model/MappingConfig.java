package com.kobylynskyi.graphql.codegen.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MappingConfig {

    /**
     * Scalars mapping can be defined here.
     * e.g.: DateTime -> String
     * e.g.: Price.amount -> java.math.BigDecimal
     */
    private Map<String, String> customTypesMapping = new HashMap<>();

    /**
     * Custom annotations for fields can be defined here.
     * e.g.: EpochMillis -> com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.EpochMillisScalarDeserializer.class)
     */
    private Map<String, String> customAnnotationsMapping = new HashMap<>();

    private boolean generateApis = true;
    private String packageName;
    private String apiPackageName;
    private String modelPackageName;
    private String modelNamePrefix;
    private String modelNameSuffix;

    public void putCustomTypeMappingIfAbsent(String from, String to) {
        if (customTypesMapping == null) {
            customTypesMapping = new HashMap<>();
        }
        if (!customTypesMapping.containsKey(from)) {
            customTypesMapping.put(from, to);
        }
    }

}
