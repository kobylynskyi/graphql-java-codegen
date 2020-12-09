package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.GraphQLCodegen;
import com.kobylynskyi.graphql.codegen.MapperFactory;
import com.kobylynskyi.graphql.codegen.model.GeneratedInformation;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.MappingConfigConstants;
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
        if (mappingConfig.getGenerateBuilder() == null) {
            // functional expression
            mappingConfig.setGenerateBuilder(false);
        }
        if (mappingConfig.getGenerateImmutableModels() == null) {
            // functional expression
            mappingConfig.setGenerateImmutableModels(true);
        }
        super.initDefaultValues(mappingConfig);
        // a type does not have `?`, then it cannot be null any more.So the annotation is invalid.
        // TODO If it is not the default, we assume that the user forces use the annotation(It doesn't help, though).
        if(mappingConfig.getModelValidationAnnotation().equals(MappingConfigConstants.DEFAULT_VALIDATION_ANNOTATION)){
            mappingConfig.setModelValidationAnnotation(null);
        }
    }

    @Override
    protected void initCustomTypeMappings(Collection<ExtendedScalarTypeDefinition> scalarTypeDefinitions) {
        super.initCustomTypeMappings(scalarTypeDefinitions);
        mappingConfig.putCustomTypeMappingIfAbsent("Int", "Int?");
        mappingConfig.putCustomTypeMappingIfAbsent("Int!", "Int");
        mappingConfig.putCustomTypeMappingIfAbsent("Float", "Double?");
        mappingConfig.putCustomTypeMappingIfAbsent("Float!", "Double");
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean", "Boolean?");
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean!", "Boolean");
        mappingConfig.putCustomTypeMappingIfAbsent("String!", "String");
        mappingConfig.putCustomTypeMappingIfAbsent("String", "String?");
        mappingConfig.putCustomTypeMappingIfAbsent("ID", "String?");
        mappingConfig.putCustomTypeMappingIfAbsent("ID!", "String");
    }

}
