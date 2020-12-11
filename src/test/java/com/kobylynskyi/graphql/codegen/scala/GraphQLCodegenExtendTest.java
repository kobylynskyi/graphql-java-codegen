package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
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
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.SCALA);
        schemaFinder.setIncludePattern("extend.*\\.graphqls");
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generateServerSideClasses() throws Exception {
        new ScalaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("MutationResolver.scala", "QueryResolver.scala",
                "EventsQueryResolver.scala", "AssetsQueryResolver.scala",
                "CreateEventMutationResolver.scala", "CreateAssetMutationResolver.scala",
                "Event.scala", "Asset.scala", "EventInput.scala", "AssetInput.scala",
                "Node.scala", "Status.scala", "PinnableItem.scala")), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format("src/test/resources/expected-classes/scala/extend/%s.txt", file.getName())),
                    file);
        }
    }

    @Test
    void generateServerSideClasses_ExtensionFieldsResolvers_WithExclusions() throws Exception {
        mappingConfig.setGenerateExtensionFieldsResolvers(true);
        mappingConfig.setFieldsWithoutResolvers(new HashSet<>(asList("Node", "Event.assets")));
        new ScalaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("MutationResolver.scala", "QueryResolver.scala",
                "EventsQueryResolver.scala", "AssetsQueryResolver.scala",
                "CreateEventMutationResolver.scala", "CreateAssetMutationResolver.scala",
                "Event.scala", "Asset.scala", "EventInput.scala", "AssetInput.scala",
                "Node.scala", "Status.scala", "PinnableItem.scala")), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format("src/test/resources/expected-classes/scala/extend/%s.txt", file.getName())),
                    file);
        }

    }

    @Test
    void generateServerSideClasses_EmptyTypes() throws Exception {
        schemaFinder.setIncludePattern("empty-types-with-extend\\.graphqls");
        new ScalaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("MutationResolver.scala", "QueryResolver.scala",
                "EventsQueryResolver.scala", "AssetsQueryResolver.scala",
                "CreateEventMutationResolver.scala", "CreateAssetMutationResolver.scala",
                "Event.scala", "Asset.scala", "EventInput.scala", "AssetInput.scala",
                "Node.scala", "Status.scala", "PinnableItem.scala")), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format("src/test/resources/expected-classes/scala/extend/%s.txt", file.getName())),
                    file);
        }
    }

    @Test
    void generateClientSideClasses() throws Exception {
        mappingConfig.setGenerateApis(false);
        mappingConfig.setGenerateClient(true);
        new ScalaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/scala/extend/request/EventResponseProjection.scala.txt"),
                getFileByName(files, "EventResponseProjection.scala"));

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/scala/extend/request/AssetResponseProjection.scala.txt"),
                getFileByName(files, "AssetResponseProjection.scala"));
    }

    @Test
    void generatePrimitiveTypesResponseResolverClasses() throws Exception {
        mappingConfig.setGenerateApis(true);
        mappingConfig.setGenerateClient(true);
        schemaFinder.setIncludePattern("null-extend1.graphqls");
        new ScalaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/scala/extend/resolver/SimpleEventCountsQueryResponse.scala.txt"),
                getFileByName(files, "SimpleEventCountsQueryResponse.scala"));

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/scala/extend/resolver/SimpleEventCountsQueryResolver.scala.txt"),
                getFileByName(files, "SimpleEventCountsQueryResolver.scala"));

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/scala/extend/resolver/SimpleEventCountQueryResolver.scala.txt"),
                getFileByName(files, "SimpleEventCountQueryResolver.scala"));

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/scala/extend/resolver/SimpleEventCountQueryResponse.scala.txt"),
                getFileByName(files, "SimpleEventCountQueryResponse.scala"));
    }

    @Test
    void generatePrimitiveTypesResponseResolverClasses_With_SetUseOptionalForNullableReturnTypes() throws Exception {
        mappingConfig.setGenerateApis(true);
        mappingConfig.setGenerateClient(true);
        mappingConfig.setUseOptionalForNullableReturnTypes(true);
        schemaFinder.setIncludePattern("null-extend2.graphqls");
        new ScalaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/scala/extend/nullreturn/SimplesQueryResolver.scala.txt"),
                getFileByName(files, "SimplesQueryResolver.scala"));

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/scala/extend/nullreturn/SimplesQueryResponse.scala.txt"),
                getFileByName(files, "SimplesQueryResponse.scala"));
    }
}