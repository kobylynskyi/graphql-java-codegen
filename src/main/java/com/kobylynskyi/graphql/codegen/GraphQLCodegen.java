package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.generators.FilesGenerator;
import com.kobylynskyi.graphql.codegen.generators.FilesGeneratorsFactory;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapperFactory;
import com.kobylynskyi.graphql.codegen.mapper.MapperFactory;
import com.kobylynskyi.graphql.codegen.model.GeneratedInformation;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.MappingConfigDefaultValuesInitializer;
import com.kobylynskyi.graphql.codegen.model.MappingConfigValidator;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDocument;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedScalarTypeDefinition;
import com.kobylynskyi.graphql.codegen.parser.GraphQLDocumentParser;
import com.kobylynskyi.graphql.codegen.supplier.MappingConfigSupplier;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.ScalarTypeExtensionDefinition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Generates classes based on GraphQL schema.
 * Extendable for customizing code generation for other JVM languages
 *
 * @author kobylynskyi
 * @author valinhadev
 */
public abstract class GraphQLCodegen {

    protected final MappingConfig mappingConfig;

    private final List<String> schemas;
    private final String introspectionResult;
    private final File outputDir;
    private final GeneratedInformation generatedInformation;
    private final DataModelMapperFactory dataModelMapperFactory;

    // used in tests
    protected GraphQLCodegen(List<String> schemas,
                             File outputDir,
                             MappingConfig mappingConfig,
                             GeneratedInformation generatedInformation,
                             MapperFactory mapperFactory) {
        this(schemas, null, outputDir, mappingConfig, null, generatedInformation, mapperFactory);
    }

    // used in tests
    protected GraphQLCodegen(String introspectionResult,
                             File outputDir,
                             MappingConfig mappingConfig,
                             GeneratedInformation generatedInformation,
                             MapperFactory mapperFactory) {
        this(null, introspectionResult, outputDir, mappingConfig, null, generatedInformation, mapperFactory);
    }

    // used in plugins
    protected GraphQLCodegen(List<String> schemas,
                             String introspectionResult,
                             File outputDir,
                             MappingConfig mappingConfig,
                             MappingConfigSupplier externalMappingConfigSupplier,
                             MapperFactory mapperFactory) {
        this(schemas, introspectionResult, outputDir, mappingConfig, externalMappingConfigSupplier,
                new GeneratedInformation(), mapperFactory);
    }

    // used by other constructors
    protected GraphQLCodegen(List<String> schemas,
                             String introspectionResult,
                             File outputDir,
                             MappingConfig mappingConfig,
                             MappingConfigSupplier externalMappingConfigSupplier,
                             GeneratedInformation generatedInformation,
                             MapperFactory mapperFactory) {
        this.schemas = schemas;
        this.introspectionResult = introspectionResult;
        this.outputDir = outputDir;
        this.generatedInformation = generatedInformation;
        this.dataModelMapperFactory = new DataModelMapperFactory(mapperFactory);
        this.mappingConfig = mappingConfig;
        this.mappingConfig.combine(externalMappingConfigSupplier != null ? externalMappingConfigSupplier.get() : null);

        initDefaultValues(mappingConfig);
        validateConfigs(mappingConfig);
        sanitizeValues(mappingConfig);
    }

