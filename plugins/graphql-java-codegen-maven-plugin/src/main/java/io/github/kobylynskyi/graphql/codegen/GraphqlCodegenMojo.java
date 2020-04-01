package io.github.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.GraphqlCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.supplier.JsonMappingConfigSupplier;
import com.kobylynskyi.graphql.codegen.supplier.MappingConfigSupplier;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GraphqlCodegenMojo extends AbstractMojo {

    @Parameter(required = true)
    private String[] graphqlSchemaPaths;

    @Parameter(required = true)
    private File outputDir;

    @Parameter
    private Map<String, String> customTypesMapping;
    @Parameter
    private Map<String, String> customAnnotationsMapping;
    @Parameter
    private String packageName;

    @Parameter(defaultValue = "true")
    private boolean generateApis;

    @Parameter(defaultValue = "false")
    private boolean generateEqualsAndHashCode;

    @Parameter(defaultValue = "false")
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

    @Parameter(defaultValue = "false")
    private boolean generateAsyncApi;

    @Parameter(defaultValue = "javax.validation.constraints.NotNull")
    private String modelValidationAnnotation;

    @Parameter(name = "jsonConfigurationFile", required = false)
    private String jsonConfigurationFile;

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
        mappingConfig.setCustomTypesMapping(
                customTypesMapping != null ? customTypesMapping : new HashMap<>());
        mappingConfig.setModelNamePrefix(modelNamePrefix);
        mappingConfig.setModelNameSuffix(modelNameSuffix);
        mappingConfig.setApiPackageName(apiPackageName);
        mappingConfig.setModelPackageName(modelPackageName);
        mappingConfig.setGenerateApis(generateApis);
        mappingConfig.setModelValidationAnnotation(modelValidationAnnotation);
        mappingConfig.setCustomAnnotationsMapping(
                customAnnotationsMapping != null ? customAnnotationsMapping : new HashMap<>());
        mappingConfig.setGenerateEqualsAndHashCode(generateEqualsAndHashCode);
        mappingConfig.setGenerateToString(generateToString);
        mappingConfig.setSubscriptionReturnType(subscriptionReturnType);
        mappingConfig.setGenerateAsyncApi(generateAsyncApi);

        MappingConfigSupplier mappingConfigSupplier = buildJsonSupplier(jsonConfigurationFile);

        try {
            new GraphqlCodegen(Arrays.asList(graphqlSchemaPaths), outputDir, mappingConfig, mappingConfigSupplier).generate();
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("Code generation failed. See above for the full exception.");
        }
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

    private static Map<String, String> convertToMap(Properties properties) {
        Map<String, String> map = new HashMap<>();
        properties.forEach((key, value) -> map.put((String) key, (String) value));
        return map;
    }

    public String[] getGraphqlSchemaPaths() {
        return graphqlSchemaPaths;
    }

    public void setGraphqlSchemaPaths(String[] graphqlSchemaPaths) {
        this.graphqlSchemaPaths = graphqlSchemaPaths;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public Map<String, String> getCustomTypesMapping() {
        return customTypesMapping;
    }

    public void setCustomTypesMapping(Map<String, String> customTypesMapping) {
        this.customTypesMapping = customTypesMapping;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getApiPackageName() {
        return apiPackageName;
    }

    public void setApiPackageName(String apiPackageName) {
        this.apiPackageName = apiPackageName;
    }

    public String getModelPackageName() {
        return modelPackageName;
    }

    public void setModelPackageName(String modelPackageName) {
        this.modelPackageName = modelPackageName;
    }

    public String getModelNamePrefix() {
        return modelNamePrefix;
    }

    public void setModelNamePrefix(String modelNamePrefix) {
        this.modelNamePrefix = modelNamePrefix;
    }

    public String getModelNameSuffix() {
        return modelNameSuffix;
    }

    public void setModelNameSuffix(String modelNameSuffix) {
        this.modelNameSuffix = modelNameSuffix;
    }

    public Map<String, String> getCustomAnnotationsMapping() {
        return customAnnotationsMapping;
    }

    public void setCustomAnnotationsMapping(Map<String, String> customAnnotationsMapping) {
        this.customAnnotationsMapping = customAnnotationsMapping;
    }

    public String getModelValidationAnnotation() {
        return modelValidationAnnotation;
    }

    public void setModelValidationAnnotation(String modelValidationAnnotation) {
        this.modelValidationAnnotation = modelValidationAnnotation;
    }

    public boolean isGenerateApis() {
        return generateApis;
    }

    public void setGenerateApis(boolean generateApis) {
        this.generateApis = generateApis;
    }

    public boolean isGenerateEqualsAndHashCode() {
        return generateEqualsAndHashCode;
    }

    public void setGenerateEqualsAndHashCode(boolean generateEqualsAndHashCode) {
        this.generateEqualsAndHashCode = generateEqualsAndHashCode;
    }

    public boolean isGenerateToString() {
        return generateToString;
    }

    public void setGenerateToString(boolean generateToString) {
        this.generateToString = generateToString;
    }

    public boolean isGenerateAsyncApi() {
        return generateAsyncApi;
    }

    public void setGenerateAsyncApi(boolean generateAsyncApi) {
        this.generateAsyncApi = generateAsyncApi;
    }

    public void setJsonConfigurationFile(String jsonConfigurationFile) {
        this.jsonConfigurationFile = jsonConfigurationFile;
    }

    public String getJsonConfigurationFile() {
        return jsonConfigurationFile;
    }

    public void setSubscriptionReturnType(String subscriptionReturnType) {
        this.subscriptionReturnType = subscriptionReturnType;
    }

    public String getSubscriptionReturnType() {
        return subscriptionReturnType;
    }
}
