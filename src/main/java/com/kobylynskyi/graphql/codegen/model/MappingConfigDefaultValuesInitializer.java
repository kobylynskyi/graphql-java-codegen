package com.kobylynskyi.graphql.codegen.model;

/**
 * Initializes mapping config with default values
 */
public class MappingConfigDefaultValuesInitializer {

    private MappingConfigDefaultValuesInitializer() {
    }

    /**
     * Initializes mapping config with default values
     *
     * @param mappingConfig Global mapping config
     */
    public static void initDefaultValues(MappingConfig mappingConfig) {
        if (mappingConfig.getModelValidationAnnotation() == null) {
            mappingConfig.setModelValidationAnnotation(MappingConfigConstants.DEFAULT_VALIDATION_ANNOTATION);
        }
        if (mappingConfig.getGenerateBuilder() == null) {
            mappingConfig.setGenerateBuilder(MappingConfigConstants.DEFAULT_BUILDER);
        }
        if (mappingConfig.getGenerateEqualsAndHashCode() == null) {
            mappingConfig.setGenerateEqualsAndHashCode(MappingConfigConstants.DEFAULT_EQUALS_AND_HASHCODE);
        }
        if (mappingConfig.getGenerateClient() == null) {
            mappingConfig.setGenerateClient(MappingConfigConstants.DEFAULT_GENERATE_CLIENT);
        }
        if (mappingConfig.getRequestSuffix() == null) {
            mappingConfig.setRequestSuffix(MappingConfigConstants.DEFAULT_REQUEST_SUFFIX);
        }
        if (mappingConfig.getResponseSuffix() == null) {
            mappingConfig.setResponseSuffix(MappingConfigConstants.DEFAULT_RESPONSE_SUFFIX);
        }
        if (mappingConfig.getResponseProjectionSuffix() == null) {
            mappingConfig.setResponseProjectionSuffix(MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_SUFFIX);
        }
        if (mappingConfig.getParametrizedInputSuffix() == null) {
            mappingConfig.setParametrizedInputSuffix(MappingConfigConstants.DEFAULT_PARAMETRIZED_INPUT_SUFFIX);
        }
        if (mappingConfig.getGenerateImmutableModels() == null) {
            mappingConfig.setGenerateImmutableModels(MappingConfigConstants.DEFAULT_GENERATE_IMMUTABLE_MODELS);
        }
        if (mappingConfig.getGenerateToString() == null) {
            mappingConfig.setGenerateToString(MappingConfigConstants.DEFAULT_TO_STRING);
        }
        if (mappingConfig.getGenerateApis() == null) {
            mappingConfig.setGenerateApis(MappingConfigConstants.DEFAULT_GENERATE_APIS);
        }
        if (mappingConfig.getApiNameSuffix() == null) {
            mappingConfig.setApiNameSuffix(MappingConfigConstants.DEFAULT_RESOLVER_SUFFIX);
        }
        if (mappingConfig.getTypeResolverSuffix() == null) {
            mappingConfig.setTypeResolverSuffix(MappingConfigConstants.DEFAULT_RESOLVER_SUFFIX);
        }
        if (mappingConfig.getGenerateParameterizedFieldsResolvers() == null) {
            mappingConfig.setGenerateParameterizedFieldsResolvers(
                    MappingConfigConstants.DEFAULT_GENERATE_PARAMETERIZED_FIELDS_RESOLVERS);
        }
        if (mappingConfig.getGenerateExtensionFieldsResolvers() == null) {
            mappingConfig.setGenerateExtensionFieldsResolvers(
                    MappingConfigConstants.DEFAULT_GENERATE_EXTENSION_FIELDS_RESOLVERS);
        }
        if (mappingConfig.getGenerateDataFetchingEnvironmentArgumentInApis() == null) {
            mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(
                    MappingConfigConstants.DEFAULT_GENERATE_DATA_FETCHING_ENV);
        }
        if (mappingConfig.getGenerateModelsForRootTypes() == null) {
            mappingConfig.setGenerateModelsForRootTypes(MappingConfigConstants.DEFAULT_GENERATE_MODELS_FOR_ROOT_TYPES);
        }
        if (mappingConfig.getGenerateApisWithThrowsException() == null) {
            mappingConfig.setGenerateApisWithThrowsException(
                    MappingConfigConstants.DEFAULT_GENERATE_APIS_WITH_THROWS_EXCEPTION);
        }
        if (mappingConfig.getGenerateApisWithSuspendFunctions() == null) {
            mappingConfig.setGenerateApisWithSuspendFunctions(
                    MappingConfigConstants.DEFAULT_GENERATE_APIS_WITH_SUSPEND_FUNCTIONS);
        }
        if (mappingConfig.getAddGeneratedAnnotation() == null) {
            mappingConfig.setAddGeneratedAnnotation(MappingConfigConstants.DEFAULT_ADD_GENERATED_ANNOTATION);
        }
        if (mappingConfig.getGenerateJacksonTypeIdResolver() == null) {
            mappingConfig.setGenerateJacksonTypeIdResolver(
                    MappingConfigConstants.DEFAULT_GENERATE_JACKSON_TYPE_ID_RESOLVER);
        }
        if (mappingConfig.getUseOptionalForNullableReturnTypes() == null) {
            mappingConfig.setUseOptionalForNullableReturnTypes(
                    MappingConfigConstants.DEFAULT_USE_OPTIONAL_FOR_NULLABLE_RETURN_TYPES);
        }
        if (mappingConfig.getUseOptionalForNullableInputTypes() == null) {
            mappingConfig.setUseOptionalForNullableInputTypes(
                    MappingConfigConstants.DEFAULT_USE_OPTIONAL_FOR_NULLABLE_INPUT_TYPES);
        }
        if (mappingConfig.getApiNamePrefixStrategy() == null) {
            mappingConfig.setApiNamePrefixStrategy(MappingConfigConstants.DEFAULT_API_NAME_PREFIX_STRATEGY);
        }
        if (mappingConfig.getApiRootInterfaceStrategy() == null) {
            mappingConfig.setApiRootInterfaceStrategy(MappingConfigConstants.DEFAULT_API_ROOT_INTERFACE_STRATEGY);
        }
        if (mappingConfig.getApiInterfaceStrategy() == null) {
            mappingConfig.setApiInterfaceStrategy(MappingConfigConstants.DEFAULT_API_INTERFACE_STRATEGY);
        }
        if (Boolean.TRUE.equals(mappingConfig.getGenerateClient())) {
            // required for request serialization
            mappingConfig.setGenerateToString(true);
        }
        if (mappingConfig.getResponseProjectionMaxDepth() == null) {
            mappingConfig.setResponseProjectionMaxDepth(MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_MAX_DEPTH);
        }
        if (mappingConfig.getGeneratedLanguage() == null) {
            mappingConfig.setGeneratedLanguage(MappingConfigConstants.DEFAULT_GENERATED_LANGUAGE);
        }
        if (mappingConfig.getGenerateAllMethodInProjection() == null) {
            mappingConfig.setGenerateAllMethodInProjection(MappingConfigConstants.DEFAULT_GENERATE_ALL_METHOD);
        }
        if (mappingConfig.isGenerateSealedInterfaces() == null) {
            mappingConfig.setGenerateSealedInterfaces(MappingConfigConstants.DEFAULT_GENERATE_SEALED_INTERFACES);
        }
        if (mappingConfig.isSupportUnknownFields() == null) {
            mappingConfig.setSupportUnknownFields(MappingConfigConstants.DEFAULT_SUPPORT_UNKNOWN_FIELDS);
        }
        if (mappingConfig.getUnknownFieldsPropertyName() == null) {
            mappingConfig.setUnknownFieldsPropertyName(MappingConfigConstants.DEFAULT_UNKNOWN_FIELDS_PROPERTY_NAME);
        }
        if (mappingConfig.isGenerateNoArgsConstructorOnly() == null) {
            mappingConfig.setGenerateNoArgsConstructorOnly(
                    MappingConfigConstants.DEFAULT_GENERATE_NOARGS_CONSTRUCTOR_ONLY);
        }
        if (mappingConfig.isGenerateModelsWithPublicFields() == null) {
            mappingConfig.setGenerateModelsWithPublicFields(
                    MappingConfigConstants.DEFAULT_GENERATE_MODELS_WITH_PUBLIC_FIELDS);
        }
    }

}
