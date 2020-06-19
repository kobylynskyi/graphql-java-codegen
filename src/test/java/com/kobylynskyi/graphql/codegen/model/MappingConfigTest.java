package com.kobylynskyi.graphql.codegen.model;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
class MappingConfigTest {

    @Test
    void combineDefaultWithNull() {
        MappingConfig mappingConfig = buildEmptyMappingConfig();
        mappingConfig.combine(null);

        assertEquals(buildEmptyMappingConfig(), mappingConfig);
    }

    @Test
    void combineDefaultWithDefault() {
        MappingConfig mappingConfig = buildEmptyMappingConfig();
        mappingConfig.combine(buildEmptyMappingConfig());

        assertEquals(buildEmptyMappingConfig(), mappingConfig);
    }

    @Test
    void combineDefaultWithCustom() {
        MappingConfig mappingConfig = buildEmptyMappingConfig();
        mappingConfig.combine(buildMappingConfig());

        assertEquals(singletonMap("1", "2"), mappingConfig.getCustomTypesMapping());
        assertEquals(singletonMap("3", "4"), mappingConfig.getCustomAnnotationsMapping());
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
        assertFalse(mappingConfig.getGenerateAsyncApi());
        assertTrue(mappingConfig.getGenerateParameterizedFieldsResolvers());
        assertTrue(mappingConfig.getGenerateExtensionFieldsResolvers());
        assertEquals(singleton("5"), mappingConfig.getFieldsWithResolvers());
        assertEquals(singleton("8"), mappingConfig.getFieldsWithoutResolvers());
        assertEquals("6", mappingConfig.getRequestSuffix());
        assertEquals("10", mappingConfig.getResponseSuffix());
        assertEquals("7", mappingConfig.getResponseProjectionSuffix());
        assertFalse(mappingConfig.getGenerateClient());
        assertFalse(mappingConfig.getGenerateModelsForRootTypes());
        assertEquals("11", mappingConfig.getTypeResolverPrefix());
        assertEquals("12", mappingConfig.getTypeResolverSuffix());
    }

    @Test
    void combineCustomWithDefault() {
        MappingConfig mappingConfig = buildMappingConfig();
        mappingConfig.combine(buildEmptyMappingConfig());

        assertEquals(singletonMap("1", "2"), mappingConfig.getCustomTypesMapping());
        assertEquals(singletonMap("3", "4"), mappingConfig.getCustomAnnotationsMapping());
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
        assertEquals("SubscriptionsReturnType", mappingConfig.getSubscriptionReturnType());
        assertFalse(mappingConfig.getGenerateAsyncApi());
        assertTrue(mappingConfig.getGenerateParameterizedFieldsResolvers());
        assertTrue(mappingConfig.getGenerateExtensionFieldsResolvers());
        assertEquals(singleton("5"), mappingConfig.getFieldsWithResolvers());
        assertEquals(singleton("8"), mappingConfig.getFieldsWithoutResolvers());
        assertEquals("6", mappingConfig.getRequestSuffix());
        assertEquals("10", mappingConfig.getResponseSuffix());
        assertEquals("7", mappingConfig.getResponseProjectionSuffix());
        assertFalse(mappingConfig.getGenerateClient());
        assertFalse(mappingConfig.getGenerateModelsForRootTypes());
        assertEquals("9", mappingConfig.getParametrizedInputSuffix());
        assertEquals("11", mappingConfig.getTypeResolverPrefix());
        assertEquals("12", mappingConfig.getTypeResolverSuffix());
    }

