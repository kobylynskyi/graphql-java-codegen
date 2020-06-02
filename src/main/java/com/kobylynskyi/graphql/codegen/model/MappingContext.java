package com.kobylynskyi.graphql.codegen.model;

import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDocument;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class MappingContext implements GraphQLCodegenConfiguration {

    private final MappingConfig config;
    private final ExtendedDocument document;
    private final Set<String> typeNames;
    private final Set<String> interfaceNames;

    @Override
    public Map<String, String> getCustomTypesMapping() {
        return config.getCustomTypesMapping();
    }

    @Override
    public Map<String, String> getCustomAnnotationsMapping() {
        return config.getCustomAnnotationsMapping();
    }

    @Override
    public Boolean getGenerateApis() {
        return config.getGenerateApis();
    }

    @Override
    public String getPackageName() {
        return config.getPackageName();
    }

    @Override
    public String getApiPackageName() {
        return config.getApiPackageName();
    }

    @Override
    public String getModelPackageName() {
        return config.getModelPackageName();
    }

    @Override
    public String getModelNamePrefix() {
        return config.getModelNamePrefix();
    }

    @Override
    public String getModelNameSuffix() {
        return config.getModelNameSuffix();
    }

    @Override
    public String getModelValidationAnnotation() {
        return config.getModelValidationAnnotation();
    }

    @Override
    public String getSubscriptionReturnType() {
        return config.getSubscriptionReturnType();
    }

    @Override
    public Boolean getGenerateBuilder() {
        return config.getGenerateBuilder();
    }

    @Override
    public Boolean getGenerateEqualsAndHashCode() {
        return config.getGenerateEqualsAndHashCode();
    }

    @Override
    public Boolean getGenerateToString() {
        return config.getGenerateToString();
    }

    @Override
    public Boolean getGenerateAsyncApi() {
        return config.getGenerateAsyncApi();
    }

    @Override
    public Boolean getGenerateParameterizedFieldsResolvers() {
        return config.getGenerateParameterizedFieldsResolvers();
    }

    @Override
    public Boolean getGenerateExtensionFieldsResolvers() {
        return config.getGenerateExtensionFieldsResolvers();
    }

    @Override
    public Boolean getGenerateDataFetchingEnvironmentArgumentInApis() {
        return config.getGenerateDataFetchingEnvironmentArgumentInApis();
    }

    @Override
    public Set<String> getFieldsWithResolvers() {
        return config.getFieldsWithResolvers();
    }

    @Override
    public Set<String> getFieldsWithoutResolvers() {
        return config.getFieldsWithoutResolvers();
    }

    @Override
    public Boolean getGenerateClient() {
        return config.getGenerateClient();
    }

    @Override
    public String getRequestSuffix() {
        return config.getRequestSuffix();
    }

    @Override
    public String getResponseSuffix() {
        return config.getResponseSuffix();
    }

    @Override
    public String getResponseProjectionSuffix() {
        return config.getResponseProjectionSuffix();
    }

    @Override
    public String getParametrizedInputSuffix() {
        return config.getParametrizedInputSuffix();
    }

    @Override
    public String getQueryResolverParentInterface() {
        return config.getQueryResolverParentInterface();
    }

    @Override
    public String getMutationResolverParentInterface() {
        return config.getMutationResolverParentInterface();
    }

    @Override
    public String getSubscriptionResolverParentInterface() {
        return config.getSubscriptionResolverParentInterface();
    }

    @Override
    public String getResolverParentInterface() {
        return config.getResolverParentInterface();
    }

}
