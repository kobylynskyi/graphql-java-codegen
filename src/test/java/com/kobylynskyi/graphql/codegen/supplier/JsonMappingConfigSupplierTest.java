package com.kobylynskyi.graphql.codegen.supplier;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonMappingConfigSupplierTest {

    @Test
    void loadCorrect() {
        MappingConfig externalMappingConfig = new JsonMappingConfigSupplier("src/test/resources/json/mappingconfig.json").get();
        assertEquals("com.kobylynskyi.graphql.testconfigjson", externalMappingConfig.getPackageName());
        assertTrue(externalMappingConfig.getGenerateApis());
        assertEquals("java.math.BigDecimal", externalMappingConfig.getCustomTypesMapping().get("Price.amount"));
        assertNull(externalMappingConfig.getApiPackageName());
    }

    @Test
    void loadNull() {
        assertNull(new JsonMappingConfigSupplier(null).get());
    }

    @Test
    void loadInvalid() {
        JsonMappingConfigSupplier jsonMappingConfigSupplier = new JsonMappingConfigSupplier("blah.json");
        assertThrows(IllegalArgumentException.class, jsonMappingConfigSupplier::get);
    }

}