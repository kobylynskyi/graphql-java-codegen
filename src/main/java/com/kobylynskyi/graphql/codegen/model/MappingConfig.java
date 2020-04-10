package com.kobylynskyi.graphql.codegen.model;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private String subscriptionReturnType;
    private Boolean generateBuilder;
    private Boolean generateEqualsAndHashCode;
    private Boolean generateToString;
    private Boolean generateAsyncApi;
    private Boolean generateParameterizedFieldsResolvers;
    private Boolean generateDataFetchingEnvironmentArgumentInApis;

    /**
     * Fields that require Resolvers should be defined here in format: TypeName.fieldName
     * E.g.: "Person.friends"
     * If just type is specified, then all fields of this type will
     */
    private Set<String> fieldsWithResolvers = new HashSet<>();

    // client-side codegen configs:
    private Boolean generateRequests;
    private String requestSuffix;
    private String responseProjectionSuffix;

    @Override
    public void combine(MappingConfig source) {
        if (source == null) {
            return;
        }
        if (this.customTypesMapping != null && source.customTypesMapping != null) {
            this.customTypesMapping.putAll(source.customTypesMapping);
        } else if (this.customTypesMapping == null) {
            this.customTypesMapping = source.customTypesMapping;
        }
        if (this.customAnnotationsMapping != null && source.customAnnotationsMapping != null) {
            this.customAnnotationsMapping.putAll(source.customAnnotationsMapping);
        } else if (this.customAnnotationsMapping == null) {
            this.customAnnotationsMapping = source.customAnnotationsMapping;
        }
        this.generateApis = source.generateApis != null ? source.generateApis : this.generateApis;
        this.packageName = source.packageName != null ? source.packageName : this.packageName;
        this.apiPackageName = source.apiPackageName != null ? source.apiPackageName : this.apiPackageName;
        this.modelPackageName = source.modelPackageName != null ? source.modelPackageName : this.modelPackageName;
        this.modelNamePrefix = source.modelNamePrefix != null ? source.modelNamePrefix : this.modelNamePrefix;
        this.modelNameSuffix = source.modelNameSuffix != null ? source.modelNameSuffix : this.modelNameSuffix;
        this.modelValidationAnnotation = source.modelValidationAnnotation != null ? source.modelValidationAnnotation : this.modelValidationAnnotation;
        this.subscriptionReturnType = source.subscriptionReturnType != null ? source.subscriptionReturnType : this.subscriptionReturnType;
        this.generateBuilder = source.generateBuilder != null ? source.generateBuilder : this.generateBuilder;
        this.generateEqualsAndHashCode = source.generateEqualsAndHashCode != null ? source.generateEqualsAndHashCode : this.generateEqualsAndHashCode;
        this.generateToString = source.generateToString != null ? source.generateToString : this.generateToString;
        this.generateAsyncApi = source.generateAsyncApi != null ? source.generateAsyncApi : this.generateAsyncApi;
        this.generateParameterizedFieldsResolvers = source.generateParameterizedFieldsResolvers != null ? source.generateParameterizedFieldsResolvers : this.generateParameterizedFieldsResolvers;
        this.generateDataFetchingEnvironmentArgumentInApis = source.generateDataFetchingEnvironmentArgumentInApis != null ? source.generateDataFetchingEnvironmentArgumentInApis : this.generateDataFetchingEnvironmentArgumentInApis;
        if (this.fieldsWithResolvers != null && source.fieldsWithResolvers != null) {
            this.fieldsWithResolvers.addAll(source.fieldsWithResolvers);
        } else if (this.fieldsWithResolvers == null) {
            this.fieldsWithResolvers = source.fieldsWithResolvers;
        }
        this.generateRequests = source.generateRequests != null ? source.generateRequests : this.generateRequests;
        this.requestSuffix = source.requestSuffix != null ? source.requestSuffix : this.requestSuffix;
        this.responseProjectionSuffix = source.responseProjectionSuffix != null ? source.responseProjectionSuffix : this.responseProjectionSuffix;
    }

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
}
