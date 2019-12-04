package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.supplier.JsonMappingConfigSupplier;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The type Graphql codegen external config test.
 *
 * @author valinha
 */
class GraphqlCodegenExternalConfigTest {

    /**
     * Check mapping config from json file.
     *
     * @throws Exception the exception
     */
    @Test
    void check_mappingConfigFromJsonFile() throws Exception {
        MappingConfig externalMappingConfig = new JsonMappingConfigSupplier("src/test/resources/json/mappingconfig.json").get();

        assertEquals(externalMappingConfig.getPackageName(), "com.kobylynskyi.graphql.testconfigjson");
        assertEquals(externalMappingConfig.getGenerateApis(), true);
        assertEquals(externalMappingConfig.getCustomTypesMapping().get("Price.amount"), "java.math.BigDecimal");
        assertNull(externalMappingConfig.getApiPackageName());
    }


    /**
     * Check combine mapping config with external.
     *
     * @throws Exception the exception
     */
    @Test
    void check_combineMappingConfigWithExternal() throws Exception {
        MappingConfig mappingConfig = new MappingConfig();
        mappingConfig.setPackageName("com.kobylynskyi.graphql.test1");

        MappingConfig externalMappingConfig = new MappingConfig();
        externalMappingConfig.setPackageName("com.kobylynskyi.graphql.testconfig");
        mappingConfig.combine(externalMappingConfig);
        assertEquals(externalMappingConfig.getPackageName(), mappingConfig.getPackageName());
    }


}