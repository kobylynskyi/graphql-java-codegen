package com.kobylynskyi.graphql.codegen.supplier;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;

/**
 * Retrieve a MappingConfig fro json configuration file.
 *
 * @author valinha
 */
public class JsonMappingConfigSupplier implements MappingConfigSupplier {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String jsonConfigFile;

    /**
     * Instantiates a new Json configuration file supplier.
     *
     * @param jsonConfigFile the json config file
     */
    public JsonMappingConfigSupplier(String jsonConfigFile) {
        this.jsonConfigFile = jsonConfigFile;
    }

    @Override
    public MappingConfig get() {
        if (jsonConfigFile != null && !jsonConfigFile.isEmpty()) {
            try {
                return OBJECT_MAPPER.readValue(new File(jsonConfigFile), MappingConfig.class);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return null;
    }
}
