package com.kobylynskyi.graphql.codegen.model;

import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("unchecked")
class MappingConfigTest {

    @Test
    void combineDefaultWithNull() {
        MappingConfig mappingConfig = buildEmptyMappingConfig();
        mappingConfig.combine(null);

        compareMappingConfigs(mappingConfig, buildEmptyMappingConfig());
    }

    @Test
    void combineDefaultWithDefault() {
        MappingConfig mappingConfig = buildEmptyMappingConfig();
        mappingConfig.combine(buildEmptyMappingConfig());

        compareMappingConfigs(mappingConfig, buildEmptyMappingConfig());
    }

    @Test
    void combineDefaultWithCustom() {
        MappingConfig mappingConfig = buildEmptyMappingConfig();
        mappingConfig.combine(buildMappingConfig());

        assertEquals(singletonMap("1", "2"), mappingConfig.getCustomTypesMapping());
        assertEquals(singletonMap("3", singletonList("4")), mappingConfig.getCustomAnnotationsMapping());
        assertEquals(singletonMap("5", singletonList("6")), mappingConfig.getDirectiveAnnotationsMapping());
        assertEquals("ApiPackageName", mappingConfig.getApiPackageName());
        assertTrue(mappingConfig.getGenerateBuilder());
        assertTrue(mappingConfig.getGenerateApis());
        assertTrue(mappingConfig.getGenerateEqualsAndHashCode());
        assertFalse(mappingConfig.getGenerateImmutableModels());
        assertTrue(mappingConfig.getGenerateToString());
        assertEquals("ModelNamePrefix", mappingConfig.getModelNamePrefix());
        assertEquals("ModelNameSuffix", mappingConfig.getModelNameSuffix());
        assertEquals("ModelPackageName", mappingConfig.getModelPackageName());
        assertEquals("ModelValidationAnnotation", mappingConfig.getModelValidationAnnotation());
        assertEquals("PackageName", mappingConfig.getPackageName());
        assertEquals("ApiReturnType", mappingConfig.getApiReturnType());
        assertEquals("ApiReturnListType", mappingConfig.getApiReturnListType());
        assertEquals("SubscriptionsReturnType", mappingConfig.getSubscriptionReturnType());
        assertTrue(mappingConfig.getGenerateParameterizedFieldsResolvers());
        assertTrue(mappingConfig.getGenerateExtensionFieldsResolvers());
        assertTrue(mappingConfig.getUseOptionalForNullableReturnTypes());
        assertEquals(ApiInterfaceStrategy.INTERFACE_PER_OPERATION, mappingConfig.getApiInterfaceStrategy());
        assertEquals(ApiNamePrefixStrategy.FOLDER_NAME_AS_PREFIX, mappingConfig.getApiNamePrefixStrategy());
        assertEquals(ApiRootInterfaceStrategy.SINGLE_INTERFACE, mappingConfig.getApiRootInterfaceStrategy());
        assertEquals(singleton("5"), mappingConfig.getFieldsWithResolvers());
        assertEquals(singleton("8"), mappingConfig.getFieldsWithoutResolvers());
        assertEquals("6", mappingConfig.getRequestSuffix());
        assertEquals("10", mappingConfig.getResponseSuffix());
        assertEquals("7", mappingConfig.getResponseProjectionSuffix());
        assertFalse(mappingConfig.getGenerateClient());
        assertFalse(mappingConfig.getGenerateModelsForRootTypes());
        assertTrue(mappingConfig.getGenerateApisWithThrowsException());
        assertTrue(mappingConfig.getAddGeneratedAnnotation());
        assertEquals("11", mappingConfig.getTypeResolverPrefix());
        assertEquals("12", mappingConfig.getTypeResolverSuffix());
        assertEquals("key", mappingConfig.getRelayConfig().getDirectiveArgumentName());
    }

