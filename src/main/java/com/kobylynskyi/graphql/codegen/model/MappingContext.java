package com.kobylynskyi.graphql.codegen.model;

import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDocument;

import java.util.Map;
import java.util.Set;

public class MappingContext implements GraphQLCodegenConfiguration {

    private final MappingConfig config;
    private final ExtendedDocument document;
    private final Set<String> typeNames;
    private final Set<String> interfaceNames;
    private final GeneratedInformation generatedInformation;

    public MappingContext(MappingConfig mappingConfig,
                          ExtendedDocument document,
                          GeneratedInformation generatedInformation) {
        this.config = mappingConfig;
        this.document = document;
        this.typeNames = document.getTypeNames();
        this.interfaceNames = document.getInterfaceNames();
        this.generatedInformation = generatedInformation;
    }

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
    public Boolean getGenerateModelsForRootTypes() {
        return config.getGenerateModelsForRootTypes();
    }

    @Override
    public ApiRootInterfaceStrategy getApiRootInterfaceStrategy() {
        return config.getApiRootInterfaceStrategy();
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
    public ApiNamePrefixStrategy getApiNamePrefixStrategy() {
        return config.getApiNamePrefixStrategy();
    }

    @Override
    public String getApiNamePrefix() {
        return config.getApiNamePrefix();
    }

    @Override
    public String getApiNameSuffix() {
        return config.getApiNameSuffix();
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
    public Boolean getGenerateImmutableModels() {
        return config.getGenerateImmutableModels();
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
    public String getTypeResolverPrefix() {
        return config.getTypeResolverPrefix();
    }

    @Override
    public String getTypeResolverSuffix() {
        return config.getTypeResolverSuffix();
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

    public ExtendedDocument getDocument() {
        return document;
    }

    public Set<String> getTypeNames() {
        return typeNames;
    }

    public Set<String> getInterfaceNames() {
        return interfaceNames;
    }

    public GeneratedInformation getGeneratedInformation() {
        return generatedInformation;
    }
}
