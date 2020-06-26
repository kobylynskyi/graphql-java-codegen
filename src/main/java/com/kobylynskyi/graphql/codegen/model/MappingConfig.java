package com.kobylynskyi.graphql.codegen.model;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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

    // package name configs:
    private String packageName;
    private String apiPackageName;
    private String modelPackageName;

    // suffix/prefix/strategies
    private String modelNamePrefix;
    private String modelNameSuffix;
    private String apiNamePrefix;
    private String apiNameSuffix;
    private String typeResolverPrefix;
    private String typeResolverSuffix;
    private ApiRootInterfaceStrategy apiRootInterfaceStrategy;
    private ApiNamePrefixStrategy apiNamePrefixStrategy;
    private String modelValidationAnnotation;
    private String subscriptionReturnType;

    // various toggles
    private Boolean generateApis;
    private Boolean generateBuilder;
    private Boolean generateEqualsAndHashCode;
    private Boolean generateToString;
    private Boolean generateImmutableModels;
    private Boolean generateAsyncApi;
    private Boolean generateParameterizedFieldsResolvers;
    private Boolean generateExtensionFieldsResolvers;
    private Boolean generateDataFetchingEnvironmentArgumentInApis;
    private Boolean generateModelsForRootTypes;

    // field resolvers configs:
    private Set<String> fieldsWithResolvers = new HashSet<>();
    private Set<String> fieldsWithoutResolvers = new HashSet<>();

    // parent interfaces configs:
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
        generateApis = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getGenerateApis);
        packageName = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getPackageName);
        apiPackageName = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getApiPackageName);
        modelPackageName = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getModelPackageName);
        modelNamePrefix = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getModelNamePrefix);
        modelNameSuffix = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getModelNameSuffix);
        apiNamePrefix = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getApiNamePrefix);
        apiNameSuffix = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getApiNameSuffix);
        apiRootInterfaceStrategy = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getApiRootInterfaceStrategy);
        apiNamePrefixStrategy = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getApiNamePrefixStrategy);
        typeResolverPrefix = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getTypeResolverPrefix);
        typeResolverSuffix = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getTypeResolverSuffix);
        modelValidationAnnotation = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getModelValidationAnnotation);
        subscriptionReturnType = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getSubscriptionReturnType);
        generateBuilder = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getGenerateBuilder);
        generateEqualsAndHashCode = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getGenerateEqualsAndHashCode);
        generateImmutableModels = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getGenerateImmutableModels);
        generateToString = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getGenerateToString);
        generateAsyncApi = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getGenerateAsyncApi);
        generateParameterizedFieldsResolvers = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getGenerateParameterizedFieldsResolvers);
        generateExtensionFieldsResolvers = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getGenerateExtensionFieldsResolvers);
        generateDataFetchingEnvironmentArgumentInApis = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getGenerateDataFetchingEnvironmentArgumentInApis);
        generateModelsForRootTypes = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getGenerateModelsForRootTypes);
        queryResolverParentInterface = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getQueryResolverParentInterface);
        mutationResolverParentInterface = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getMutationResolverParentInterface);
        subscriptionResolverParentInterface = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getSubscriptionResolverParentInterface);
        resolverParentInterface = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getResolverParentInterface);
        generateClient = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getGenerateClient);
        requestSuffix = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getRequestSuffix);
        responseSuffix = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getResponseSuffix);
        responseProjectionSuffix = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getResponseProjectionSuffix);
        parametrizedInputSuffix = getValueOrDefaultToThis(source, GraphQLCodegenConfiguration::getParametrizedInputSuffix);
        fieldsWithResolvers = combineSet(fieldsWithResolvers, source.fieldsWithResolvers);
        fieldsWithoutResolvers = combineSet(fieldsWithoutResolvers, source.fieldsWithoutResolvers);
        customTypesMapping = combineMap(customTypesMapping, source.customTypesMapping);
        customAnnotationsMapping = combineMap(customAnnotationsMapping, source.customAnnotationsMapping);
    }

    private static Map<String, String> combineMap(Map<String, String> thisMap, Map<String, String> otherMap) {
        if (thisMap != null && otherMap != null) {
            Map<String, String> resultMap = new HashMap<>();
            resultMap.putAll(thisMap);
            resultMap.putAll(otherMap);
            return resultMap;
        } else if (thisMap == null) {
            return otherMap;
        } else {
            return thisMap;
        }
    }

    private static Set<String> combineSet(Set<String> thisSet, Set<String> otherSet) {
        if (thisSet != null && otherSet != null) {
            Set<String> resultSet = new HashSet<>();
            resultSet.addAll(thisSet);
            resultSet.addAll(otherSet);
            return resultSet;
        } else if (thisSet == null) {
            return otherSet;
        } else {
            return thisSet;
        }
    }

    private <T> T getValueOrDefaultToThis(MappingConfig source, Function<MappingConfig, T> getValueFunction) {
        T sourceValue = getValueFunction.apply(source);
        return sourceValue != null ? sourceValue : getValueFunction.apply(this);
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
