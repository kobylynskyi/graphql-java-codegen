package io.github.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.GraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.GraphQLCodegenConfiguration;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.MappingConfigConstants;
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
import java.util.*;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GraphQLCodegenMojo extends AbstractMojo implements GraphQLCodegenConfiguration {

    @Parameter
    private String[] graphqlSchemaPaths;

    @Parameter
    private SchemaFinderConfig graphqlSchemas = new SchemaFinderConfig();

    @Parameter(required = true)
    private File outputDir;

    @Parameter
    private Map<String, String> customTypesMapping;

    @Parameter
    private Map<String, String> customAnnotationsMapping;

    @Parameter
    private String packageName;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_BUILDER_STRING)
    private boolean generateBuilder;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_APIS_STRING)
    private boolean generateApis;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_EQUALS_AND_HASHCODE_STRING)
    private boolean generateEqualsAndHashCode;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_TO_STRING_STRING)
    private boolean generateToString;

    @Parameter
    private String apiPackageName;

    @Parameter
    private String modelPackageName;

    @Parameter
    private String modelNamePrefix;

    @Parameter
    private String modelNameSuffix;

    @Parameter
    private String subscriptionReturnType;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_ASYNC_APIS_STRING)
    private Boolean generateAsyncApi;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_VALIDATION_ANNOTATION)
    private String modelValidationAnnotation;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_PARAMETERIZED_FIELDS_RESOLVERS_STRING)
    private boolean generateParameterizedFieldsResolvers;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_EXTENSION_FIELDS_RESOLVERS_STRING)
    private boolean generateExtensionFieldsResolvers;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_DATA_FETCHING_ENV_STRING)
    private boolean generateDataFetchingEnvironmentArgumentInApis;

    @Parameter
    private Set<String> fieldsWithResolvers = new HashSet<>();

    @Parameter
    private Set<String> fieldsWithoutResolvers = new HashSet<>();

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_GENERATE_REQUESTS_STRING)
    private boolean generateRequests;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_REQUEST_SUFFIX)
    private String requestSuffix;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_SUFFIX)
    private String responseProjectionSuffix;

    @Parameter(defaultValue = MappingConfigConstants.DEFAULT_PARAMETRIZED_INPUT_SUFIX)
    private String parametrizedInputSuffix;

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
        mappingConfig.setModelNamePrefix(modelNamePrefix);
        mappingConfig.setModelNameSuffix(modelNameSuffix);
        mappingConfig.setApiPackageName(apiPackageName);
        mappingConfig.setModelPackageName(modelPackageName);
        mappingConfig.setGenerateBuilder(generateBuilder);
        mappingConfig.setGenerateApis(generateApis);
        mappingConfig.setModelValidationAnnotation(modelValidationAnnotation);
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping != null ? customAnnotationsMapping : new HashMap<>());
        mappingConfig.setGenerateEqualsAndHashCode(generateEqualsAndHashCode);
        mappingConfig.setGenerateToString(generateToString);
        mappingConfig.setSubscriptionReturnType(subscriptionReturnType);
        mappingConfig.setGenerateAsyncApi(generateAsyncApi);
        mappingConfig.setGenerateParameterizedFieldsResolvers(generateParameterizedFieldsResolvers);
        mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(generateDataFetchingEnvironmentArgumentInApis);
        mappingConfig.setGenerateExtensionFieldsResolvers(generateExtensionFieldsResolvers);
        mappingConfig.setFieldsWithResolvers(fieldsWithResolvers != null ? fieldsWithResolvers : new HashSet<>());
        mappingConfig.setFieldsWithoutResolvers(fieldsWithoutResolvers != null ? fieldsWithoutResolvers : new HashSet<>());
        mappingConfig.setGenerateRequests(generateRequests);
        mappingConfig.setRequestSuffix(requestSuffix);
        mappingConfig.setResponseProjectionSuffix(responseProjectionSuffix);
        mappingConfig.setParametrizedInputSuffix(parametrizedInputSuffix);
        mappingConfig.setResolverParentInterface(getResolverParentInterface());
        mappingConfig.setQueryResolverParentInterface(getQueryResolverParentInterface());
        mappingConfig.setMutationResolverParentInterface(getMutationResolverParentInterface());
        mappingConfig.setSubscriptionResolverParentInterface(getSubscriptionResolverParentInterface());

        MappingConfigSupplier mappingConfigSupplier = buildJsonSupplier(jsonConfigurationFile);

        try {
            new GraphQLCodegen(getSchemas(), outputDir, mappingConfig, mappingConfigSupplier).generate();
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
    public Map<String, String> getCustomAnnotationsMapping() {
        return customAnnotationsMapping;
    }

    public void setCustomAnnotationsMapping(Map<String, String> customAnnotationsMapping) {
        this.customAnnotationsMapping = customAnnotationsMapping;
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
    public Boolean getGenerateEqualsAndHashCode() {
        return generateEqualsAndHashCode;
    }

    public void setGenerateEqualsAndHashCode(boolean generateEqualsAndHashCode) {
        this.generateEqualsAndHashCode = generateEqualsAndHashCode;
    }

    @Override
    public Boolean getGenerateToString() {
        return generateToString;
    }

    public void setGenerateToString(boolean generateToString) {
        this.generateToString = generateToString;
    }

    @Override
    public Boolean getGenerateAsyncApi() {
        return generateAsyncApi;
    }

    public void setGenerateAsyncApi(boolean generateAsyncApi) {
        this.generateAsyncApi = generateAsyncApi;
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
    public Boolean getGenerateDataFetchingEnvironmentArgumentInApis() {
        return generateDataFetchingEnvironmentArgumentInApis;
    }

    public void setGenerateDataFetchingEnvironmentArgumentInApis(boolean generateDataFetchingEnvironmentArgumentInApis) {
        this.generateDataFetchingEnvironmentArgumentInApis = generateDataFetchingEnvironmentArgumentInApis;
    }

    @Override
    public Set<String> getFieldsWithResolvers() {
        return fieldsWithResolvers;
    }

    public void setFieldsWithResolvers(Set<String> fieldsWithResolvers) {
        this.fieldsWithResolvers = fieldsWithResolvers;
    }

    @Override
    public Set<String> getFieldsWithoutResolvers() {
        return fieldsWithoutResolvers;
    }

    public void setFieldsWithoutResolvers(Set<String> fieldsWithoutResolvers) {
        this.fieldsWithoutResolvers = fieldsWithoutResolvers;
    }

    @Override
    public Boolean getGenerateRequests() {
        return generateRequests;
    }

    public void setGenerateRequests(boolean generateRequests) {
        this.generateRequests = generateRequests;
    }

    @Override
    public String getRequestSuffix() {
        return requestSuffix;
    }

    public void setRequestSuffix(String requestSuffix) {
        this.requestSuffix = requestSuffix;
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
}