    private static void sanitizeValues(MappingConfig mappingConfig) {
        mappingConfig.setModelValidationAnnotation(
                Utils.replaceLeadingAtSign(mappingConfig.getModelValidationAnnotation()));

        if (mappingConfig.getResolverArgumentAnnotations() != null) {
            mappingConfig.setResolverArgumentAnnotations(mappingConfig.getResolverArgumentAnnotations().stream()
                    .map(Utils::replaceLeadingAtSign).collect(Collectors.toSet()));
        }
        if (mappingConfig.getParametrizedResolverAnnotations() != null) {
            mappingConfig.setParametrizedResolverAnnotations(mappingConfig.getParametrizedResolverAnnotations().stream()
                    .map(Utils::replaceLeadingAtSign).collect(Collectors.toSet()));
        }

        Map<String, List<String>> customAnnotationsMapping = mappingConfig.getCustomAnnotationsMapping();
        if (customAnnotationsMapping != null) {
            for (Map.Entry<String, List<String>> entry : customAnnotationsMapping.entrySet()) {
                if (entry.getValue() != null) {
                    entry.setValue(entry.getValue().stream().map(Utils::replaceLeadingAtSign).collect(toList()));
                }
            }
        }
        Map<String, List<String>> directiveAnnotationsMapping = mappingConfig.getDirectiveAnnotationsMapping();
        if (directiveAnnotationsMapping != null) {
            for (Map.Entry<String, List<String>> entry : directiveAnnotationsMapping.entrySet()) {
                if (entry.getValue() != null) {
                    entry.setValue(entry.getValue().stream().map(Utils::replaceLeadingAtSign).collect(toList()));
                }
            }
        }
    }

    protected void initDefaultValues(MappingConfig mappingConfig) {
        MappingConfigDefaultValuesInitializer.initDefaultValues(mappingConfig);
    }

    private void validateConfigs(MappingConfig mappingConfig) {
        MappingConfigValidator.validate(mappingConfig);
    }

    /**
     * Entry point.
     * Generates class files based on GraphQL schema
     *
     * @return a list of generated classes
     * @throws IOException in case some I/O error occurred, e.g.: file can't be created, directory access issues, etc.
     */
    public List<File> generate() throws IOException {
        long startTime = System.currentTimeMillis();

        // prepare output directory
        Utils.deleteDir(outputDir);
        Utils.createDirIfAbsent(outputDir);

        if (!Utils.isEmpty(schemas)) {
            ExtendedDocument document = GraphQLDocumentParser.getDocumentFromSchemas(mappingConfig, schemas);
            List<File> generatedFiles = processDefinitions(document);
            System.out.printf("Finished processing %d schema(s) in %d ms%n", schemas.size(),
                    System.currentTimeMillis() - startTime);
            return generatedFiles;
        } else if (introspectionResult != null) {
            ExtendedDocument document = GraphQLDocumentParser
                    .getDocumentFromIntrospectionResult(mappingConfig, introspectionResult);
            List<File> generatedFiles = processDefinitions(document);
            System.out.printf("Finished processing introspection result in %d ms%n",
                    System.currentTimeMillis() - startTime);
            return generatedFiles;
        } else {
            // either schemas or introspection result should be provided
            throw new IllegalArgumentException(
                    "Either graphql schema path or introspection result path should be supplied");
        }
    }

    private List<File> processDefinitions(ExtendedDocument document) {
        initCustomTypeMappings(document.getScalarDefinitions());
        MappingContext context = MappingContext.builder()
                .setMappingConfig(mappingConfig)
                .setOutputDirectory(outputDir)
                .setGeneratedInformation(generatedInformation)
                .setDataModelMapperFactory(dataModelMapperFactory)
                .setDocument(document)
                .build();

        List<File> generatedFiles = new ArrayList<>();
        for (FilesGenerator generator : FilesGeneratorsFactory.getAll(context, dataModelMapperFactory)) {
            generatedFiles.addAll(generator.generate());
        }
        System.out.printf("Generated %d definition classes in folder %s%n", generatedFiles.size(),
                outputDir.getAbsolutePath());
        return generatedFiles;
    }

    protected void initCustomTypeMappings(Collection<ExtendedScalarTypeDefinition> scalarTypeDefinitions) {
        for (ExtendedScalarTypeDefinition definition : scalarTypeDefinitions) {
            if (definition.getDefinition() != null) {
                mappingConfig.putCustomTypeMappingIfAbsent(definition.getDefinition().getName(), "String");
            }
            for (ScalarTypeExtensionDefinition extension : definition.getExtensions()) {
                mappingConfig.putCustomTypeMappingIfAbsent(extension.getName(), "String");
            }
        }
    }

}
