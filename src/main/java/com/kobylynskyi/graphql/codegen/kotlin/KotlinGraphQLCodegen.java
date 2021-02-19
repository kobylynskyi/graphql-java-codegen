package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.GraphQLCodegen;
import com.kobylynskyi.graphql.codegen.MapperFactory;
import com.kobylynskyi.graphql.codegen.model.GeneratedInformation;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedScalarTypeDefinition;
import com.kobylynskyi.graphql.codegen.supplier.MappingConfigSupplier;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * @author 梦境迷离
 * @since 2020/12/09
 */
public class KotlinGraphQLCodegen extends GraphQLCodegen {

    private static final MapperFactory MAPPER_FACTORY = new KotlinMapperFactoryImpl();

    public KotlinGraphQLCodegen(List<String> schemas, File outputDir, MappingConfig mappingConfig, GeneratedInformation generatedInformation) {
        super(schemas, outputDir, mappingConfig, generatedInformation, MAPPER_FACTORY);
    }

    public KotlinGraphQLCodegen(String introspectionResult, File outputDir, MappingConfig mappingConfig, GeneratedInformation generatedInformation) {
        super(introspectionResult, outputDir, mappingConfig, generatedInformation, MAPPER_FACTORY);
    }

    public KotlinGraphQLCodegen(List<String> schemas, String introspectionResult, File outputDir, MappingConfig mappingConfig, MappingConfigSupplier externalMappingConfigSupplier) {
        super(schemas, introspectionResult, outputDir, mappingConfig, externalMappingConfigSupplier, MAPPER_FACTORY);
    }

    public KotlinGraphQLCodegen(List<String> schemas, String introspectionResult, File outputDir, MappingConfig mappingConfig, MappingConfigSupplier externalMappingConfigSupplier, GeneratedInformation generatedInformation) {
        super(schemas, introspectionResult, outputDir, mappingConfig, externalMappingConfigSupplier, generatedInformation, MAPPER_FACTORY);
    }

    @Override
    protected void initDefaultValues(MappingConfig mappingConfig) {
        if (mappingConfig.isGenerateModelOpenClasses() == null) {
            mappingConfig.setGenerateModelOpenClasses(false);
        }
        if (mappingConfig.getGenerateBuilder() == null) {
            // functional expression
            mappingConfig.setGenerateBuilder(false);
        }
        if (mappingConfig.getGenerateImmutableModels() == null) {
            // functional expression
            mappingConfig.setGenerateImmutableModels(true);
        }
        super.initDefaultValues(mappingConfig);
    }

    @Override
    protected void initCustomTypeMappings(Collection<ExtendedScalarTypeDefinition> scalarTypeDefinitions) {
        super.initCustomTypeMappings(scalarTypeDefinitions);
        mappingConfig.putCustomTypeMappingIfAbsent("Int", "Int?");
        mappingConfig.putCustomTypeMappingIfAbsent("Int!", "Int");
        mappingConfig.putCustomTypeMappingIfAbsent("Float", Double.class.getSimpleName() + "?");
        mappingConfig.putCustomTypeMappingIfAbsent("Float!", Double.class.getSimpleName());
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean", Boolean.class.getSimpleName() + "?");
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean!", Boolean.class.getSimpleName());
        mappingConfig.putCustomTypeMappingIfAbsent("String!", String.class.getSimpleName());
        mappingConfig.putCustomTypeMappingIfAbsent("String", String.class.getSimpleName() + "?");
        mappingConfig.putCustomTypeMappingIfAbsent("ID", String.class.getSimpleName() + "?");
        mappingConfig.putCustomTypeMappingIfAbsent("ID!", String.class.getSimpleName());
    }

}
