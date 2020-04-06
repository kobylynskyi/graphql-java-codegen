package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GraphqlCodegenRequestTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/github/graphql");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void init() {
        mappingConfig.setPackageName("com.github.graphql");
        mappingConfig.setGenerateRequests(true);
        mappingConfig.setGenerateToString(false); // should be overridden to true
        mappingConfig.setGenerateApis(false);
    }

    @AfterEach
    void cleanup() throws IOException {
        Utils.deleteDir(new File("build/generated"));
    }

    @Test
    void generate_RequestAndResponseProjections() throws Exception {
        new GraphqlCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventResponseProjection.java.txt"),
                getGeneratedFile(files, "EventResponseProjection.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventPropertyResponseProjection.java.txt"),
                getGeneratedFile(files, "EventPropertyResponseProjection.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventsByCategoryAndStatusQueryRequest.java.txt"),
                getGeneratedFile(files, "EventsByCategoryAndStatusQueryRequest.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/VersionQueryRequest.java.txt"),
                getGeneratedFile(files, "VersionQueryRequest.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventsByIdsQueryRequest.java.txt"),
                getGeneratedFile(files, "EventsByIdsQueryRequest.java"));
    }

    @Test
    void generate_WithModelSuffix() throws Exception {
        mappingConfig.setModelNameSuffix("TO");
        new GraphqlCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventStatusTO.java.txt"),
                getGeneratedFile(files, "EventStatusTO.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventsByCategoryAndStatusQueryRequest_withModelSuffix.java.txt"),
                getGeneratedFile(files, "EventsByCategoryAndStatusQueryRequest.java"));
    }

    @Test
    void generate_RequestAndResponseProjections_github() throws Exception {
        new GraphqlCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/CodeOfConductResponseProjection.java.txt"),
                getGeneratedFile(files, "CodeOfConductResponseProjection.java"));

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/UpdateRepositoryMutationRequest.java.txt"),
                getGeneratedFile(files, "UpdateRepositoryMutationRequest.java"));
    }

    @Test
    void generate_ToStringIsEnabledForInput() throws Exception {
        new GraphqlCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/AcceptTopicSuggestionInput.java.txt"),
                getGeneratedFile(files, "AcceptTopicSuggestionInput.java"));
    }

    @Test
    void generate_emptyRequestSuffix() throws Exception {
        mappingConfig.setRequestSuffix("");
        new GraphqlCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertNotNull(getGeneratedFile(files, "EventsByCategoryAndStatusQuery.java"));
    }

    private static File getGeneratedFile(File[] files, String fileName) throws FileNotFoundException {
        return Arrays.stream(files)
                .filter(f -> f.getName().equalsIgnoreCase(fileName))
                .findFirst()
                .orElseThrow(FileNotFoundException::new);
    }
}