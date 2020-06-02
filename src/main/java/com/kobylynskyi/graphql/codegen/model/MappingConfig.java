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
public class MappingConfig implements GraphQLCodegenConfiguration, Combinable<MappingConfig> {

    private Map<String, String> customTypesMapping = new HashMap<>();
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
    private Boolean generateExtensionFieldsResolvers;
    private Boolean generateDataFetchingEnvironmentArgumentInApis;
    private Set<String> fieldsWithResolvers = new HashSet<>();
    private Set<String> fieldsWithoutResolvers = new HashSet<>();
    private String queryResolverParentInterface;
    private String mutationResolverParentInterface;
    private String subscriptionResolverParentInterface;
    private String resolverParentInterface;

    // client-side codegen configs:
    private Boolean generateClient;
    private String requestSuffix;
    private String responseSuffix;
    private String responseProjectionSuffix;
    private String parametrizedInputSuffix;

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
        this.generateExtensionFieldsResolvers = source.generateExtensionFieldsResolvers != null ? source.generateExtensionFieldsResolvers : this.generateExtensionFieldsResolvers;
        this.generateDataFetchingEnvironmentArgumentInApis = source.generateDataFetchingEnvironmentArgumentInApis != null ? source.generateDataFetchingEnvironmentArgumentInApis : this.generateDataFetchingEnvironmentArgumentInApis;
        if (this.fieldsWithResolvers != null && source.fieldsWithResolvers != null) {
            this.fieldsWithResolvers.addAll(source.fieldsWithResolvers);
        } else if (this.fieldsWithResolvers == null) {
            this.fieldsWithResolvers = source.fieldsWithResolvers;
        }
        if (this.fieldsWithoutResolvers != null && source.fieldsWithoutResolvers != null) {
            this.fieldsWithoutResolvers.addAll(source.fieldsWithoutResolvers);
        } else if (this.fieldsWithoutResolvers == null) {
            this.fieldsWithoutResolvers = source.fieldsWithoutResolvers;
        }
        this.queryResolverParentInterface = source.queryResolverParentInterface != null ? source.queryResolverParentInterface : this.queryResolverParentInterface;
        this.mutationResolverParentInterface = source.mutationResolverParentInterface != null ? source.mutationResolverParentInterface : this.mutationResolverParentInterface;
        this.subscriptionResolverParentInterface = source.subscriptionResolverParentInterface != null ? source.subscriptionResolverParentInterface : this.subscriptionResolverParentInterface;
        this.resolverParentInterface = source.resolverParentInterface != null ? source.resolverParentInterface : this.resolverParentInterface;
        this.generateClient = source.generateClient != null ? source.generateClient : this.generateClient;
        this.requestSuffix = source.requestSuffix != null ? source.requestSuffix : this.requestSuffix;
        this.responseSuffix = source.responseSuffix != null ? source.responseSuffix : this.responseSuffix;
        this.responseProjectionSuffix = source.responseProjectionSuffix != null ? source.responseProjectionSuffix : this.responseProjectionSuffix;
        this.parametrizedInputSuffix = source.parametrizedInputSuffix != null ? source.parametrizedInputSuffix : this.parametrizedInputSuffix;
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
