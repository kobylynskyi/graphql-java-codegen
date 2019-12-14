package com.kobylynskyi.graphql.codegen.model;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

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
        assertTrue(mappingConfig.getGenerateApis());
        assertTrue(mappingConfig.getGenerateEqualsAndHashCode());
        assertTrue(mappingConfig.getGenerateToString());
        assertEquals("ModelNamePrefix", mappingConfig.getModelNamePrefix());
        assertEquals("ModelNameSuffix", mappingConfig.getModelNameSuffix());
        assertEquals("ModelPackageName", mappingConfig.getModelPackageName());
        assertEquals("ModelValidationAnnotation", mappingConfig.getModelValidationAnnotation());
        assertEquals("PackageName", mappingConfig.getPackageName());
    }

    @Test
    void combineCustomWithDefault() {
        MappingConfig mappingConfig = buildMappingConfig();
        mappingConfig.combine(buildEmptyMappingConfig());

        assertEquals(singletonMap("1", "2"), mappingConfig.getCustomTypesMapping());
        assertEquals(singletonMap("3", "4"), mappingConfig.getCustomAnnotationsMapping());
        assertEquals("ApiPackageName", mappingConfig.getApiPackageName());
        assertTrue(mappingConfig.getGenerateApis());
        assertTrue(mappingConfig.getGenerateEqualsAndHashCode());
        assertTrue(mappingConfig.getGenerateToString());
        assertEquals("ModelNamePrefix", mappingConfig.getModelNamePrefix());
        assertEquals("ModelNameSuffix", mappingConfig.getModelNameSuffix());
        assertEquals("ModelPackageName", mappingConfig.getModelPackageName());
        assertEquals("ModelValidationAnnotation", mappingConfig.getModelValidationAnnotation());
        assertEquals("PackageName", mappingConfig.getPackageName());
        assertEquals("SubscriptionsReturnType", mappingConfig.getSubscriptionReturnType());
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
        assertFalse(mappingConfig.getGenerateApis());
        assertFalse(mappingConfig.getGenerateEqualsAndHashCode());
        assertFalse(mappingConfig.getGenerateToString());
        assertEquals("ModelNamePrefix2", mappingConfig.getModelNamePrefix());
        assertEquals("ModelNameSuffix2", mappingConfig.getModelNameSuffix());
        assertEquals("ModelPackageName2", mappingConfig.getModelPackageName());
        assertEquals("ModelValidationAnnotation2", mappingConfig.getModelValidationAnnotation());
        assertEquals("PackageName2", mappingConfig.getPackageName());
        assertEquals("SubscriptionsReturnType2", mappingConfig.getSubscriptionReturnType());
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
        config.setGenerateApis(true);
        config.setGenerateEqualsAndHashCode(true);
        config.setGenerateToString(true);
        config.setModelNamePrefix("ModelNamePrefix");
        config.setModelNameSuffix("ModelNameSuffix");
        config.setModelPackageName("ModelPackageName");
        config.setModelValidationAnnotation("ModelValidationAnnotation");
        config.setPackageName("PackageName");
        config.setSubscriptionReturnType("SubscriptionsReturnType");
        return config;
    }

    private static MappingConfig buildMappingConfig2() {
        MappingConfig config = new MappingConfig();
        config.setCustomTypesMapping(new HashMap<>(singletonMap("11", "22")));
        config.setCustomAnnotationsMapping(new HashMap<>(singletonMap("33", "44")));
        config.setApiPackageName("ApiPackageName2");
        config.setGenerateApis(false);
        config.setGenerateEqualsAndHashCode(false);
        config.setGenerateToString(false);
        config.setModelNamePrefix("ModelNamePrefix2");
        config.setModelNameSuffix("ModelNameSuffix2");
        config.setModelPackageName("ModelPackageName2");
        config.setModelValidationAnnotation("ModelValidationAnnotation2");
        config.setPackageName("PackageName2");
        config.setSubscriptionReturnType("SubscriptionsReturnType2");
        return config;
    }

    private static MappingConfig buildEmptyMappingConfig() {
        MappingConfig mappingConfig = new MappingConfig();
        mappingConfig.setCustomTypesMapping(null);
        mappingConfig.setCustomAnnotationsMapping(null);
        return mappingConfig;
    }

}