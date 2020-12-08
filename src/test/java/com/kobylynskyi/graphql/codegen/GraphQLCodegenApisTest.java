package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.ApiInterfaceStrategy;
import com.kobylynskyi.graphql.codegen.model.ApiNamePrefixStrategy;
import com.kobylynskyi.graphql.codegen.model.ApiRootInterfaceStrategy;
import com.kobylynskyi.graphql.codegen.model.GeneratedInformation;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.supplier.SchemaFinder;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphQLCodegenApisTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated");
    private final MappingConfig mappingConfig = new MappingConfig();
    private final SchemaFinder schemaFinder = new SchemaFinder(Paths.get("src/test/resources"));

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_FileNameAsPrefix() throws Exception {
        schemaFinder.setIncludePattern("sub-schema.*\\.graphqls");
        mappingConfig.setApiNamePrefixStrategy(ApiNamePrefixStrategy.FILE_NAME_AS_PREFIX);
        new JavaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo())
                .generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("SubSchema1PingQueryResolver.java", "SubSchema2PongQueryResolver.java",
                "QueryResolver.java")), generatedFileNames);
    }

    @Test
    void generate_FolderNameAsPrefix() throws Exception {
        schemaFinder.setIncludePattern("sub-schema.*\\.graphqls");
        mappingConfig.setApiNamePrefixStrategy(ApiNamePrefixStrategy.FOLDER_NAME_AS_PREFIX);
        new JavaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo())
                .generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("SubProj1PingQueryResolver.java", "SubProj2PongQueryResolver.java",
                "QueryResolver.java")), generatedFileNames);
    }

    @Test
    void generate_Constant() throws Exception {
        schemaFinder.setIncludePattern("sub-schema.*\\.graphqls");
        mappingConfig.setApiNamePrefixStrategy(ApiNamePrefixStrategy.CONSTANT);
        new JavaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo())
                .generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("PingQueryResolver.java", "PongQueryResolver.java",
                "QueryResolver.java")), generatedFileNames);
    }

    @Test
    void generate_InterfacePerSchemaAndFolderNameAsPrefix() throws Exception {
        schemaFinder.setIncludePattern("sub-schema.*\\.graphqls");
        mappingConfig.setApiNamePrefixStrategy(ApiNamePrefixStrategy.FOLDER_NAME_AS_PREFIX);
        mappingConfig.setApiRootInterfaceStrategy(ApiRootInterfaceStrategy.INTERFACE_PER_SCHEMA);
        new JavaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo())
                .generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("SubProj1PingQueryResolver.java", "SubProj2PongQueryResolver.java",
                "SubProj1QueryResolver.java", "SubProj2QueryResolver.java")), generatedFileNames);
    }

    @Test
    void generate_InterfacePerSchemaAndFileNameAsPrefix() throws Exception {
        schemaFinder.setIncludePattern("sub-schema.*\\.graphqls");
        mappingConfig.setApiNamePrefixStrategy(ApiNamePrefixStrategy.FILE_NAME_AS_PREFIX);
        mappingConfig.setApiRootInterfaceStrategy(ApiRootInterfaceStrategy.INTERFACE_PER_SCHEMA);
        new JavaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo())
                .generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("SubSchema1PingQueryResolver.java", "SubSchema2PongQueryResolver.java",
                "SubSchema1QueryResolver.java", "SubSchema2QueryResolver.java")), generatedFileNames);
    }

    @Test
    void generate_InterfacePerSchemaAndConstantPrefix() throws IOException {
        schemaFinder.setIncludePattern("sub-schema.*\\.graphqls");
        mappingConfig.setApiNamePrefixStrategy(ApiNamePrefixStrategy.CONSTANT);
        mappingConfig.setApiRootInterfaceStrategy(ApiRootInterfaceStrategy.INTERFACE_PER_SCHEMA);
        GeneratedInformation generatedInformation = TestUtils.getStaticGeneratedInfo();
        List<String> schemas = schemaFinder.findSchemas();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new JavaGraphQLCodegen(schemas, outputBuildDir, mappingConfig, generatedInformation));
    }

    @Test
    void generate_DoNotGenerateApiInterfaceForOperations() throws IOException {
        schemaFinder.setIncludePattern("test.*\\.graphqls");
        mappingConfig.setApiInterfaceStrategy(ApiInterfaceStrategy.DO_NOT_GENERATE);

        new JavaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo())
                .generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertEquals(Arrays.asList(
                "Event.java", "EventProperty.java", "EventPropertyResolver.java", "EventStatus.java",
                "MutationResolver.java", "QueryResolver.java", "SubscriptionResolver.java"),
                Arrays.stream(files).map(File::getName).sorted().collect(toList()));
    }

    @Test
    void generate_DoNotGenerateRootApiInterfaces() throws IOException {
        schemaFinder.setIncludePattern("test.*\\.graphqls");
        mappingConfig.setApiRootInterfaceStrategy(ApiRootInterfaceStrategy.DO_NOT_GENERATE);

        new JavaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo())
                .generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertEquals(Arrays.asList(
                "CreateEventMutationResolver.java", "Event.java", "EventByIdQueryResolver.java", "EventProperty.java",
                "EventPropertyResolver.java", "EventStatus.java", "EventsByCategoryAndStatusQueryResolver.java",
                "EventsByIdsQueryResolver.java", "EventsCreatedSubscriptionResolver.java", "VersionQueryResolver.java"),
                Arrays.stream(files).map(File::getName).sorted().collect(toList()));
    }

    @Test
    void generate_WithoutThrowsException() throws IOException {
        schemaFinder.setIncludePattern("github.*\\.graphqls");
        mappingConfig.setGenerateApisWithThrowsException(false);

        new JavaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo())
                .generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/apis/CodeOfConductQueryResolver_withoutThrowsException.java.txt"),
                getFileByName(files, "CodeOfConductQueryResolver.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/apis/MutationResolver_withoutThrowsException.java.txt"),
                getFileByName(files, "MutationResolver.java"));
    }

}