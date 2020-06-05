package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.supplier.JsonMappingConfigSupplier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

        assertEquals(externalMappingConfig.getPackageName(), "com.kobylynskyi.graphql.testconfigjson");
        assertEquals(externalMappingConfig.getGenerateApis(), true);
        assertEquals(externalMappingConfig.getCustomTypesMapping().get("Price.amount"), "java.math.BigDecimal");
        assertNull(externalMappingConfig.getApiPackageName());
    }


    /**
     * Check combine mapping config with external.
     */
    @Test
    void check_combineMappingConfigWithExternal() {
        MappingConfig mappingConfig = TestUtils.initMappingConfig();
        mappingConfig.setPackageName("com.kobylynskyi.graphql.test1");

        MappingConfig externalMappingConfig = TestUtils.initMappingConfig();
        externalMappingConfig.setPackageName("com.kobylynskyi.graphql.testconfig");
        mappingConfig.combine(externalMappingConfig);
        assertEquals(externalMappingConfig.getPackageName(), mappingConfig.getPackageName());
    }


}