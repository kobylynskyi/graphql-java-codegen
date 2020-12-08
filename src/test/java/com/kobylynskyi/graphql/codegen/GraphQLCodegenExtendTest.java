package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.supplier.SchemaFinder;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphQLCodegenExtendTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated");
    private final MappingConfig mappingConfig = new MappingConfig();
    private final SchemaFinder schemaFinder = new SchemaFinder(Paths.get("src/test/resources"));

    @BeforeEach
    void init() {
        schemaFinder.setIncludePattern("extend.*\\.graphqls");
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generateServerSideClasses() throws Exception {
        new JavaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("MutationResolver.java", "QueryResolver.java",
                "EventsQueryResolver.java", "AssetsQueryResolver.java",
                "CreateEventMutationResolver.java", "CreateAssetMutationResolver.java",
                "Event.java", "Asset.java", "EventInput.java", "AssetInput.java",
                "Node.java", "Status.java", "PinnableItem.java")), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format("src/test/resources/expected-classes/extend/%s.txt", file.getName())),
                    file);
        }
    }

    @Test
    void generate_onlyExtend() throws Exception {
        schemaFinder.setIncludePattern("only-extend-queries.*\\.graphqls");
        new JavaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        assertEquals(new HashSet<>(asList("SubscriptionResolver.java", "UserQueryResolver.java", "User.java",
                "UsersCreatedSubscriptionResolver.java", "CreateUserMutationResolver.java", "MutationResolver.java", "QueryResolver.java",
                "UserInput.java")), Arrays.stream(Objects.requireNonNull(outputJavaClassesDir.listFiles()))
                .map(File::getName).collect(toSet()));
    }

    @Test
    void generateServerSideClasses_ExtensionFieldsResolvers() throws Exception {
        mappingConfig.setGenerateExtensionFieldsResolvers(true);
        new JavaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("MutationResolver.java", "QueryResolver.java",
                "EventsQueryResolver.java", "AssetsQueryResolver.java",
                "EventResolver.java", "NodeResolver.java",
                "CreateEventMutationResolver.java", "CreateAssetMutationResolver.java",
                "Event.java", "Asset.java", "EventInput.java", "AssetInput.java",
                "Node.java", "Status.java", "PinnableItem.java")), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format("src/test/resources/expected-classes/extend-with-resolvers/%s.txt", file.getName())),
                    file);
        }
    }

    @Test
    void generateServerSideClasses_ExtensionFieldsResolvers_WithExclusions() throws Exception {
        mappingConfig.setGenerateExtensionFieldsResolvers(true);
        mappingConfig.setFieldsWithoutResolvers(new HashSet<>(asList("Node", "Event.assets")));
        new JavaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("MutationResolver.java", "QueryResolver.java",
                "EventsQueryResolver.java", "AssetsQueryResolver.java",
                "CreateEventMutationResolver.java", "CreateAssetMutationResolver.java",
                "Event.java", "Asset.java", "EventInput.java", "AssetInput.java",
                "Node.java", "Status.java", "PinnableItem.java")), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format("src/test/resources/expected-classes/extend/%s.txt", file.getName())),
                    file);
        }
    }

    @Test
    void generateClientSideClasses() throws Exception {
        mappingConfig.setGenerateApis(false);
        mappingConfig.setGenerateClient(true);
        new JavaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/extend/request/EventResponseProjection.java.txt"),
                getFileByName(files, "EventResponseProjection.java"));

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/extend/request/AssetResponseProjection.java.txt"),
                getFileByName(files, "AssetResponseProjection.java"));
    }

    @Test
    void generateServerSideClasses_EmptyTypes() throws Exception {
        schemaFinder.setIncludePattern("empty-types-with-extend\\.graphqls");
        new JavaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("MutationResolver.java", "QueryResolver.java",
                "EventsQueryResolver.java", "AssetsQueryResolver.java",
                "CreateEventMutationResolver.java", "CreateAssetMutationResolver.java",
                "Event.java", "Asset.java", "EventInput.java", "AssetInput.java",
                "Node.java", "Status.java", "PinnableItem.java")), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format("src/test/resources/expected-classes/extend/%s.txt", file.getName())),
                    file);
        }
    }

}