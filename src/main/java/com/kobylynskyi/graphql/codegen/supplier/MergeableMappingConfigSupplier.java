package com.kobylynskyi.graphql.codegen.supplier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import com.typesafe.config.ConfigException;

import java.util.List;

/**
 * Retrieve a MappingConfig from JSON or HOCON configuration file.
 *
 * @author valinha
 */
public class MergeableMappingConfigSupplier implements MappingConfigSupplier {

    private final String jsonConfig;

    /**
     * Instantiates a new Json configuration file supplier.
     *
     * @param configFiles List of files, either JSON or HOCON.
     */
    public MergeableMappingConfigSupplier(List<String> configFiles) {
        this.jsonConfig = Utils.parseConfigAndMerged(configFiles);
    }

    @Override
    public MappingConfig get() {
        if (jsonConfig != null && !jsonConfig.isEmpty()) {
            try {
                return Utils.OBJECT_MAPPER.readValue(jsonConfig, MappingConfig.class);
            } catch (ConfigException | JsonProcessingException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return null;
    }
}
