package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static java.util.Collections.singletonMap;

class GraphQLCodegenFieldsResolversTest {

    private GraphQLCodegen generator;

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/github/graphql");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void init() {
        mappingConfig.setPackageName("com.github.graphql");
        generator = new GraphQLCodegen(Collections.singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(new File("build/generated"));
    }

    @Test
    void generate_ParametrizedFields() throws Exception {
        mappingConfig.setGenerateParameterizedFieldsResolvers(true);
        mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(true);
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap(
                "Commit.blame", "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)")));

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Commit_noParametrizedFields.java.txt"),
                getGeneratedFile(files, "Commit.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/CommitResolver.java.txt"),
                getGeneratedFile(files, "CommitResolver.java"));
    }

    @Test
    void generate_CustomFieldsResolvers() throws Exception {
        mappingConfig.setModelNamePrefix("Github");
        mappingConfig.setModelNameSuffix("TO");
        mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(true);
        mappingConfig.setFieldsWithResolvers(Collections.singleton("AcceptTopicSuggestionPayload.topic"));

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/GithubAcceptTopicSuggestionPayloadTO.java.txt"),
                getGeneratedFile(files, "GithubAcceptTopicSuggestionPayloadTO.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/AcceptTopicSuggestionPayloadResolver.java.txt"),
                getGeneratedFile(files, "AcceptTopicSuggestionPayloadResolver.java"));
    }

    @Test
    void generate_ResolverForWholeType() throws Exception {
        mappingConfig.setFieldsWithResolvers(Collections.singleton("CommentDeletedEvent"));

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/CommentDeletedEventResolver.java.txt"),
                getGeneratedFile(files, "CommentDeletedEventResolver.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/CommentDeletedEvent.java.txt"),
                getGeneratedFile(files, "CommentDeletedEvent.java"));
    }

    private static File getGeneratedFile(File[] files, String fileName) throws FileNotFoundException {
        return Arrays.stream(files)
                .filter(f -> f.getName().equalsIgnoreCase(fileName))
                .findFirst()
                .orElseThrow(FileNotFoundException::new);
    }
}