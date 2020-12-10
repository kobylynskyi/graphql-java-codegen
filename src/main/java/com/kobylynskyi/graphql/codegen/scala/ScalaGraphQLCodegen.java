package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.GraphQLCodegen;
import com.kobylynskyi.graphql.codegen.MapperFactory;
import com.kobylynskyi.graphql.codegen.model.GeneratedInformation;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedScalarTypeDefinition;
import com.kobylynskyi.graphql.codegen.supplier.MappingConfigSupplier;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class ScalaGraphQLCodegen extends GraphQLCodegen {

    private static final MapperFactory MAPPER_FACTORY = new ScalaMapperFactoryImpl();

    public ScalaGraphQLCodegen(List<String> schemas, File outputDir, MappingConfig mappingConfig, GeneratedInformation generatedInformation) {
        super(schemas, outputDir, mappingConfig, generatedInformation, MAPPER_FACTORY);
    }

    public ScalaGraphQLCodegen(String introspectionResult, File outputDir, MappingConfig mappingConfig, GeneratedInformation generatedInformation) {
        super(introspectionResult, outputDir, mappingConfig, generatedInformation, MAPPER_FACTORY);
    }

    public ScalaGraphQLCodegen(List<String> schemas, String introspectionResult, File outputDir, MappingConfig mappingConfig, MappingConfigSupplier externalMappingConfigSupplier) {
        super(schemas, introspectionResult, outputDir, mappingConfig, externalMappingConfigSupplier, MAPPER_FACTORY);
    }

    public ScalaGraphQLCodegen(List<String> schemas, String introspectionResult, File outputDir, MappingConfig mappingConfig, MappingConfigSupplier externalMappingConfigSupplier, GeneratedInformation generatedInformation) {
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
    }

    @Override
    protected void initCustomTypeMappings(Collection<ExtendedScalarTypeDefinition> scalarTypeDefinitions) {
        super.initCustomTypeMappings(scalarTypeDefinitions);
        mappingConfig.putCustomTypeMappingIfAbsent("ID", "String");
        mappingConfig.putCustomTypeMappingIfAbsent("String", "String");
        mappingConfig.putCustomTypeMappingIfAbsent("Int", "java.lang.Integer");
        mappingConfig.putCustomTypeMappingIfAbsent("Int!", "Int");
        mappingConfig.putCustomTypeMappingIfAbsent("Float", "java.lang.Double");
        mappingConfig.putCustomTypeMappingIfAbsent("Float!", "Double");
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean", "java.lang.Boolean");
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean!", "Boolean");
    }

}
