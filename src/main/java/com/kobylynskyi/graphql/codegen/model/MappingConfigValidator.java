package com.kobylynskyi.graphql.codegen.model;

import com.kobylynskyi.graphql.codegen.utils.Utils;

/**
 * Validator of the mapping config
 */
public class MappingConfigValidator {

    private MappingConfigValidator() {
    }

    /**
     * Validator of the mapping config
     *
     * @param mappingConfig Global mapping config
     * @throws IllegalArgumentException in case config is not valid
     */
    public static void validate(MappingConfig mappingConfig) {
        if (mappingConfig.getApiRootInterfaceStrategy() == ApiRootInterfaceStrategy.INTERFACE_PER_SCHEMA &&
                mappingConfig.getApiNamePrefixStrategy() == ApiNamePrefixStrategy.CONSTANT) {
            // we will have a conflict in case there is "type Query" in multiple graphql schema files
            throw new IllegalArgumentException("API prefix should not be CONSTANT for INTERFACE_PER_SCHEMA option");
        }
        if (Boolean.TRUE.equals(mappingConfig.getGenerateApis())
                && Boolean.TRUE.equals(mappingConfig.getGenerateModelsForRootTypes())
                && mappingConfig.getApiNamePrefixStrategy() == ApiNamePrefixStrategy.CONSTANT) {
            // checking for conflict between root type model classes and api interfaces
            if (Utils.stringsEqualIgnoreSpaces(
                    mappingConfig.getApiNamePrefix(), mappingConfig.getModelNamePrefix()) &&
                    Utils.stringsEqualIgnoreSpaces(
                            mappingConfig.getApiNameSuffix(), mappingConfig.getModelNameSuffix())) {
                // we will have a conflict between model pojo (Query.java) and api interface (Query.java)
                throw new IllegalArgumentException("Either disable APIs generation or " +
                        "set different Prefix/Suffix for API classes and model classes");
            }
            // checking for conflict between root type model resolver classes and api interfaces
            if (Utils.stringsEqualIgnoreSpaces(mappingConfig.getApiNamePrefix(),
                    mappingConfig.getTypeResolverPrefix()) &&
                    Utils.stringsEqualIgnoreSpaces(mappingConfig.getApiNameSuffix(),
                            mappingConfig.getTypeResolverSuffix())) {
                // we will have a conflict between model resolver interface (QueryResolver.java) and api interface
                // resolver (QueryResolver.java)
                throw new IllegalArgumentException("Either disable APIs generation or " +
                        "set different Prefix/Suffix for API classes and type resolver classes");
            }
        }
    }

}