    @Test
    void combineCustomWithDefault() {
        MappingConfig mappingConfig = buildMappingConfig();
        mappingConfig.combine(buildEmptyMappingConfig());

        assertEquals(singletonMap("1", "2"), mappingConfig.getCustomTypesMapping());
        assertEquals(singletonMap("3", singletonList("4")), mappingConfig.getCustomAnnotationsMapping());
        assertEquals(singletonMap("5", singletonList("6")), mappingConfig.getDirectiveAnnotationsMapping());
        assertEquals("ApiPackageName", mappingConfig.getApiPackageName());
        assertTrue(mappingConfig.getGenerateBuilder());
        assertTrue(mappingConfig.getGenerateApis());
        assertTrue(mappingConfig.getGenerateEqualsAndHashCode());
        assertFalse(mappingConfig.getGenerateImmutableModels());
        assertTrue(mappingConfig.getGenerateToString());
        assertEquals("ModelNamePrefix", mappingConfig.getModelNamePrefix());
        assertEquals("ModelNameSuffix", mappingConfig.getModelNameSuffix());
        assertEquals("ModelPackageName", mappingConfig.getModelPackageName());
        assertEquals("ModelValidationAnnotation", mappingConfig.getModelValidationAnnotation());
        assertEquals("PackageName", mappingConfig.getPackageName());
        assertEquals("ApiReturnType", mappingConfig.getApiReturnType());
        assertEquals("ApiReturnListType", mappingConfig.getApiReturnListType());
        assertEquals("SubscriptionsReturnType", mappingConfig.getSubscriptionReturnType());
        assertTrue(mappingConfig.getGenerateParameterizedFieldsResolvers());
        assertTrue(mappingConfig.getGenerateExtensionFieldsResolvers());
        assertTrue(mappingConfig.getUseOptionalForNullableReturnTypes());
        assertEquals(ApiInterfaceStrategy.INTERFACE_PER_OPERATION, mappingConfig.getApiInterfaceStrategy());
        assertEquals(ApiNamePrefixStrategy.FOLDER_NAME_AS_PREFIX, mappingConfig.getApiNamePrefixStrategy());
        assertEquals(ApiRootInterfaceStrategy.SINGLE_INTERFACE, mappingConfig.getApiRootInterfaceStrategy());
        assertEquals(singleton("5"), mappingConfig.getFieldsWithResolvers());
        assertEquals(singleton("8"), mappingConfig.getFieldsWithoutResolvers());
        assertEquals("6", mappingConfig.getRequestSuffix());
        assertEquals("10", mappingConfig.getResponseSuffix());
        assertEquals("7", mappingConfig.getResponseProjectionSuffix());
        assertFalse(mappingConfig.getGenerateClient());
        assertFalse(mappingConfig.getGenerateModelsForRootTypes());
        assertTrue(mappingConfig.getGenerateApisWithThrowsException());
        assertTrue(mappingConfig.getAddGeneratedAnnotation());
        assertEquals("9", mappingConfig.getParametrizedInputSuffix());
        assertEquals("11", mappingConfig.getTypeResolverPrefix());
        assertEquals("12", mappingConfig.getTypeResolverSuffix());
        assertEquals("key", mappingConfig.getRelayConfig().getDirectiveArgumentName());
    }

