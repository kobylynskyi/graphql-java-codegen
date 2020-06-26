package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.supplier.JsonMappingConfigSupplier;
import org.junit.jupiter.api.Test;

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
        MappingConfig externalMappingConfig = new JsonMappingConfigSupplier("src/test/resources/json/mappingconfig.json").get();

        assertEquals("com.kobylynskyi.graphql.testconfigjson", externalMappingConfig.getPackageName());
        assertTrue(externalMappingConfig.getGenerateApis());
        assertEquals("java.math.BigDecimal", externalMappingConfig.getCustomTypesMapping().get("Price.amount"));
        assertNull(externalMappingConfig.getApiPackageName());
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