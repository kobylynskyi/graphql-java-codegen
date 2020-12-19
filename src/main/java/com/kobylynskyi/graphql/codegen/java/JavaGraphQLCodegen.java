package com.kobylynskyi.graphql.codegen.java;

import com.kobylynskyi.graphql.codegen.GraphQLCodegen;
import com.kobylynskyi.graphql.codegen.MapperFactory;
import com.kobylynskyi.graphql.codegen.model.GeneratedInformation;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedScalarTypeDefinition;
import com.kobylynskyi.graphql.codegen.supplier.MappingConfigSupplier;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class JavaGraphQLCodegen extends GraphQLCodegen {

    private static final MapperFactory MAPPER_FACTORY = new JavaMapperFactoryImpl();

    public JavaGraphQLCodegen(List<String> schemas, File outputDir, MappingConfig mappingConfig, GeneratedInformation generatedInformation) {
        super(schemas, outputDir, mappingConfig, generatedInformation, MAPPER_FACTORY);
    }

    public JavaGraphQLCodegen(String introspectionResult, File outputDir, MappingConfig mappingConfig, GeneratedInformation generatedInformation) {
        super(introspectionResult, outputDir, mappingConfig, generatedInformation, MAPPER_FACTORY);
    }

    public JavaGraphQLCodegen(List<String> schemas, String introspectionResult, File outputDir, MappingConfig mappingConfig, MappingConfigSupplier externalMappingConfigSupplier) {
        super(schemas, introspectionResult, outputDir, mappingConfig, externalMappingConfigSupplier, MAPPER_FACTORY);
    }

    public JavaGraphQLCodegen(List<String> schemas, String introspectionResult, File outputDir, MappingConfig mappingConfig, MappingConfigSupplier externalMappingConfigSupplier, GeneratedInformation generatedInformation) {
        super(schemas, introspectionResult, outputDir, mappingConfig, externalMappingConfigSupplier, generatedInformation, MAPPER_FACTORY);
    }

    @Override
    protected void initCustomTypeMappings(Collection<ExtendedScalarTypeDefinition> scalarTypeDefinitions) {
        super.initCustomTypeMappings(scalarTypeDefinitions);
        mappingConfig.putCustomTypeMappingIfAbsent("ID", String.class.getSimpleName());
        mappingConfig.putCustomTypeMappingIfAbsent("String", String.class.getSimpleName());
        mappingConfig.putCustomTypeMappingIfAbsent("Int", Integer.class.getSimpleName());
        mappingConfig.putCustomTypeMappingIfAbsent("Int!", "int");
        mappingConfig.putCustomTypeMappingIfAbsent("Float", Double.class.getSimpleName());
        mappingConfig.putCustomTypeMappingIfAbsent("Float!", "double");
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean", Boolean.class.getSimpleName());
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean!", "boolean");
    }

}
