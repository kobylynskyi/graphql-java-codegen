package com.kobylynskyi.graphql.codegen.supplier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

import java.io.File;
import java.util.List;

/**
 * Retrieve a MappingConfig from JSON or HOCON configuration file.
 *
 * @author valinha
 */
public class MergeableMappingConfigSupplier implements MappingConfigSupplier {

    private static final ConfigRenderOptions configRenderOptions = ConfigRenderOptions.concise();

    private final String jsonConfig;

    /**
     * Instantiates a new Json configuration file supplier.
     *
     * @param configFiles List of files, either JSON or HOCON.
     */
    public MergeableMappingConfigSupplier(List<String> configFiles) {
        this.jsonConfig = parseConfigAndMerged(configFiles);
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


    /**
     * parser list of config files.
     *
     * @param confFiles List of files, either JSON or HOCON.
     * @return The string of the configuration after merging.
     */
    private static String parseConfigAndMerged(List<String> confFiles) {
        try {
            if (confFiles == null || confFiles.isEmpty()) {
                return null;
            }

            return confFiles.stream()
                    .map(c -> ConfigFactory.parseFile(new File(c)))
                    .reduce(Config::withFallback)
                    .map(value -> value.root().render(configRenderOptions))
                    .orElse(null);
        } catch (ConfigException ce) {
            return null;
        }
    }
}
