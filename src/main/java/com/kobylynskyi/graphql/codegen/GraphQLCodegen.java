package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.mapper.DataModelMapperFactory;
import com.kobylynskyi.graphql.codegen.mapper.FieldDefinitionToParameterMapper;
import com.kobylynskyi.graphql.codegen.model.ApiInterfaceStrategy;
import com.kobylynskyi.graphql.codegen.model.ApiNamePrefixStrategy;
import com.kobylynskyi.graphql.codegen.model.ApiRootInterfaceStrategy;
import com.kobylynskyi.graphql.codegen.model.GeneratedInformation;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.MappingConfigConstants;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDocument;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedEnumTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInputObjectTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInterfaceTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedScalarTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedUnionTypeDefinition;
import com.kobylynskyi.graphql.codegen.supplier.MappingConfigSupplier;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.FieldDefinition;
import graphql.language.ScalarTypeExtensionDefinition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        this(schemas, introspectionResult, outputDir, mappingConfig, externalMappingConfigSupplier, new GeneratedInformation(), mapperFactory);
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
        this.mappingConfig = mappingConfig;
        this.mappingConfig.combine(externalMappingConfigSupplier != null ? externalMappingConfigSupplier.get() : null);
        this.generatedInformation = generatedInformation;
        this.dataModelMapperFactory = new DataModelMapperFactory(mapperFactory);

        initDefaultValues(mappingConfig);
        validateConfigs(mappingConfig);
        sanitizeValues(mappingConfig);
    }

    private static void sanitizeValues(MappingConfig mappingConfig) {
        mappingConfig.setModelValidationAnnotation(
                Utils.replaceLeadingAtSign(mappingConfig.getModelValidationAnnotation()));

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
        if (mappingConfig.getModelValidationAnnotation() == null) {
            mappingConfig.setModelValidationAnnotation(MappingConfigConstants.DEFAULT_VALIDATION_ANNOTATION);
        }
        if (mappingConfig.getGenerateBuilder() == null) {
            mappingConfig.setGenerateBuilder(MappingConfigConstants.DEFAULT_BUILDER);
        }
        if (mappingConfig.getGenerateEqualsAndHashCode() == null) {
            mappingConfig.setGenerateEqualsAndHashCode(MappingConfigConstants.DEFAULT_EQUALS_AND_HASHCODE);
        }
        if (mappingConfig.getGenerateClient() == null) {
            mappingConfig.setGenerateClient(MappingConfigConstants.DEFAULT_GENERATE_CLIENT);
        }
        if (mappingConfig.getRequestSuffix() == null) {
            mappingConfig.setRequestSuffix(MappingConfigConstants.DEFAULT_REQUEST_SUFFIX);
        }
        if (mappingConfig.getResponseSuffix() == null) {
            mappingConfig.setResponseSuffix(MappingConfigConstants.DEFAULT_RESPONSE_SUFFIX);
        }
        if (mappingConfig.getResponseProjectionSuffix() == null) {
            mappingConfig.setResponseProjectionSuffix(MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_SUFFIX);
        }
        if (mappingConfig.getParametrizedInputSuffix() == null) {
            mappingConfig.setParametrizedInputSuffix(MappingConfigConstants.DEFAULT_PARAMETRIZED_INPUT_SUFFIX);
        }
        if (mappingConfig.getGenerateImmutableModels() == null) {
            mappingConfig.setGenerateImmutableModels(MappingConfigConstants.DEFAULT_GENERATE_IMMUTABLE_MODELS);
        }
        if (mappingConfig.getGenerateToString() == null) {
            mappingConfig.setGenerateToString(MappingConfigConstants.DEFAULT_TO_STRING);
        }
        if (mappingConfig.getGenerateApis() == null) {
            mappingConfig.setGenerateApis(MappingConfigConstants.DEFAULT_GENERATE_APIS);
        }
        if (mappingConfig.getApiNameSuffix() == null) {
            mappingConfig.setApiNameSuffix(MappingConfigConstants.DEFAULT_RESOLVER_SUFFIX);
        }
        if (mappingConfig.getTypeResolverSuffix() == null) {
            mappingConfig.setTypeResolverSuffix(MappingConfigConstants.DEFAULT_RESOLVER_SUFFIX);
        }
        if (mappingConfig.getGenerateParameterizedFieldsResolvers() == null) {
            mappingConfig.setGenerateParameterizedFieldsResolvers(MappingConfigConstants.DEFAULT_GENERATE_PARAMETERIZED_FIELDS_RESOLVERS);
        }
        if (mappingConfig.getGenerateExtensionFieldsResolvers() == null) {
            mappingConfig.setGenerateExtensionFieldsResolvers(MappingConfigConstants.DEFAULT_GENERATE_EXTENSION_FIELDS_RESOLVERS);
        }
        if (mappingConfig.getGenerateDataFetchingEnvironmentArgumentInApis() == null) {
            mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(MappingConfigConstants.DEFAULT_GENERATE_DATA_FETCHING_ENV);
        }
        if (mappingConfig.getGenerateModelsForRootTypes() == null) {
            mappingConfig.setGenerateModelsForRootTypes(MappingConfigConstants.DEFAULT_GENERATE_MODELS_FOR_ROOT_TYPES);
        }
        if (mappingConfig.getGenerateApisWithThrowsException() == null) {
            mappingConfig.setGenerateApisWithThrowsException(MappingConfigConstants.DEFAULT_GENERATE_APIS_WITH_THROWS_EXCEPTION);
        }
        if (mappingConfig.getAddGeneratedAnnotation() == null) {
            mappingConfig.setAddGeneratedAnnotation(MappingConfigConstants.DEFAULT_ADD_GENERATED_ANNOTATION);
        }
        if (mappingConfig.getUseOptionalForNullableReturnTypes() == null) {
            mappingConfig.setUseOptionalForNullableReturnTypes(MappingConfigConstants.DEFAULT_USE_OPTIONAL_FOR_NULLABLE_RETURN_TYPES);
        }
        if (mappingConfig.getApiNamePrefixStrategy() == null) {
            mappingConfig.setApiNamePrefixStrategy(MappingConfigConstants.DEFAULT_API_NAME_PREFIX_STRATEGY);
        }
        if (mappingConfig.getApiRootInterfaceStrategy() == null) {
            mappingConfig.setApiRootInterfaceStrategy(MappingConfigConstants.DEFAULT_API_ROOT_INTERFACE_STRATEGY);
        }
        if (mappingConfig.getApiInterfaceStrategy() == null) {
            mappingConfig.setApiInterfaceStrategy(MappingConfigConstants.DEFAULT_API_INTERFACE_STRATEGY);
        }
        if (Boolean.TRUE.equals(mappingConfig.getGenerateClient())) {
            // required for request serialization
            mappingConfig.setGenerateToString(true);
        }
        if (mappingConfig.getResponseProjectionMaxDepth() == null) {
            mappingConfig.setResponseProjectionMaxDepth(MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_MAX_DEPTH);
        }
        if (mappingConfig.getGeneratedLanguage() == null) {
            mappingConfig.setGeneratedLanguage(MappingConfigConstants.DEFAULT_GENERATED_LANGUAGE);
        }
    }

    private void validateConfigs(MappingConfig mappingConfig) {
        if (!Utils.isEmpty(schemas) && introspectionResult != null ||
                (Utils.isEmpty(schemas) && introspectionResult == null)) {
            // either schemas or introspection result should be provided
            throw new IllegalArgumentException("Either graphql schema path or introspection result path should be supplied");
        }
        if (mappingConfig.getApiRootInterfaceStrategy() == ApiRootInterfaceStrategy.INTERFACE_PER_SCHEMA &&
                mappingConfig.getApiNamePrefixStrategy() == ApiNamePrefixStrategy.CONSTANT) {
            // we will have a conflict in case there is "type Query" in multiple graphql schema files
            throw new IllegalArgumentException("API prefix should not be CONSTANT for INTERFACE_PER_SCHEMA option");
        }
        if (Boolean.TRUE.equals(mappingConfig.getGenerateApis())
                && Boolean.TRUE.equals(mappingConfig.getGenerateModelsForRootTypes())
                && mappingConfig.getApiNamePrefixStrategy() == ApiNamePrefixStrategy.CONSTANT) {
            // checking for conflict between root type model classes and api interfaces
            if (Utils.stringsEqualIgnoreSpaces(mappingConfig.getApiNamePrefix(), mappingConfig.getModelNamePrefix()) &&
                    Utils.stringsEqualIgnoreSpaces(mappingConfig.getApiNameSuffix(), mappingConfig.getModelNameSuffix())) {
                // we will have a conflict between model pojo (Query.java) and api interface (Query.java)
                throw new IllegalArgumentException("Either disable APIs generation or set different Prefix/Suffix for API classes and model classes");
            }
            // checking for conflict between root type model resolver classes and api interfaces
            if (Utils.stringsEqualIgnoreSpaces(mappingConfig.getApiNamePrefix(), mappingConfig.getTypeResolverPrefix()) &&
                    Utils.stringsEqualIgnoreSpaces(mappingConfig.getApiNameSuffix(), mappingConfig.getTypeResolverSuffix())) {
                // we will have a conflict between model resolver interface (QueryResolver.java) and api interface resolver (QueryResolver.java)
                throw new IllegalArgumentException("Either disable APIs generation or set different Prefix/Suffix for API classes and type resolver classes");
            }
        }
    }

    public List<File> generate() throws IOException {
        GraphQLCodegenFileCreator.prepareOutputDir(outputDir);
        long startTime = System.currentTimeMillis();
        List<File> generatedFiles = Collections.emptyList();
        if (!Utils.isEmpty(schemas)) {
            ExtendedDocument document = GraphQLDocumentParser.getDocumentFromSchemas(mappingConfig, schemas);
            initCustomTypeMappings(document.getScalarDefinitions());
            generatedFiles = processDefinitions(document);
            System.out.printf("Finished processing %d schema(s) in %d ms%n", schemas.size(),
                    System.currentTimeMillis() - startTime);
        } else if (introspectionResult != null) {
            ExtendedDocument document = GraphQLDocumentParser.getDocumentFromIntrospectionResult(mappingConfig, introspectionResult);
            initCustomTypeMappings(document.getScalarDefinitions());
            generatedFiles = processDefinitions(document);
            System.out.printf("Finished processing introspection result in %d ms%n",
                    System.currentTimeMillis() - startTime);
        }

        return generatedFiles;
    }

    private List<File> processDefinitions(ExtendedDocument document) {
        MappingContext context = new MappingContext(mappingConfig, document, generatedInformation, dataModelMapperFactory);
        List<File> generatedFiles = new ArrayList<>();
        for (ExtendedEnumTypeDefinition extendedEnumTypeDefinition : document.getEnumDefinitions()) {
            generatedFiles.add(generateEnum(context, extendedEnumTypeDefinition));
        }
        for (ExtendedInterfaceTypeDefinition extendedInterfaceTypeDefinition : document.getInterfaceDefinitions()) {
            generatedFiles.addAll(generateInterface(context, extendedInterfaceTypeDefinition));
        }
        for (ExtendedObjectTypeDefinition extendedObjectTypeDefinition : document.getTypeDefinitions()) {
            generatedFiles.addAll(generateType(context, extendedObjectTypeDefinition));
        }
        for (ExtendedObjectTypeDefinition extendedObjectTypeDefinition : document.getTypeDefinitions()) {
            generateFieldResolver(context, extendedObjectTypeDefinition.getFieldDefinitions(), extendedObjectTypeDefinition)
                    .ifPresent(generatedFiles::add);
        }
        for (ExtendedObjectTypeDefinition extendedObjectTypeDefinition : document.getOperationDefinitions()) {
            if (Boolean.TRUE.equals(mappingConfig.getGenerateApis())) {
                generatedFiles.addAll(generateServerOperations(context, extendedObjectTypeDefinition));
            }
            if (Boolean.TRUE.equals(mappingConfig.getGenerateClient())) {
                generatedFiles.addAll(generateClient(context, extendedObjectTypeDefinition));
            }
        }
        for (ExtendedInputObjectTypeDefinition extendedInputObjectTypeDefinition : document.getInputDefinitions()) {
            generatedFiles.add(generateInput(context, extendedInputObjectTypeDefinition));
        }
        for (ExtendedUnionTypeDefinition extendedUnionTypeDefinition : document.getUnionDefinitions()) {
            generatedFiles.addAll(generateUnion(context, extendedUnionTypeDefinition));
        }
        for (ExtendedInterfaceTypeDefinition definition : document.getInterfaceDefinitions()) {
            generateFieldResolver(context, definition.getFieldDefinitions(), definition).ifPresent(generatedFiles::add);
        }
        System.out.printf("Generated %d definition classes in folder %s%n", generatedFiles.size(), outputDir.getAbsolutePath());
        return generatedFiles;
    }

    private List<File> generateUnion(MappingContext mappingContext, ExtendedUnionTypeDefinition definition) {
        List<File> generatedFiles = new ArrayList<>();
        Map<String, Object> dataModel = dataModelMapperFactory.getUnionDefinitionMapper().map(mappingContext, definition);
        generatedFiles.add(GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.UNION, dataModel, outputDir));

        if (Boolean.TRUE.equals(mappingConfig.getGenerateClient())) {
            Map<String, Object> responseProjDataModel = dataModelMapperFactory.getRequestResponseDefinitionMapper().mapResponseProjection(mappingContext, definition);
            generatedFiles.add(GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.RESPONSE_PROJECTION, responseProjDataModel, outputDir));
        }
        return generatedFiles;
    }

    private List<File> generateInterface(MappingContext mappingContext, ExtendedInterfaceTypeDefinition definition) {
        List<File> generatedFiles = new ArrayList<>();
        Map<String, Object> dataModel = dataModelMapperFactory.getInterfaceDefinitionMapper().map(mappingContext, definition);
        generatedFiles.add(GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.INTERFACE, dataModel, outputDir));

        if (Boolean.TRUE.equals(mappingConfig.getGenerateClient())) {
            Map<String, Object> responseProjDataModel = dataModelMapperFactory.getRequestResponseDefinitionMapper().mapResponseProjection(mappingContext, definition);
            generatedFiles.add(GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.RESPONSE_PROJECTION, responseProjDataModel, outputDir));

            for (ExtendedFieldDefinition fieldDefinition : definition.getFieldDefinitions()) {
                if (!Utils.isEmpty(fieldDefinition.getInputValueDefinitions())) {
                    Map<String, Object> fieldProjDataModel = dataModelMapperFactory.getRequestResponseDefinitionMapper().mapParametrizedInput(mappingContext, fieldDefinition, definition);
                    generatedFiles.add(GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.PARAMETRIZED_INPUT, fieldProjDataModel, outputDir));
                }
            }
        }
        return generatedFiles;
    }

    private List<File> generateServerOperations(MappingContext mappingContext, ExtendedObjectTypeDefinition definition) {
        List<File> generatedFiles = new ArrayList<>();
        // Generate a root interface with all operations inside
        // Relates to https://github.com/facebook/relay/issues/112
        switch (mappingContext.getApiRootInterfaceStrategy()) {
            case INTERFACE_PER_SCHEMA:
                for (ExtendedObjectTypeDefinition defInFile : definition.groupBySourceLocationFile().values()) {
                    generatedFiles.add(generateRootApi(mappingContext, defInFile));
                }
                break;
            case DO_NOT_GENERATE:
                break;
            case SINGLE_INTERFACE:
            default:
                generatedFiles.add(generateRootApi(mappingContext, definition));
                break;
        }

        if (mappingContext.getApiInterfaceStrategy() == ApiInterfaceStrategy.INTERFACE_PER_OPERATION) {
            // Generate separate interfaces for all queries, mutations and subscriptions
            List<String> fieldNames = definition.getFieldDefinitions().stream().map(FieldDefinition::getName).collect(toList());
            switch (mappingContext.getApiNamePrefixStrategy()) {
                case FOLDER_NAME_AS_PREFIX:
                    for (ExtendedObjectTypeDefinition fileDef : definition.groupBySourceLocationFolder().values()) {
                        generatedFiles.addAll(generateApis(mappingContext, fileDef, fieldNames));
                    }
                    break;
                case FILE_NAME_AS_PREFIX:
                    for (ExtendedObjectTypeDefinition fileDef : definition.groupBySourceLocationFile().values()) {
                        generatedFiles.addAll(generateApis(mappingContext, fileDef, fieldNames));
                    }
                    break;
                case CONSTANT:
                default:
                    generatedFiles.addAll(generateApis(mappingContext, definition, fieldNames));
                    break;
            }
        }
        return generatedFiles;
    }

    private List<File> generateClient(MappingContext mappingContext, ExtendedObjectTypeDefinition definition) {
        List<File> generatedFiles = new ArrayList<>();
        List<String> fieldNames = definition.getFieldDefinitions().stream().map(FieldDefinition::getName).collect(toList());
        for (ExtendedFieldDefinition operationDef : definition.getFieldDefinitions()) {
            Map<String, Object> requestDataModel = dataModelMapperFactory.getRequestResponseDefinitionMapper().mapRequest(mappingContext, operationDef, definition.getName(), fieldNames);
            generatedFiles.add(GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.REQUEST, requestDataModel, outputDir));

            Map<String, Object> responseDataModel = dataModelMapperFactory.getRequestResponseDefinitionMapper().mapResponse(mappingContext, operationDef, definition.getName(), fieldNames);
            generatedFiles.add(GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.RESPONSE, responseDataModel, outputDir));
        }
        return generatedFiles;
    }

    private List<File> generateApis(MappingContext mappingContext, ExtendedObjectTypeDefinition definition, List<String> fieldNames) {
        List<File> generatedFiles = new ArrayList<>();
        for (ExtendedFieldDefinition operationDef : definition.getFieldDefinitions()) {
            Map<String, Object> dataModel = dataModelMapperFactory.getFieldDefinitionsToResolverMapper().mapRootTypeField(mappingContext, operationDef, definition.getName(), fieldNames);
            generatedFiles.add(GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.OPERATIONS, dataModel, outputDir));
        }
        return generatedFiles;
    }

    private File generateRootApi(MappingContext mappingContext, ExtendedObjectTypeDefinition definition) {
        Map<String, Object> dataModel = dataModelMapperFactory.getFieldDefinitionsToResolverMapper().mapRootTypeFields(mappingContext, definition);
        return GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.OPERATIONS, dataModel, outputDir);
    }

    private List<File> generateType(MappingContext mappingContext, ExtendedObjectTypeDefinition definition) {
        List<File> generatedFiles = new ArrayList<>();
        Map<String, Object> dataModel = dataModelMapperFactory.getTypeDefinitionMapper().map(mappingContext, definition);
        generatedFiles.add(GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.TYPE, dataModel, outputDir));

        if (Boolean.TRUE.equals(mappingConfig.getGenerateClient())) {
            Map<String, Object> responseProjDataModel = dataModelMapperFactory.getRequestResponseDefinitionMapper().mapResponseProjection(mappingContext, definition);
            generatedFiles.add(GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.RESPONSE_PROJECTION, responseProjDataModel, outputDir));

            for (ExtendedFieldDefinition fieldDefinition : definition.getFieldDefinitions()) {
                if (!Utils.isEmpty(fieldDefinition.getInputValueDefinitions())) {
                    Map<String, Object> fieldProjDataModel = dataModelMapperFactory.getRequestResponseDefinitionMapper().mapParametrizedInput(mappingContext, fieldDefinition, definition);
                    generatedFiles.add(GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.PARAMETRIZED_INPUT, fieldProjDataModel, outputDir));
                }
            }
        }
        return generatedFiles;
    }

    private Optional<File> generateFieldResolver(MappingContext mappingContext,
                                                 List<ExtendedFieldDefinition> fieldDefinitions,
                                                 ExtendedDefinition<?, ?> parentDefinition) {
        if (Boolean.TRUE.equals(mappingConfig.getGenerateApis())) {
            List<ExtendedFieldDefinition> fieldDefsWithResolvers = fieldDefinitions.stream()
                    .filter(fieldDef -> FieldDefinitionToParameterMapper.generateResolversForField(mappingContext, fieldDef, parentDefinition))
                    .collect(toList());
            if (!fieldDefsWithResolvers.isEmpty()) {
                Map<String, Object> dataModel = dataModelMapperFactory.getFieldDefinitionsToResolverMapper().mapToTypeResolver(mappingContext, fieldDefsWithResolvers, parentDefinition.getName());
                return Optional.of(GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.OPERATIONS, dataModel, outputDir));
            }
        }
        return Optional.empty();
    }

    private File generateInput(MappingContext mappingContext, ExtendedInputObjectTypeDefinition definition) {
        Map<String, Object> dataModel = dataModelMapperFactory.getInputDefinitionMapper().map(mappingContext, definition);
        return GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.TYPE, dataModel, outputDir);
    }

    private File generateEnum(MappingContext mappingContext, ExtendedEnumTypeDefinition definition) {
        Map<String, Object> dataModel = dataModelMapperFactory.getEnumDefinitionMapper().map(mappingContext, definition);
        return GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.ENUM, dataModel, outputDir);
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
