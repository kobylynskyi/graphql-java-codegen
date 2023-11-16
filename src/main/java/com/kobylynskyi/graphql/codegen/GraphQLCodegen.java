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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                new GeneratedInformation(mappingConfig), mapperFactory);
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
        sanitize(mappingConfig);
    }

    private static void sanitize(MappingConfig mappingConfig) {
        mappingConfig.setModelValidationAnnotation(
                Utils.replaceLeadingAtSign(mappingConfig.getModelValidationAnnotation()));
        mappingConfig.setResolverArgumentAnnotations(sanitizeSet(mappingConfig.getResolverArgumentAnnotations()));
        mappingConfig.setParametrizedResolverAnnotations(
                sanitizeSet(mappingConfig.getParametrizedResolverAnnotations()));
        mappingConfig.setCustomAnnotationsMapping(sanitizeMap(mappingConfig.getCustomAnnotationsMapping()));
        mappingConfig.setDirectiveAnnotationsMapping(sanitizeMap(mappingConfig.getDirectiveAnnotationsMapping()));
        if (mappingConfig.getCustomTypesMapping() != null) {
            mappingConfig.setCustomTypesMapping(new HashMap<>(mappingConfig.getCustomTypesMapping()));
        }
        if (mappingConfig.getCustomTemplates() != null) {
            mappingConfig.setCustomTemplates(new HashMap<>(mappingConfig.getCustomTemplates()));
        }
    }

    private static Set<String> sanitizeSet(Set<String> originalSet) {
        if (originalSet == null) {
            return new HashSet<>();
        }
        return originalSet.stream().map(Utils::replaceLeadingAtSign).collect(Collectors.toSet());
    }

    private static Map<String, List<String>> sanitizeMap(Map<String, List<String>> multiValueMap) {
        if (multiValueMap == null) {
            return new HashMap<>();
        }
        Map<String, List<String>> sanitizedMultiValueMap = new HashMap<>(multiValueMap.size());
        for (Map.Entry<String, List<String>> entry : multiValueMap.entrySet()) {
            List<String> sanitizedValues = null;
            if (entry.getValue() != null) {
                sanitizedValues = entry.getValue().stream().map(Utils::replaceLeadingAtSign).collect(toList());
            }
            sanitizedMultiValueMap.put(entry.getKey(), sanitizedValues);
        }
        return sanitizedMultiValueMap;
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
            return processDefinitions(document, schemas.size() + " schema(s)", startTime);
        } else if (introspectionResult != null) {
            ExtendedDocument document = GraphQLDocumentParser
                    .getDocumentFromIntrospectionResult(mappingConfig, introspectionResult);
            return processDefinitions(document, "introspection result", startTime);
        } else {
            // either schemas or introspection result should be provided
            throw new IllegalArgumentException(
                    "Either graphql schema path or introspection result path should be supplied");
        }
    }

    private List<File> processDefinitions(ExtendedDocument document, String source, long startTime) {
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
        printOutputResult(source, generatedFiles.size(), System.currentTimeMillis() - startTime);
        return generatedFiles;
    }

    private void printOutputResult(String source, int classesGenerated, long duration) {
        System.out.printf("Generated %d classes from %s in folder %s, took %d ms%n",
                classesGenerated, source, outputDir.getAbsolutePath(), duration);
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
