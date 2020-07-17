package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.ApiNamePrefixStrategy;
import com.kobylynskyi.graphql.codegen.model.ApiRootInterfaceStrategy;
import com.kobylynskyi.graphql.codegen.model.GeneratedInformation;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.supplier.SchemaFinder;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphQLCodegenApisTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated");
    private final MappingConfig mappingConfig = new MappingConfig();
    private final SchemaFinder schemaFinder = new SchemaFinder(Paths.get("src/test/resources"));

    @BeforeEach
    void init() {
        schemaFinder.setIncludePattern("sub-schema.*\\.graphqls");
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_FileNameAsPrefix() throws Exception {
        mappingConfig.setApiNamePrefixStrategy(ApiNamePrefixStrategy.FILE_NAME_AS_PREFIX);
        new GraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo())
                .generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("SubSchema1PingQueryResolver.java", "SubSchema2PongQueryResolver.java",
                "QueryResolver.java")), generatedFileNames);
    }

    @Test
    void generate_FolderNameAsPrefix() throws Exception {
        mappingConfig.setApiNamePrefixStrategy(ApiNamePrefixStrategy.FOLDER_NAME_AS_PREFIX);
        new GraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo())
                .generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("SubProj1PingQueryResolver.java", "SubProj2PongQueryResolver.java",
                "QueryResolver.java")), generatedFileNames);
    }

    @Test
    void generate_Constant() throws Exception {
        mappingConfig.setApiNamePrefixStrategy(ApiNamePrefixStrategy.CONSTANT);
        new GraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo())
                .generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("PingQueryResolver.java", "PongQueryResolver.java",
                "QueryResolver.java")), generatedFileNames);
    }

    @Test
    void generate_InterfacePerSchemaAndFolderNameAsPrefix() throws Exception {
        mappingConfig.setApiNamePrefixStrategy(ApiNamePrefixStrategy.FOLDER_NAME_AS_PREFIX);
        mappingConfig.setApiRootInterfaceStrategy(ApiRootInterfaceStrategy.INTERFACE_PER_SCHEMA);
        new GraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo())
                .generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("SubProj1PingQueryResolver.java", "SubProj2PongQueryResolver.java",
                "SubProj1QueryResolver.java", "SubProj2QueryResolver.java")), generatedFileNames);
    }

    @Test
    void generate_InterfacePerSchemaAndFileNameAsPrefix() throws Exception {
        mappingConfig.setApiNamePrefixStrategy(ApiNamePrefixStrategy.FILE_NAME_AS_PREFIX);
        mappingConfig.setApiRootInterfaceStrategy(ApiRootInterfaceStrategy.INTERFACE_PER_SCHEMA);
        new GraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo())
                .generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("SubSchema1PingQueryResolver.java", "SubSchema2PongQueryResolver.java",
                "SubSchema1QueryResolver.java", "SubSchema2QueryResolver.java")), generatedFileNames);
    }

    @Test
    void generate_InterfacePerSchemaAndConstantPrefix() throws IOException {
        mappingConfig.setApiNamePrefixStrategy(ApiNamePrefixStrategy.CONSTANT);
        mappingConfig.setApiRootInterfaceStrategy(ApiRootInterfaceStrategy.INTERFACE_PER_SCHEMA);
        GeneratedInformation generatedInformation = TestUtils.getStaticGeneratedInfo();
        List<String> schemas = schemaFinder.findSchemas();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new GraphQLCodegen(schemas, outputBuildDir, mappingConfig, generatedInformation));
    }

}