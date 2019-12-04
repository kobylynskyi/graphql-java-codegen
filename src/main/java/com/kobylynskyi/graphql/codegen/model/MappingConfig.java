package com.kobylynskyi.graphql.codegen.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Mapping config.
 *
 * @author kobylynskyi
 * @author valinha
 */
@Data
public class MappingConfig implements Combinable<MappingConfig> {

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

    private Boolean generateApis;
    private String packageName;
    private String apiPackageName;
    private String modelPackageName;
    private String modelNamePrefix;
    private String modelNameSuffix;
    private String modelValidationAnnotation;
    private Boolean generateEqualsAndHashCode;

    /**
     * Put custom type mapping if absent.
     *
     * @param from the from
     * @param to   the to
     */
    public void putCustomTypeMappingIfAbsent(String from, String to) {
        if (customTypesMapping == null) {
            customTypesMapping = new HashMap<>();
        }
        if (!customTypesMapping.containsKey(from)) {
            customTypesMapping.put(from, to);
        }
    }


    @Override
    public void combine(MappingConfig source) {
        if (source != null) {
            if (this.customTypesMapping != null && source.customTypesMapping != null) {
                this.customTypesMapping.putAll(source.customTypesMapping);
            } else if (this.customTypesMapping == null && source.customTypesMapping != null) {
                this.customTypesMapping = source.customTypesMapping;
            }
            if (this.customAnnotationsMapping != null && source.customAnnotationsMapping != null) {
                this.customAnnotationsMapping.putAll(source.customAnnotationsMapping);
            } else if (this.customAnnotationsMapping == null && source.customAnnotationsMapping != null) {
                this.customAnnotationsMapping = source.customAnnotationsMapping;
            }
            this.generateApis = source.generateApis != null ? source.generateApis : this.generateApis;
            this.packageName = source.packageName != null ? source.packageName : this.packageName;
            this.apiPackageName = source.apiPackageName != null ? source.apiPackageName : this.apiPackageName;
            this.modelPackageName = source.modelPackageName != null ? source.modelPackageName : this.modelPackageName;
            this.modelNamePrefix = source.modelNamePrefix != null ? source.modelNamePrefix : this.modelNamePrefix;
            this.modelNameSuffix = source.modelNameSuffix != null ? source.modelNameSuffix : this.modelNameSuffix;
            this.modelValidationAnnotation = source.modelValidationAnnotation != null ? source.modelValidationAnnotation : this.modelValidationAnnotation;
            this.generateEqualsAndHashCode = source.generateEqualsAndHashCode != null ? source.generateEqualsAndHashCode : this.generateEqualsAndHashCode;
        }
    }
}
