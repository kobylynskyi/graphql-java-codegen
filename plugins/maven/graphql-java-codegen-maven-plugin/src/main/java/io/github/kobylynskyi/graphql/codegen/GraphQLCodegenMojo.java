package io.github.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.GraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.ApiInterfaceStrategy;
import com.kobylynskyi.graphql.codegen.model.ApiNamePrefixStrategy;
import com.kobylynskyi.graphql.codegen.model.ApiRootInterfaceStrategy;
import com.kobylynskyi.graphql.codegen.model.GraphQLCodegenConfiguration;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.MappingConfigConstants;
import com.kobylynskyi.graphql.codegen.model.RelayConfig;
import com.kobylynskyi.graphql.codegen.supplier.JsonMappingConfigSupplier;
import com.kobylynskyi.graphql.codegen.supplier.MappingConfigSupplier;
import com.kobylynskyi.graphql.codegen.supplier.SchemaFinder;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    private Map<String, String> customTypesMapping;

    @Parameter
    private Map<String, String[]> customAnnotationsMapping;

    @Parameter
    private Map<String, String[]> directiveAnnotationsMapping;

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

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_MAX_DEPTH_STRING)
    private int responseProjectionMaxDepth;

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

    @Parameter
    private String[] fieldsWithResolvers;

    @Parameter
    private String[] fieldsWithoutResolvers;

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
    private RelayConfig relayConfig = new RelayConfig();

    @Parameter
    private String jsonConfigurationFile;

    @Parameter
    private ParentInterfacesConfig parentInterfaces = new ParentInterfacesConfig();

    /**
     * The project being built.
     */
    @Parameter(readonly = true, required = true, defaultValue = "${project}")
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        addCompileSourceRootIfConfigured();

        MappingConfig mappingConfig = new MappingConfig();
        mappingConfig.setPackageName(packageName);
        mappingConfig.setCustomTypesMapping(customTypesMapping != null ? customTypesMapping : new HashMap<>());
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
        mappingConfig.setFieldsWithResolvers(mapToHashSet(fieldsWithResolvers));
        mappingConfig.setFieldsWithoutResolvers(mapToHashSet(fieldsWithoutResolvers));
        mappingConfig.setGenerateClient(generateClient);
        mappingConfig.setRequestSuffix(requestSuffix);
        mappingConfig.setResponseSuffix(responseSuffix);
        mappingConfig.setResponseProjectionSuffix(responseProjectionSuffix);
        mappingConfig.setParametrizedInputSuffix(parametrizedInputSuffix);
        mappingConfig.setResolverParentInterface(getResolverParentInterface());
        mappingConfig.setQueryResolverParentInterface(getQueryResolverParentInterface());
        mappingConfig.setMutationResolverParentInterface(getMutationResolverParentInterface());
        mappingConfig.setSubscriptionResolverParentInterface(getSubscriptionResolverParentInterface());
        mappingConfig.setResponseProjectionMaxDepth(getResponseProjectionMaxDepth());
        mappingConfig.setRelayConfig(relayConfig);

        MappingConfigSupplier mappingConfigSupplier = buildJsonSupplier(jsonConfigurationFile);

        try {
            new GraphQLCodegen(getSchemas(), graphqlQueryIntrospectionResultPath, outputDir, mappingConfig, mappingConfigSupplier).generate();
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("Code generation failed. See above for the full exception.");
        }
    }

    private List<String> getSchemas() throws IOException {
        if (graphqlSchemaPaths != null) {
            return Arrays.asList(graphqlSchemaPaths);
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

    private MappingConfigSupplier buildJsonSupplier(String jsonConfigurationFile) {
        if (jsonConfigurationFile != null && !jsonConfigurationFile.isEmpty()) {
            return new JsonMappingConfigSupplier(jsonConfigurationFile);
        }
        return null;
    }

    private void addCompileSourceRootIfConfigured() {
        String path = outputDir.getPath();
        getLog().info("Added the following path to the source root: " + path);
        project.addCompileSourceRoot(path);
    }

    public String[] getGraphqlSchemaPaths() {
        return graphqlSchemaPaths;
    }

    public void setGraphqlSchemaPaths(String[] graphqlSchemaPaths) {
        this.graphqlSchemaPaths = graphqlSchemaPaths;
    }

    public String getGraphqlQueryIntrospectionResultPath() {
        return graphqlQueryIntrospectionResultPath;
    }

    public void setGraphqlQueryIntrospectionResultPath(String graphqlQueryIntrospectionResultPath) {
        this.graphqlQueryIntrospectionResultPath = graphqlQueryIntrospectionResultPath;
    }

    public SchemaFinderConfig getGraphqlSchemas() {
        return graphqlSchemas;
    }

    public void setGraphqlSchemas(SchemaFinderConfig graphqlSchemas) {
        this.graphqlSchemas = graphqlSchemas;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public Map<String, String> getCustomTypesMapping() {
        return customTypesMapping;
    }

    public void setCustomTypesMapping(Map<String, String> customTypesMapping) {
        this.customTypesMapping = customTypesMapping;
    }

    @Override
    public Map<String, List<String>> getCustomAnnotationsMapping() {
        return convertToListsMap(customAnnotationsMapping);
    }

    public void setCustomAnnotationsMapping(Map<String, List<String>> customAnnotationsMapping) {
        this.customAnnotationsMapping = convertToArraysMap(customAnnotationsMapping);
    }

    @Override
    public Map<String, List<String>> getDirectiveAnnotationsMapping() {
        return convertToListsMap(directiveAnnotationsMapping);
    }

    public void setDirectiveAnnotationsMapping(Map<String, List<String>> directiveAnnotationsMapping) {
        this.directiveAnnotationsMapping = convertToArraysMap(directiveAnnotationsMapping);
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getApiPackageName() {
        return apiPackageName;
    }

    public void setApiPackageName(String apiPackageName) {
        this.apiPackageName = apiPackageName;
    }

    @Override
    public String getApiNamePrefix() {
        return apiNamePrefix;
    }

    public void setApiNamePrefix(String apiNamePrefix) {
        this.apiNamePrefix = apiNamePrefix;
    }

    @Override
    public String getApiNameSuffix() {
        return apiNameSuffix;
    }

    public void setApiNameSuffix(String apiNameSuffix) {
        this.apiNameSuffix = apiNameSuffix;
    }

    @Override
    public String getModelPackageName() {
        return modelPackageName;
    }

    public void setModelPackageName(String modelPackageName) {
        this.modelPackageName = modelPackageName;
    }

    @Override
    public String getModelNamePrefix() {
        return modelNamePrefix;
    }

    public void setModelNamePrefix(String modelNamePrefix) {
        this.modelNamePrefix = modelNamePrefix;
    }

    @Override
    public String getModelNameSuffix() {
        return modelNameSuffix;
    }

    public void setModelNameSuffix(String modelNameSuffix) {
        this.modelNameSuffix = modelNameSuffix;
    }

    @Override
    public String getModelValidationAnnotation() {
        return modelValidationAnnotation;
    }

    public void setModelValidationAnnotation(String modelValidationAnnotation) {
        this.modelValidationAnnotation = modelValidationAnnotation;
    }

    @Override
    public Boolean getGenerateBuilder() {
        return generateBuilder;
    }

    public void setGenerateBuilder(boolean generateBuilder) {
        this.generateBuilder = generateBuilder;
    }

    @Override
    public Boolean getGenerateApis() {
        return generateApis;
    }

    public void setGenerateApis(boolean generateApis) {
        this.generateApis = generateApis;
    }

    @Override
    public Boolean getGenerateModelsForRootTypes() {
        return generateModelsForRootTypes;
    }

    public void setGenerateModelsForRootTypes(boolean generateModelsForRootTypes) {
        this.generateModelsForRootTypes = generateModelsForRootTypes;
    }

    @Override
    public Boolean getGenerateEqualsAndHashCode() {
        return generateEqualsAndHashCode;
    }

    public void setGenerateEqualsAndHashCode(boolean generateEqualsAndHashCode) {
        this.generateEqualsAndHashCode = generateEqualsAndHashCode;
    }

    @Override
    public Boolean getGenerateImmutableModels() {
        return generateImmutableModels;
    }

    public void setGenerateImmutableModels(boolean generateImmutableModels) {
        this.generateImmutableModels = generateImmutableModels;
    }

    @Override
    public Boolean getGenerateToString() {
        return generateToString;
    }

    public void setGenerateToString(boolean generateToString) {
        this.generateToString = generateToString;
    }

    @Override
    public String getApiReturnType() {
        return apiReturnType;
    }

    public void setApiReturnType(String apiReturnType) {
        this.apiReturnType = apiReturnType;
    }

    @Override
    public String getApiReturnListType() {
        return apiReturnListType;
    }

    public void setApiReturnListType(String apiReturnListType) {
        this.apiReturnListType = apiReturnListType;
    }

    @Override
    public String getSubscriptionReturnType() {
        return subscriptionReturnType;
    }

    public void setSubscriptionReturnType(String subscriptionReturnType) {
        this.subscriptionReturnType = subscriptionReturnType;
    }

    @Override
    public Boolean getGenerateExtensionFieldsResolvers() {
        return generateExtensionFieldsResolvers;
    }

    public void setGenerateExtensionFieldsResolvers(boolean generateExtensionFieldsResolvers) {
        this.generateExtensionFieldsResolvers = generateExtensionFieldsResolvers;
    }

    @Override
    public Boolean getGenerateParameterizedFieldsResolvers() {
        return generateParameterizedFieldsResolvers;
    }

    public void setGenerateParameterizedFieldsResolvers(boolean generateParameterizedFieldsResolvers) {
        this.generateParameterizedFieldsResolvers = generateParameterizedFieldsResolvers;
    }

    @Override
    public String getTypeResolverPrefix() {
        return typeResolverPrefix;
    }

    public void setTypeResolverPrefix(String typeResolverPrefix) {
        this.typeResolverPrefix = typeResolverPrefix;
    }

    @Override
    public String getTypeResolverSuffix() {
        return typeResolverSuffix;
    }

    public void setTypeResolverSuffix(String typeResolverSuffix) {
        this.typeResolverSuffix = typeResolverSuffix;
    }

    @Override
    public Boolean getGenerateDataFetchingEnvironmentArgumentInApis() {
        return generateDataFetchingEnvironmentArgumentInApis;
    }

    @Override
    public RelayConfig getRelayConfig() {
        return relayConfig;
    }

    public void setRelayConfig(RelayConfig relayConfig) {
        this.relayConfig = relayConfig;
    }

    public void setGenerateDataFetchingEnvironmentArgumentInApis(boolean generateDataFetchingEnvironmentArgumentInApis) {
        this.generateDataFetchingEnvironmentArgumentInApis = generateDataFetchingEnvironmentArgumentInApis;
    }

    @Override
    public Boolean getUseOptionalForNullableReturnTypes() {
        return useOptionalForNullableReturnTypes;
    }

    public void setUseOptionalForNullableReturnTypes(boolean useOptionalForNullableReturnTypes) {
        this.useOptionalForNullableReturnTypes = useOptionalForNullableReturnTypes;
    }

    @Override
    public ApiRootInterfaceStrategy getApiRootInterfaceStrategy() {
        return apiRootInterfaceStrategy;
    }

    public void setApiRootInterfaceStrategy(ApiRootInterfaceStrategy apiRootInterfaceStrategy) {
        this.apiRootInterfaceStrategy = apiRootInterfaceStrategy;
    }

    @Override
    public ApiInterfaceStrategy getApiInterfaceStrategy() {
        return apiInterfaceStrategy;
    }

    public void setApiInterfaceStrategy(ApiInterfaceStrategy apiInterfaceStrategy) {
        this.apiInterfaceStrategy = apiInterfaceStrategy;
    }

    @Override
    public ApiNamePrefixStrategy getApiNamePrefixStrategy() {
        return apiNamePrefixStrategy;
    }

    public void setApiNamePrefixStrategy(ApiNamePrefixStrategy apiNamePrefixStrategy) {
        this.apiNamePrefixStrategy = apiNamePrefixStrategy;
    }

    @Override
    public Set<String> getFieldsWithResolvers() {
        return mapToHashSet(fieldsWithResolvers);
    }

    public void setFieldsWithResolvers(Set<String> fieldsWithResolvers) {
        this.fieldsWithResolvers = mapToArray(fieldsWithResolvers);
    }

    @Override
    public Set<String> getFieldsWithoutResolvers() {
        return mapToHashSet(fieldsWithoutResolvers);
    }

    public void setFieldsWithoutResolvers(Set<String> fieldsWithoutResolvers) {
        this.fieldsWithoutResolvers = mapToArray(fieldsWithoutResolvers);
    }

    @Override
    public Boolean getGenerateClient() {
        return generateClient;
    }

    public void setGenerateClient(boolean generateClient) {
        this.generateClient = generateClient;
    }

    @Override
    public String getRequestSuffix() {
        return requestSuffix;
    }

    public void setRequestSuffix(String requestSuffix) {
        this.requestSuffix = requestSuffix;
    }

    @Override
    public String getResponseSuffix() {
        return responseSuffix;
    }

    public void setResponseSuffix(String responseSuffix) {
        this.responseSuffix = responseSuffix;
    }

    @Override
    public String getResponseProjectionSuffix() {
        return responseProjectionSuffix;
    }

    public void setResponseProjectionSuffix(String responseProjectionSuffix) {
        this.responseProjectionSuffix = responseProjectionSuffix;
    }

    @Override
    public String getParametrizedInputSuffix() {
        return parametrizedInputSuffix;
    }

    public void setParametrizedInputSuffix(String parametrizedInputSuffix) {
        this.parametrizedInputSuffix = parametrizedInputSuffix;
    }

    public ParentInterfacesConfig getParentInterfaces() {
        return parentInterfaces;
    }

    public void setParentInterfaces(ParentInterfacesConfig parentInterfaces) {
        this.parentInterfaces = parentInterfaces;
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

    public String getJsonConfigurationFile() {
        return jsonConfigurationFile;
    }

    public void setJsonConfigurationFile(String jsonConfigurationFile) {
        this.jsonConfigurationFile = jsonConfigurationFile;
    }

    @Override
    public Integer getResponseProjectionMaxDepth() {
        return responseProjectionMaxDepth;
    }

    public void setResponseProjectionMaxDepth(int responseProjectionMaxDepth) {
        this.responseProjectionMaxDepth = responseProjectionMaxDepth;
    }

    private static Map<String, List<String>> convertToListsMap(Map<String, String[]> sourceMap) {
        if (sourceMap == null) {
            return new HashMap<>();
        }
        Map<String, List<String>> map = new HashMap<>();
        for (Map.Entry<String, String[]> e : sourceMap.entrySet()) {
            if (e.getValue() != null) {
                map.put(e.getKey(), Arrays.asList(e.getValue()));
            }
        }
        return map;
    }

    private static Map<String, String[]> convertToArraysMap(Map<String, List<String>> sourceMap) {
        if (sourceMap == null) {
            return new HashMap<>();
        }
        Map<String, String[]> map = new HashMap<>();
        for (Map.Entry<String, List<String>> e : sourceMap.entrySet()) {
            if (e.getValue() != null) {
                map.put(e.getKey(), e.getValue().toArray(new String[0]));
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

    private static String[] mapToArray(Set<String> sourceSet) {
        if (sourceSet == null) {
            return new String[0];
        }
        return sourceSet.toArray(new String[0]);
    }

}