    @Test
    void combineCustomWithCustom() {
        MappingConfig mappingConfig = buildMappingConfig();
        mappingConfig.combine(buildMappingConfig2());

        assertEquals(hashMap(new HashMap.SimpleEntry<>("1", "2"), new HashMap.SimpleEntry<>("11", "22")),
                mappingConfig.getCustomTypesMapping());
        assertEquals(hashMap(new HashMap.SimpleEntry<>("3", singletonList("4")), new HashMap.SimpleEntry<>("33", singletonList("44"))),
                mappingConfig.getCustomAnnotationsMapping());
        assertEquals(hashMap(new HashMap.SimpleEntry<>("5", singletonList("6")), new HashMap.SimpleEntry<>("55", singletonList("66"))),
                mappingConfig.getDirectiveAnnotationsMapping());
        assertEquals("ApiPackageName2", mappingConfig.getApiPackageName());
        assertFalse(mappingConfig.getGenerateBuilder());
        assertFalse(mappingConfig.getGenerateApis());
        assertFalse(mappingConfig.getGenerateEqualsAndHashCode());
        assertTrue(mappingConfig.getGenerateImmutableModels());
        assertFalse(mappingConfig.getGenerateToString());
        assertEquals("ModelNamePrefix2", mappingConfig.getModelNamePrefix());
        assertEquals("ModelNameSuffix2", mappingConfig.getModelNameSuffix());
        assertEquals("ModelPackageName2", mappingConfig.getModelPackageName());
        assertEquals("ModelValidationAnnotation2", mappingConfig.getModelValidationAnnotation());
        assertEquals("PackageName2", mappingConfig.getPackageName());
        assertEquals("ApiReturnType2", mappingConfig.getApiReturnType());
        assertEquals("ApiReturnListType2", mappingConfig.getApiReturnListType());
        assertEquals("SubscriptionsReturnType2", mappingConfig.getSubscriptionReturnType());
        assertFalse(mappingConfig.getGenerateParameterizedFieldsResolvers());
        assertFalse(mappingConfig.getGenerateExtensionFieldsResolvers());
        assertFalse(mappingConfig.getUseOptionalForNullableReturnTypes());
        assertEquals(ApiInterfaceStrategy.DO_NOT_GENERATE, mappingConfig.getApiInterfaceStrategy());
        assertEquals(ApiNamePrefixStrategy.FILE_NAME_AS_PREFIX, mappingConfig.getApiNamePrefixStrategy());
        assertEquals(ApiRootInterfaceStrategy.DO_NOT_GENERATE, mappingConfig.getApiRootInterfaceStrategy());
        assertEquals(new HashSet<>(Arrays.asList("5", "55")), mappingConfig.getFieldsWithResolvers());
        assertEquals(new HashSet<>(Arrays.asList("8", "88")), mappingConfig.getFieldsWithoutResolvers());
        assertEquals("66", mappingConfig.getRequestSuffix());
        assertEquals("1010", mappingConfig.getResponseSuffix());
        assertEquals("77", mappingConfig.getResponseProjectionSuffix());
        assertTrue(mappingConfig.getGenerateClient());
        assertTrue(mappingConfig.getGenerateModelsForRootTypes());
        assertFalse(mappingConfig.getGenerateApisWithThrowsException());
        assertFalse(mappingConfig.getAddGeneratedAnnotation());
        assertEquals("99", mappingConfig.getParametrizedInputSuffix());
        assertEquals("1111", mappingConfig.getTypeResolverPrefix());
        assertEquals("1212", mappingConfig.getTypeResolverSuffix());
        assertEquals("for", mappingConfig.getRelayConfig().getDirectiveArgumentName());
    }

