package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.supplier.MergeableMappingConfigSupplier;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The type Graphql codegen external config test.
 *
 * @author valinha
 */
class GraphQLCodegenExternalConfigTest {

    /**
     * Check mapping config from json file.
     */
    @Test
    void check_mappingConfigFromJsonFile() {
        MappingConfig externalMappingConfig =
                new MergeableMappingConfigSupplier(new ArrayList<String>() {
                    {
                        add("src/test/resources/json/mappingconfig.json");
                        add("src/test/resources/json/mappingconfig2.json");
                    }
                }).get();

        assertEquals("com.kobylynskyi.graphql.testconfigjson", externalMappingConfig.getPackageName());
        // If the previous configuration file does not contain a key, the later one will be used.
        assertEquals(externalMappingConfig.getGenerateToString(), true);
        assertTrue(externalMappingConfig.getGenerateApis());
        assertEquals("java.math.BigDecimal", externalMappingConfig.getCustomTypesMapping().get("Price.amount"));
        assertNull(externalMappingConfig.getApiPackageName());
    }


    /**
     * Check mapping config from json file.
     * The previous key is preferred. If there is no key, the following key is used.
     */
    @Test
    void check_mappingConfigFromJsonFile_key_priority_json_json() {
        MappingConfig externalMappingConfig =
                new MergeableMappingConfigSupplier(new ArrayList<String>() {
                    {
                        add("src/test/resources/json/mappingconfig2.json");
                        add("src/test/resources/json/mappingconfig3.json");
                    }
                }).get();

        assertEquals("com.kobylynskyi.graphql.testconfigjson", externalMappingConfig.getPackageName());
        assertEquals(externalMappingConfig.getGenerateToString(), true);
    }

    /**
     * Check mapping config from json and hocon file.
     * The previous key is preferred. If there is no key, the following key is used.
     */
    @Test
    void check_mappingConfigFromJsonFile_key_priority_json_conf() {
        MappingConfig externalMappingConfig =
                new MergeableMappingConfigSupplier(new ArrayList<String>() {
                    {
                        add("src/test/resources/json/mappingconfig3.json");
                        add("src/test/resources/json/mappingconfig4.conf");
                    }
                }).get();

        assertEquals(externalMappingConfig.getGenerateToString(), false);
        assertEquals(externalMappingConfig.getGenerateApis(), true);
    }

    /**
     * Check mapping config from json and hocon file.
     */
    @Test
    void check_mappingConfigFromJsonFile_key_priority_hocon_json() {
        MappingConfig externalMappingConfig =
                new MergeableMappingConfigSupplier(new ArrayList<String>() {
                    {
                        add("src/test/resources/json/mappingconfig4.conf");
                        add("src/test/resources/json/mappingconfig3.json");
                    }
                }).get();

        assertEquals(externalMappingConfig.getGenerateToString(), true);
        assertEquals(externalMappingConfig.getGenerateApis(), true);
    }

    /**
     * Check mapping config from hocon file.
     * The previous key is preferred. If there is no key, the following key is used.
     */
    @Test
    void check_mappingConfigFromJsonFile_key_priority_hocon_hocon() {
        MappingConfig externalMappingConfig =
                new MergeableMappingConfigSupplier(new ArrayList<String>() {
                    {
                        add("src/test/resources/json/mappingconfig5.conf");
                        add("src/test/resources/json/mappingconfig4.conf");
                    }
                }).get();

        assertEquals(externalMappingConfig.getGenerateToString(), false);
        assertEquals(externalMappingConfig.getGenerateApis(), false);
    }

    /**
     * Check combine mapping config with external.
     */
    @Test
    void check_combineMappingConfigWithExternal() {
        MappingConfig mappingConfig = new MappingConfig();
        mappingConfig.setPackageName("com.kobylynskyi.graphql.test1");

        MappingConfig externalMappingConfig = new MappingConfig();
        externalMappingConfig.setPackageName("com.kobylynskyi.graphql.testconfig");
        mappingConfig.combine(externalMappingConfig);
        assertEquals(mappingConfig.getPackageName(), externalMappingConfig.getPackageName());
    }


}