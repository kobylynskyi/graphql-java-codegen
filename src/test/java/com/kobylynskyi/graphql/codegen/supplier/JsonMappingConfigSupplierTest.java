package com.kobylynskyi.graphql.codegen.supplier;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonMappingConfigSupplierTest {

    @Test
    void loadCorrect() {
        MappingConfig externalMappingConfig = new JsonMappingConfigSupplier("src/test/resources/json/mappingconfig.json").get();
        assertEquals(externalMappingConfig.getPackageName(), "com.kobylynskyi.graphql.testconfigjson");
        assertEquals(externalMappingConfig.getGenerateApis(), true);
        assertEquals(externalMappingConfig.getCustomTypesMapping().get("Price.amount"), "java.math.BigDecimal");
        assertNull(externalMappingConfig.getApiPackageName());
    }

    @Test
    void loadNull() {
        assertNull(new JsonMappingConfigSupplier(null).get());
    }

    @Test
    void loadInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new JsonMappingConfigSupplier("blah.json").get());
    }

}