    private static <T> Map<String, T> hashMap(AbstractMap.SimpleEntry<String, T>... entries) {
        return Arrays.stream(entries).collect(
                Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (a, b) -> b));
    }

    private static MappingConfig buildMappingConfig() {
        MappingConfig config = new MappingConfig();
        config.setCustomTypesMapping(new HashMap<>(singletonMap("1", "2")));
        config.setCustomAnnotationsMapping(new HashMap<>(singletonMap("3", singletonList("4"))));
        config.setDirectiveAnnotationsMapping(new HashMap<>(singletonMap("5", singletonList("6"))));
        config.setApiPackageName("ApiPackageName");
        config.setGenerateBuilder(true);
        config.setGenerateApis(true);
        config.setGenerateEqualsAndHashCode(true);
        config.setGenerateImmutableModels(false);
        config.setGenerateToString(true);
        config.setModelNamePrefix("ModelNamePrefix");
        config.setModelNameSuffix("ModelNameSuffix");
        config.setModelPackageName("ModelPackageName");
        config.setModelValidationAnnotation("ModelValidationAnnotation");
        config.setPackageName("PackageName");
        config.setApiReturnType("ApiReturnType");
        config.setApiReturnListType("ApiReturnListType");
        config.setSubscriptionReturnType("SubscriptionsReturnType");
        config.setGenerateParameterizedFieldsResolvers(true);
        config.setGenerateExtensionFieldsResolvers(true);
        config.setUseOptionalForNullableReturnTypes(true);
        config.setApiInterfaceStrategy(ApiInterfaceStrategy.INTERFACE_PER_OPERATION);
        config.setApiNamePrefixStrategy(ApiNamePrefixStrategy.FOLDER_NAME_AS_PREFIX);
        config.setApiRootInterfaceStrategy(ApiRootInterfaceStrategy.SINGLE_INTERFACE);
        config.setFieldsWithResolvers(new HashSet<>(singletonList("5")));
        config.setFieldsWithoutResolvers(new HashSet<>(singleton("8")));
        config.setRequestSuffix("6");
        config.setResponseSuffix("10");
        config.setResponseProjectionSuffix("7");
        config.setGenerateClient(false);
        config.setGenerateModelsForRootTypes(false);
        config.setGenerateApisWithThrowsException(true);
        config.setAddGeneratedAnnotation(true);
        config.setParametrizedInputSuffix("9");
        config.setTypeResolverPrefix("11");
        config.setTypeResolverSuffix("12");
        RelayConfig relayConfig = new RelayConfig();
        relayConfig.setDirectiveArgumentName("key");
        config.setRelayConfig(relayConfig);
        return config;
    }

    private static MappingConfig buildMappingConfig2() {
        MappingConfig config = new MappingConfig();
        config.setCustomTypesMapping(new HashMap<>(singletonMap("11", "22")));
        config.setCustomAnnotationsMapping(new HashMap<>(singletonMap("33", singletonList("44"))));
        config.setDirectiveAnnotationsMapping(new HashMap<>(singletonMap("55", singletonList("66"))));
        config.setApiPackageName("ApiPackageName2");
        config.setGenerateBuilder(false);
        config.setGenerateApis(false);
        config.setGenerateImmutableModels(true);
        config.setGenerateEqualsAndHashCode(false);
        config.setGenerateToString(false);
        config.setModelNamePrefix("ModelNamePrefix2");
        config.setModelNameSuffix("ModelNameSuffix2");
        config.setModelPackageName("ModelPackageName2");
        config.setModelValidationAnnotation("ModelValidationAnnotation2");
        config.setPackageName("PackageName2");
        config.setApiReturnType("ApiReturnType2");
        config.setApiReturnListType("ApiReturnListType2");
        config.setSubscriptionReturnType("SubscriptionsReturnType2");
        config.setGenerateParameterizedFieldsResolvers(false);
        config.setGenerateExtensionFieldsResolvers(false);
        config.setUseOptionalForNullableReturnTypes(false);
        config.setApiInterfaceStrategy(ApiInterfaceStrategy.DO_NOT_GENERATE);
        config.setApiNamePrefixStrategy(ApiNamePrefixStrategy.FILE_NAME_AS_PREFIX);
        config.setApiRootInterfaceStrategy(ApiRootInterfaceStrategy.DO_NOT_GENERATE);
        config.setFieldsWithResolvers(singleton("55"));
        config.setFieldsWithoutResolvers(singleton("88"));
        config.setRequestSuffix("66");
        config.setResponseSuffix("1010");
        config.setResponseProjectionSuffix("77");
        config.setGenerateClient(true);
        config.setGenerateModelsForRootTypes(true);
        config.setGenerateApisWithThrowsException(false);
        config.setAddGeneratedAnnotation(false);
        config.setParametrizedInputSuffix("99");
        config.setTypeResolverPrefix("1111");
        config.setTypeResolverSuffix("1212");
        RelayConfig relayConfig = new RelayConfig();
        relayConfig.setDirectiveArgumentName("for");
        config.setRelayConfig(relayConfig);
        return config;
    }

    private static MappingConfig buildEmptyMappingConfig() {
        MappingConfig mappingConfig = new MappingConfig();
        mappingConfig.setCustomTypesMapping(null);
        mappingConfig.setCustomAnnotationsMapping(null);
        mappingConfig.setDirectiveAnnotationsMapping(null);
        mappingConfig.setFieldsWithResolvers(null);
        mappingConfig.setFieldsWithoutResolvers(null);
        mappingConfig.setRelayConfig(null);
        return mappingConfig;
    }

    private static void compareMappingConfigs(MappingConfig mappingConfig, MappingConfig expectedMappingConfig) {
        assertEquals(expectedMappingConfig.getCustomTypesMapping(), mappingConfig.getCustomTypesMapping());
        assertEquals(expectedMappingConfig.getCustomAnnotationsMapping(), mappingConfig.getCustomAnnotationsMapping());
        assertEquals(expectedMappingConfig.getDirectiveAnnotationsMapping(), mappingConfig.getDirectiveAnnotationsMapping());
        assertEquals(expectedMappingConfig.getApiPackageName(), mappingConfig.getApiPackageName());
        assertEquals(expectedMappingConfig.getGenerateBuilder(), mappingConfig.getGenerateBuilder());
        assertEquals(expectedMappingConfig.getGenerateApis(), mappingConfig.getGenerateApis());
        assertEquals(expectedMappingConfig.getGenerateEqualsAndHashCode(), mappingConfig.getGenerateEqualsAndHashCode());
        assertEquals(expectedMappingConfig.getGenerateImmutableModels(), mappingConfig.getGenerateImmutableModels());
        assertEquals(expectedMappingConfig.getGenerateToString(), mappingConfig.getGenerateToString());
        assertEquals(expectedMappingConfig.getSubscriptionReturnType(), mappingConfig.getSubscriptionReturnType());
        assertEquals(expectedMappingConfig.getApiReturnType(), mappingConfig.getApiReturnType());
        assertEquals(expectedMappingConfig.getApiReturnListType(), mappingConfig.getApiReturnListType());
        assertEquals(expectedMappingConfig.getModelNamePrefix(), mappingConfig.getModelNamePrefix());
        assertEquals(expectedMappingConfig.getModelNameSuffix(), mappingConfig.getModelNameSuffix());
        assertEquals(expectedMappingConfig.getModelPackageName(), mappingConfig.getModelPackageName());
        assertEquals(expectedMappingConfig.getModelValidationAnnotation(), mappingConfig.getModelValidationAnnotation());
        assertEquals(expectedMappingConfig.getPackageName(), mappingConfig.getPackageName());
        assertEquals(expectedMappingConfig.getGenerateParameterizedFieldsResolvers(), mappingConfig.getGenerateParameterizedFieldsResolvers());
        assertEquals(expectedMappingConfig.getGenerateExtensionFieldsResolvers(), mappingConfig.getGenerateExtensionFieldsResolvers());
        assertEquals(expectedMappingConfig.getFieldsWithResolvers(), mappingConfig.getFieldsWithResolvers());
        assertEquals(expectedMappingConfig.getFieldsWithoutResolvers(), mappingConfig.getFieldsWithoutResolvers());
        assertEquals(expectedMappingConfig.getRequestSuffix(), mappingConfig.getRequestSuffix());
        assertEquals(expectedMappingConfig.getResponseSuffix(), mappingConfig.getResponseSuffix());
        assertEquals(expectedMappingConfig.getResponseProjectionSuffix(), mappingConfig.getResponseProjectionSuffix());
        assertEquals(expectedMappingConfig.getGenerateClient(), mappingConfig.getGenerateClient());
        assertEquals(expectedMappingConfig.getGenerateModelsForRootTypes(), mappingConfig.getGenerateModelsForRootTypes());
        assertEquals(expectedMappingConfig.getGenerateApisWithThrowsException(), mappingConfig.getGenerateApisWithThrowsException());
        assertEquals(expectedMappingConfig.getTypeResolverPrefix(), mappingConfig.getTypeResolverPrefix());
        assertEquals(expectedMappingConfig.getTypeResolverSuffix(), mappingConfig.getTypeResolverSuffix());
        assertEquals(expectedMappingConfig.getUseOptionalForNullableReturnTypes(), mappingConfig.getUseOptionalForNullableReturnTypes());
        assertEquals(expectedMappingConfig.getRelayConfig(), mappingConfig.getRelayConfig());
    }

}
