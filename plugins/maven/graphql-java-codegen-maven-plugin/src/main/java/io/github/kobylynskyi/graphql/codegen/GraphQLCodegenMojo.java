package io.github.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.GraphQLCodegen;
import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.kotlin.KotlinGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.ApiInterfaceStrategy;
import com.kobylynskyi.graphql.codegen.model.ApiNamePrefixStrategy;
import com.kobylynskyi.graphql.codegen.model.ApiRootInterfaceStrategy;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.GraphQLCodegenConfiguration;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.MappingConfigConstants;
import com.kobylynskyi.graphql.codegen.model.RelayConfig;
import com.kobylynskyi.graphql.codegen.model.exception.LanguageNotSupportedException;
import com.kobylynskyi.graphql.codegen.scala.ScalaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.supplier.MappingConfigSupplier;
import com.kobylynskyi.graphql.codegen.supplier.MergeableMappingConfigSupplier;
import com.kobylynskyi.graphql.codegen.supplier.SchemaFinder;
import graphql.parser.ParserOptions;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;

/**
 * GraphQL Codegen MOJO implementation
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class GraphQLCodegenMojo extends AbstractMojo implements GraphQLCodegenConfiguration {

    @Parameter
    private String[] graphqlSchemaPaths;

    @Parameter
    private String graphqlQueryIntrospectionResultPath;

    @Parameter
    private SchemaFinderConfig graphqlSchemas = new SchemaFinderConfig();

    @Parameter(required = true)
    private File outputDir;

    @Parameter
    private Properties customTypesMapping = new Properties();

    @Parameter
    private Map<String, Properties> customAnnotationsMapping;

    @Parameter
    private File customTemplatesRoot;

    @Parameter
    private Map<String, String> customTemplates;

    @Parameter
    private Map<String, Properties> directiveAnnotationsMapping;

    @Parameter
    private String packageName;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_BUILDER_STRING)
    private boolean generateBuilder;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_APIS_STRING)
    private boolean generateApis;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_EQUALS_AND_HASHCODE_STRING)
    private boolean generateEqualsAndHashCode;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_IMMUTABLE_MODELS_STRING)
    private boolean generateImmutableModels;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_TO_STRING_STRING)
    private boolean generateToString;

    @Parameter
    private String apiPackageName;

    @Parameter
    private String apiNamePrefix;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_RESOLVER_SUFFIX)
    private String apiNameSuffix;

    @Parameter
    private String modelPackageName;

    @Parameter
    private String modelNamePrefix;

    @Parameter
    private String modelNameSuffix;

    @Parameter
    private String typeResolverPrefix;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_RESOLVER_SUFFIX)
    private String typeResolverSuffix;

    @Parameter
    private String apiReturnType;

    @Parameter
    private String apiReturnListType;

    @Parameter
    private String subscriptionReturnType;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_API_ROOT_INTERFACE_STRATEGY_STRING)
    private ApiRootInterfaceStrategy apiRootInterfaceStrategy;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_API_INTERFACE_STRATEGY_STRING)
    private ApiInterfaceStrategy apiInterfaceStrategy;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_API_NAME_PREFIX_STRATEGY_STRING)
    private ApiNamePrefixStrategy apiNamePrefixStrategy;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_VALIDATION_ANNOTATION)
    private String modelValidationAnnotation;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_PARAMETERIZED_FIELDS_RESOLVERS_STRING)
    private boolean generateParameterizedFieldsResolvers;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_EXTENSION_FIELDS_RESOLVERS_STRING)
    private boolean generateExtensionFieldsResolvers;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_DATA_FETCHING_ENV_STRING)
    private boolean generateDataFetchingEnvironmentArgumentInApis;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_MODELS_FOR_ROOT_TYPES_STRING)
    private boolean generateModelsForRootTypes;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_USE_OPTIONAL_FOR_NULLABLE_RETURN_TYPES_STRING)
    private boolean useOptionalForNullableReturnTypes;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_APIS_WITH_THROWS_EXCEPTION_STRING)
    private boolean generateApisWithThrowsException;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_APIS_WITH_SUSPEND_FUNCTIONS_STRING)
    private boolean generateApisWithSuspendFunctions;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_JACKSON_TYPE_ID_RESOLVER_STRING)
    private boolean generateJacksonTypeIdResolver;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_ADD_GENERATED_ANNOTATION_STRING)
    private boolean addGeneratedAnnotation;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_NOARGS_CONSTRUCTOR_ONLY_STRING)
    private boolean generateNoArgsConstructorOnly;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_MODELS_WITH_PUBLIC_FIELDS_STRING)
    private boolean generateModelsWithPublicFields;

    @Parameter
    private String generatedAnnotation;

    @Parameter
    private String[] fieldsWithResolvers;

    @Parameter
    private String[] fieldsWithoutResolvers;

    @Parameter
    private String[] fieldsToExcludeFromGeneration;

    @Parameter
    private String[] fieldsWithDataFetcherResult;

    @Parameter
    private RelayConfig relayConfig = new RelayConfig();

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_CLIENT_STRING)
    private boolean generateClient;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_REQUEST_SUFFIX)
    private String requestSuffix;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_RESPONSE_SUFFIX)
    private String responseSuffix;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_SUFFIX)
    private String responseProjectionSuffix;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_PARAMETRIZED_INPUT_SUFFIX)
    private String parametrizedInputSuffix;

    @Parameter
    private String[] useObjectMapperForRequestSerialization;

    @Parameter
    private String[] typesAsInterfaces;

    @Parameter
    private String[] resolverArgumentAnnotations;

    @Parameter
    private String[] parametrizedResolverAnnotations;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_MAX_DEPTH_STRING)
    private int responseProjectionMaxDepth;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_ALL_METHOD_STRING)
    private boolean generateAllMethodInProjection;

    @Parameter
    private ParentInterfacesConfig parentInterfaces = new ParentInterfacesConfig();

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATED_LANGUAGE_STRING)
    private GeneratedLanguage generatedLanguage;

    @Parameter
    private String[] configurationFiles;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_SUPPORT_UNKNOWN_FIELDS_STRING)
    private boolean supportUnknownFields;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_UNKNOWN_FIELDS_PROPERTY_NAME)
    private String unknownFieldsPropertyName;

    /**
     * The project being built.
     */
    @Parameter(readonly = true, required = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_MODEL_OPEN_CLASSES_STRING)
    private boolean generateModelOpenClasses;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_INITIALIZE_NULLABLE_TYPES_STRING)
    private boolean initializeNullableTypes;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_SEALED_INTERFACES_STRING)
    private boolean generateSealedInterfaces;

    @Parameter(defaultValue = "false")
    private boolean skip;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_SKIP_SCHEMA_SIZE_LIMIT_STRING)
    private boolean skipSchemaSizeLimit;

    @Override
    public void execute() throws MojoExecutionException {
        addCompileSourceRootIfConfigured();

        MappingConfig mappingConfig = new MappingConfig();
        mappingConfig.setPackageName(packageName);
        mappingConfig.setCustomTypesMapping(convertToMap(customTypesMapping));
        mappingConfig.setCustomTemplatesRoot(customTemplatesRoot);
        mappingConfig.setCustomTemplates(customTemplates);
        mappingConfig.setCustomAnnotationsMapping(convertToListsMap(customAnnotationsMapping));
        mappingConfig.setDirectiveAnnotationsMapping(convertToListsMap(directiveAnnotationsMapping));
        mappingConfig.setApiNameSuffix(apiNameSuffix);
        mappingConfig.setApiNamePrefix(apiNamePrefix);
        mappingConfig.setApiRootInterfaceStrategy(apiRootInterfaceStrategy);
        mappingConfig.setApiInterfaceStrategy(apiInterfaceStrategy);
        mappingConfig.setApiNamePrefixStrategy(apiNamePrefixStrategy);
        mappingConfig.setModelNamePrefix(modelNamePrefix);
        mappingConfig.setModelNameSuffix(modelNameSuffix);
        mappingConfig.setApiPackageName(apiPackageName);
        mappingConfig.setModelPackageName(modelPackageName);
        mappingConfig.setGenerateBuilder(generateBuilder);
        mappingConfig.setGenerateApis(generateApis);
        mappingConfig.setTypeResolverSuffix(typeResolverSuffix);
        mappingConfig.setTypeResolverPrefix(typeResolverPrefix);
        mappingConfig.setModelValidationAnnotation(modelValidationAnnotation);
        mappingConfig.setGenerateEqualsAndHashCode(generateEqualsAndHashCode);
        mappingConfig.setGenerateImmutableModels(generateImmutableModels);
        mappingConfig.setGenerateToString(generateToString);
        mappingConfig.setApiReturnType(apiReturnType);
        mappingConfig.setApiReturnListType(apiReturnListType);
        mappingConfig.setSubscriptionReturnType(subscriptionReturnType);
        mappingConfig.setGenerateParameterizedFieldsResolvers(generateParameterizedFieldsResolvers);
        mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(generateDataFetchingEnvironmentArgumentInApis);
        mappingConfig.setGenerateExtensionFieldsResolvers(generateExtensionFieldsResolvers);
        mappingConfig.setGenerateModelsForRootTypes(generateModelsForRootTypes);
        mappingConfig.setUseOptionalForNullableReturnTypes(useOptionalForNullableReturnTypes);
        mappingConfig.setGenerateApisWithThrowsException(generateApisWithThrowsException);
        mappingConfig.setGenerateApisWithSuspendFunctions(generateApisWithSuspendFunctions);
        mappingConfig.setGenerateJacksonTypeIdResolver(generateJacksonTypeIdResolver);
        mappingConfig.setAddGeneratedAnnotation(addGeneratedAnnotation);
        mappingConfig.setGeneratedAnnotation(generatedAnnotation);
        mappingConfig.setGenerateNoArgsConstructorOnly(generateNoArgsConstructorOnly);
        mappingConfig.setGenerateModelsWithPublicFields(generateModelsWithPublicFields);
        mappingConfig.setFieldsWithResolvers(mapToHashSet(fieldsWithResolvers));
        mappingConfig.setFieldsWithoutResolvers(mapToHashSet(fieldsWithoutResolvers));
        mappingConfig.setFieldsToExcludeFromGeneration(mapToHashSet(fieldsToExcludeFromGeneration));
        mappingConfig.setFieldsWithDataFetcherResult(mapToHashSet(fieldsWithDataFetcherResult));
        mappingConfig.setRelayConfig(relayConfig);

        mappingConfig.setGenerateClient(generateClient);
        mappingConfig.setRequestSuffix(requestSuffix);
        mappingConfig.setResponseSuffix(responseSuffix);
        mappingConfig.setResponseProjectionSuffix(responseProjectionSuffix);
        mappingConfig.setParametrizedInputSuffix(parametrizedInputSuffix);
        mappingConfig.setGenerateAllMethodInProjection(generateAllMethodInProjection);
        mappingConfig.setResponseProjectionMaxDepth(responseProjectionMaxDepth);
        mappingConfig.setUseObjectMapperForRequestSerialization(mapToHashSet(useObjectMapperForRequestSerialization));
        mappingConfig.setTypesAsInterfaces(mapToHashSet(typesAsInterfaces));
        mappingConfig.setResolverArgumentAnnotations(mapToHashSet(resolverArgumentAnnotations));
        mappingConfig.setParametrizedResolverAnnotations(mapToHashSet(parametrizedResolverAnnotations));

        mappingConfig.setResolverParentInterface(getResolverParentInterface());
        mappingConfig.setQueryResolverParentInterface(getQueryResolverParentInterface());
        mappingConfig.setMutationResolverParentInterface(getMutationResolverParentInterface());
        mappingConfig.setSubscriptionResolverParentInterface(getSubscriptionResolverParentInterface());

        mappingConfig.setGeneratedLanguage(generatedLanguage);
        mappingConfig.setGenerateModelOpenClasses(isGenerateModelOpenClasses());
        mappingConfig.setInitializeNullableTypes(isInitializeNullableTypes());
        mappingConfig.setGenerateSealedInterfaces(isGenerateSealedInterfaces());

        mappingConfig.setSupportUnknownFields(isSupportUnknownFields());
        mappingConfig.setUnknownFieldsPropertyName(getUnknownFieldsPropertyName());

        if (skip) {
            getLog().info("Skipping code generation");
            return;
        }

        try {
            instantiateCodegen(mappingConfig).generate();
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("Code generation failed. See above for the full exception.");
        }
    }

    private GraphQLCodegen instantiateCodegen(MappingConfig mappingConfig) throws IOException {
        java.util.Optional<MappingConfigSupplier> mappingConfigSupplier = buildJsonSupplier(configurationFiles);
        GeneratedLanguage language = mappingConfigSupplier.map(Supplier::get)
                .map(MappingConfig::getGeneratedLanguage)
                .orElse(generatedLanguage);

        if (skipSchemaSizeLimit) {
            ParserOptions.Builder parserOptionBuilder = ParserOptions.newParserOptions()
                .maxTokens(Integer.MAX_VALUE)
                .maxCharacters(Integer.MAX_VALUE)
                .maxWhitespaceTokens(Integer.MAX_VALUE)
                .maxRuleDepth(Integer.MAX_VALUE);
            ParserOptions.setDefaultParserOptions(parserOptionBuilder.build());
        }

        switch (language) {
            case JAVA:
                return new JavaGraphQLCodegen(getSchemas(), graphqlQueryIntrospectionResultPath,
                        outputDir, mappingConfig, mappingConfigSupplier.orElse(null));
            case SCALA:
                return new ScalaGraphQLCodegen(getSchemas(), graphqlQueryIntrospectionResultPath,
                        outputDir, mappingConfig, mappingConfigSupplier.orElse(null));
            case KOTLIN:
                return new KotlinGraphQLCodegen(getSchemas(), graphqlQueryIntrospectionResultPath,
                        outputDir, mappingConfig, mappingConfigSupplier.orElse(null));
            default:
                throw new LanguageNotSupportedException(language);
        }
    }

    private List<String> getSchemas() throws IOException {
        if (graphqlSchemaPaths != null) {
            return Arrays.asList(graphqlSchemaPaths);
        }
        if (graphqlQueryIntrospectionResultPath != null) {
            return Collections.emptyList();
        }
        Path schemasRootDir = getSchemasRootDir();
        SchemaFinder finder = new SchemaFinder(schemasRootDir);
        finder.setRecursive(graphqlSchemas.isRecursive());
        finder.setIncludePattern(graphqlSchemas.getIncludePattern());
        finder.setExcludedFiles(graphqlSchemas.getExcludedFiles());
        return finder.findSchemas();
    }

    private Path getSchemasRootDir() {
        String rootDir = graphqlSchemas.getRootDir();
        if (rootDir == null) {
            return getDefaultResourcesDirectory().orElseThrow(() -> new IllegalStateException(
                    "Default resource folder not found, please provide <rootDir> in <graphqlSchemas>"));
        }
        return Paths.get(rootDir);
    }

    private Optional<Path> getDefaultResourcesDirectory() {
        return project.getResources().stream().findFirst().map(Resource::getDirectory).map(Paths::get);
    }

    private java.util.Optional<MappingConfigSupplier> buildJsonSupplier(String[] configurationFiles) {
        if (configurationFiles != null && configurationFiles.length != 0) {
            return java.util.Optional.of(new MergeableMappingConfigSupplier(Arrays.asList(configurationFiles.clone())));
        }
        return java.util.Optional.empty();
    }

    private void addCompileSourceRootIfConfigured() {
        String path = outputDir.getPath();
        getLog().info("Added the following path to the source root: " + path);
        project.addCompileSourceRoot(path);
    }

    public String[] getGraphqlSchemaPaths() {
        return graphqlSchemaPaths;
    }

    public String getGraphqlQueryIntrospectionResultPath() {
        return graphqlQueryIntrospectionResultPath;
    }

    public SchemaFinderConfig getGraphqlSchemas() {
        return graphqlSchemas;
    }

    public File getOutputDir() {
        return outputDir;
    }

    @Override
    public Map<String, String> getCustomTypesMapping() {
        return convertToMap(customTypesMapping);
    }

    @Override
    public Map<String, List<String>> getCustomAnnotationsMapping() {
        return convertToListsMap(customAnnotationsMapping);
    }

    @Override
    public Map<String, List<String>> getDirectiveAnnotationsMapping() {
        return convertToListsMap(directiveAnnotationsMapping);
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getApiPackageName() {
        return apiPackageName;
    }

    @Override
    public String getApiNamePrefix() {
        return apiNamePrefix;
    }

    @Override
    public String getApiNameSuffix() {
        return apiNameSuffix;
    }

    @Override
    public String getModelPackageName() {
        return modelPackageName;
    }

    @Override
    public String getModelNamePrefix() {
        return modelNamePrefix;
    }

    @Override
    public String getModelNameSuffix() {
        return modelNameSuffix;
    }

    @Override
    public String getModelValidationAnnotation() {
        return modelValidationAnnotation;
    }

    @Override
    public Boolean getGenerateBuilder() {
        return generateBuilder;
    }

    @Override
    public Boolean getGenerateApis() {
        return generateApis;
    }

    @Override
    public Boolean getGenerateModelsForRootTypes() {
        return generateModelsForRootTypes;
    }

    @Override
    public Boolean getGenerateEqualsAndHashCode() {
        return generateEqualsAndHashCode;
    }

    @Override
    public Boolean getGenerateImmutableModels() {
        return generateImmutableModels;
    }

    @Override
    public Boolean getGenerateToString() {
        return generateToString;
    }

    @Override
    public String getApiReturnType() {
        return apiReturnType;
    }

    @Override
    public String getApiReturnListType() {
        return apiReturnListType;
    }

    @Override
    public String getSubscriptionReturnType() {
        return subscriptionReturnType;
    }

    @Override
    public Boolean getGenerateExtensionFieldsResolvers() {
        return generateExtensionFieldsResolvers;
    }

    @Override
    public Boolean getGenerateParameterizedFieldsResolvers() {
        return generateParameterizedFieldsResolvers;
    }

    @Override
    public String getTypeResolverPrefix() {
        return typeResolverPrefix;
    }

    @Override
    public String getTypeResolverSuffix() {
        return typeResolverSuffix;
    }

    @Override
    public Boolean getGenerateDataFetchingEnvironmentArgumentInApis() {
        return generateDataFetchingEnvironmentArgumentInApis;
    }

    @Override
    public RelayConfig getRelayConfig() {
        return relayConfig;
    }

    @Override
    public Boolean getUseOptionalForNullableReturnTypes() {
        return useOptionalForNullableReturnTypes;
    }

    @Override
    public Boolean getGenerateApisWithThrowsException() {
        return generateApisWithThrowsException;
    }

    @Override
    public Boolean getGenerateApisWithSuspendFunctions() {
        return generateApisWithSuspendFunctions;
    }

    @Override
    public Boolean getGenerateJacksonTypeIdResolver() {
        return generateJacksonTypeIdResolver;
    }

    @Override
    public Boolean getAddGeneratedAnnotation() {
        return addGeneratedAnnotation;
    }

    @Override
    public String getGeneratedAnnotation() {
        return generatedAnnotation;
    }

    @Override
    public ApiRootInterfaceStrategy getApiRootInterfaceStrategy() {
        return apiRootInterfaceStrategy;
    }

    @Override
    public ApiInterfaceStrategy getApiInterfaceStrategy() {
        return apiInterfaceStrategy;
    }

    @Override
    public ApiNamePrefixStrategy getApiNamePrefixStrategy() {
        return apiNamePrefixStrategy;
    }

    @Override
    public Set<String> getFieldsWithResolvers() {
        return mapToHashSet(fieldsWithResolvers);
    }

    @Override
    public Set<String> getFieldsWithoutResolvers() {
        return mapToHashSet(fieldsWithoutResolvers);
    }

    @Override
    public Set<String> getFieldsToExcludeFromGeneration() {
        return mapToHashSet(fieldsToExcludeFromGeneration);
    }

    @Override
    public Set<String> getFieldsWithDataFetcherResult() {
        return mapToHashSet(fieldsWithDataFetcherResult);
    }

    @Override
    public Boolean getGenerateAllMethodInProjection() {
        return generateAllMethodInProjection;
    }

    @Override
    public Integer getResponseProjectionMaxDepth() {
        return responseProjectionMaxDepth;
    }

    @Override
    public Boolean getGenerateClient() {
        return generateClient;
    }

    @Override
    public String getRequestSuffix() {
        return requestSuffix;
    }

    @Override
    public String getResponseSuffix() {
        return responseSuffix;
    }

    @Override
    public String getResponseProjectionSuffix() {
        return responseProjectionSuffix;
    }

    @Override
    public String getParametrizedInputSuffix() {
        return parametrizedInputSuffix;
    }

    @Override
    public Set<String> getUseObjectMapperForRequestSerialization() {
        return mapToHashSet(useObjectMapperForRequestSerialization);
    }

    @Override
    public Set<String> getTypesAsInterfaces() {
        return mapToHashSet(typesAsInterfaces);
    }

    @Override
    public Set<String> getResolverArgumentAnnotations() {
        return mapToHashSet(resolverArgumentAnnotations);
    }

    @Override
    public Set<String> getParametrizedResolverAnnotations() {
        return mapToHashSet(parametrizedResolverAnnotations);
    }

    @Override
    public String getQueryResolverParentInterface() {
        return parentInterfaces.getQueryResolver();
    }

    @Override
    public String getMutationResolverParentInterface() {
        return parentInterfaces.getMutationResolver();
    }

    @Override
    public String getSubscriptionResolverParentInterface() {
        return parentInterfaces.getSubscriptionResolver();
    }

    @Override
    public String getResolverParentInterface() {
        return parentInterfaces.getResolver();
    }

    @Override
    public GeneratedLanguage getGeneratedLanguage() {
        return generatedLanguage;
    }

    @Override
    public Boolean isGenerateModelOpenClasses() {
        return generateModelOpenClasses;
    }

    @Override
    public Boolean isInitializeNullableTypes() {
        return initializeNullableTypes;
    }

    @Override
    public Boolean isGenerateSealedInterfaces() {
        return generateSealedInterfaces;
    }

    @Override
    public Boolean isSupportUnknownFields() {
        return supportUnknownFields;
    }

    @Override
    public String getUnknownFieldsPropertyName() {
        return unknownFieldsPropertyName;
    }

    @Override
    public Boolean isGenerateNoArgsConstructorOnly() {
        return generateNoArgsConstructorOnly;
    }

    @Override
    public Boolean isGenerateModelsWithPublicFields() {
        return generateModelsWithPublicFields;
    }

    public ParentInterfacesConfig getParentInterfaces() {
        return parentInterfaces;
    }

    public String[] getConfigurationFiles() {
        return configurationFiles;
    }

    private static Map<String, List<String>> convertToListsMap(Map<String, Properties> sourceMap) {
        if (sourceMap == null) {
            return new HashMap<>();
        }
        Map<String, List<String>> map = new HashMap<>(sourceMap.size());
        for (Map.Entry<String, Properties> e : sourceMap.entrySet()) {
            if (e.getValue() != null) {
                Collection<Object> values = e.getValue().values();
                List<String> stringValues = new ArrayList<>(values.size());
                for (Object value : values) {
                    stringValues.add(value.toString());
                }
                map.put(e.getKey(), stringValues);
            }
        }
        return map;
    }

    private static Set<String> mapToHashSet(String[] sourceSet) {
        if (sourceSet == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(Arrays.asList(sourceSet));
    }

    private static Map<String, String> convertToMap(Properties properties) {
        if (properties == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>(properties.size());
        for (String name : properties.stringPropertyNames()) {
            result.put(name, properties.getProperty(name));
        }
        return result;
    }   
    
    @Override
    public File getCustomTemplatesRoot() {
        return customTemplatesRoot == null ? project.getBasedir() : customTemplatesRoot;
    }

    @Override
    public Map<String, String> getCustomTemplates() {
        if (customTemplates == null) {
            return new HashMap<>();
        }
        return customTemplates;
    }

}