    @Test
    void combineCustomWithCustom() {
        MappingConfig mappingConfig = buildMappingConfig();
        mappingConfig.combine(buildMappingConfig2());

        assertEquals(hashMap(new HashMap.SimpleEntry<>("1", "2"), new HashMap.SimpleEntry<>("11", "22")),
                mappingConfig.getCustomTypesMapping());
        assertEquals(hashMap(new HashMap.SimpleEntry<>("3", "4"), new HashMap.SimpleEntry<>("33", "44")),
                mappingConfig.getCustomAnnotationsMapping());
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
        assertEquals("SubscriptionsReturnType2", mappingConfig.getSubscriptionReturnType());
        assertTrue(mappingConfig.getGenerateAsyncApi());
        assertFalse(mappingConfig.getGenerateParameterizedFieldsResolvers());
        assertFalse(mappingConfig.getGenerateExtensionFieldsResolvers());
        assertEquals(new HashSet<>(Arrays.asList("5", "55")), mappingConfig.getFieldsWithResolvers());
        assertEquals(new HashSet<>(Arrays.asList("8", "88")), mappingConfig.getFieldsWithoutResolvers());
        assertEquals("66", mappingConfig.getRequestSuffix());
        assertEquals("1010", mappingConfig.getResponseSuffix());
        assertEquals("77", mappingConfig.getResponseProjectionSuffix());
        assertTrue(mappingConfig.getGenerateClient());
        assertTrue(mappingConfig.getGenerateModelsForRootTypes());
        assertEquals("99", mappingConfig.getParametrizedInputSuffix());
        assertEquals("1111", mappingConfig.getTypeResolverPrefix());
        assertEquals("1212", mappingConfig.getTypeResolverSuffix());
    }

    private static Map<String, String> hashMap(AbstractMap.SimpleEntry<String, String>... entries) {
        return Arrays.stream(entries).collect(
                Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (a, b) -> b));
    }

    private static MappingConfig buildMappingConfig() {
        MappingConfig config = new MappingConfig();
        config.setCustomTypesMapping(new HashMap<>(singletonMap("1", "2")));
        config.setCustomAnnotationsMapping(new HashMap<>(singletonMap("3", "4")));
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
        config.setSubscriptionReturnType("SubscriptionsReturnType");
        config.setGenerateAsyncApi(false);
        config.setGenerateParameterizedFieldsResolvers(true);
        config.setGenerateExtensionFieldsResolvers(true);
        config.setFieldsWithResolvers(new HashSet<>(singletonList("5")));
        config.setFieldsWithoutResolvers(new HashSet<>(singleton("8")));
        config.setRequestSuffix("6");
        config.setResponseSuffix("10");
        config.setResponseProjectionSuffix("7");
        config.setGenerateClient(false);
        config.setGenerateModelsForRootTypes(false);
        config.setParametrizedInputSuffix("9");
        config.setTypeResolverPrefix("11");
        config.setTypeResolverSuffix("12");
        return config;
    }

    private static MappingConfig buildMappingConfig2() {
        MappingConfig config = new MappingConfig();
        config.setCustomTypesMapping(new HashMap<>(singletonMap("11", "22")));
        config.setCustomAnnotationsMapping(new HashMap<>(singletonMap("33", "44")));
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
        config.setSubscriptionReturnType("SubscriptionsReturnType2");
        config.setGenerateAsyncApi(true);
        config.setGenerateParameterizedFieldsResolvers(false);
        config.setGenerateExtensionFieldsResolvers(false);
        config.setFieldsWithResolvers(singleton("55"));
        config.setFieldsWithoutResolvers(singleton("88"));
        config.setRequestSuffix("66");
        config.setResponseSuffix("1010");
        config.setResponseProjectionSuffix("77");
        config.setGenerateClient(true);
        config.setGenerateModelsForRootTypes(true);
        config.setParametrizedInputSuffix("99");
        config.setTypeResolverPrefix("1111");
        config.setTypeResolverSuffix("1212");
        return config;
    }

    private static MappingConfig buildEmptyMappingConfig() {
        MappingConfig mappingConfig = new MappingConfig();
        mappingConfig.setCustomTypesMapping(null);
        mappingConfig.setCustomAnnotationsMapping(null);
        mappingConfig.setFieldsWithResolvers(null);
        mappingConfig.setFieldsWithoutResolvers(null);
        return mappingConfig;
    }

}