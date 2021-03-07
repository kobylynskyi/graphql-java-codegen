package com.kobylynskyi.graphql.codegen.supplier;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Retrieve a MappingConfig fro json configuration file.
 *
 * @author valinha
 */
public class JsonMappingConfigSupplier implements MappingConfigSupplier {

    private final String jsonConfigFile;

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
                return Utils.OBJECT_MAPPER.readValue(new File(jsonConfigFile), MappingConfig.class);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return null;
    }
}
