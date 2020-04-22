package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.supplier.SchemaFinder;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
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
    void cleanup() throws IOException {
        Utils.deleteDir(new File("build/generated"));
    }

    @Test
    void generateServerSideClasses() throws Exception {
        new GraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("Mutation.java", "Query.java",
                "EventsQuery.java", "AssetsQuery.java",
                "CreateEventMutation.java", "CreateAssetMutation.java",
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
        mappingConfig.setGenerateRequests(true);
        new GraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        File eventResponseProjectionFile = Arrays.stream(files)
                .filter(file -> file.getName().equalsIgnoreCase("EventResponseProjection.java")).findFirst()
                .orElseThrow(FileNotFoundException::new);
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/extend/request/EventResponseProjection.java.txt"),
                eventResponseProjectionFile);

        File assetResponseProjectionFile = Arrays.stream(files)
                .filter(file -> file.getName().equalsIgnoreCase("AssetResponseProjection.java")).findFirst()
                .orElseThrow(FileNotFoundException::new);
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/extend/request/AssetResponseProjection.java.txt"),
                assetResponseProjectionFile);
    }

}