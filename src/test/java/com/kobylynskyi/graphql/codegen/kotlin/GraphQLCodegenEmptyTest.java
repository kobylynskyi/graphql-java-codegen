package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.scala.ScalaGraphQLCodegen;
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

class GraphQLCodegenEmptyTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated");
    private final MappingConfig mappingConfig = new MappingConfig();
    private final SchemaFinder schemaFinder = new SchemaFinder(Paths.get("src/test/resources"));

    @BeforeEach
    void init() {
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.KOTLIN);
        schemaFinder.setIncludePattern("empty-types.graphqls");
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
        assertEquals(new HashSet<>(asList("Node.kt", "Event.kt", "MutationResolver.kt",
                "EventInput.kt", "QueryResolver.kt", "Status.kt", "PinnableItem.kt")),
                generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format("src/test/resources/expected-classes/kt/empty/%s.txt", file.getName())),
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
                new File("src/test/resources/expected-classes/kt/empty/EventResponseProjection.kt.txt"),
                getFileByName(files, "EventResponseProjection.kt"));
    }

}