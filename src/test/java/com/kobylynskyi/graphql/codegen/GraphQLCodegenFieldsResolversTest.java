package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

class GraphQLCodegenFieldsResolversTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/github/graphql");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void init() {
        mappingConfig.setPackageName("com.github.graphql");
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_ParametrizedFields() throws Exception {
        mappingConfig.setGenerateParameterizedFieldsResolvers(true);
        mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(true);
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap(
                "Commit.blame", "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)")));

        new GraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Commit_noParametrizedFields.java.txt"),
                getFileByName(files, "Commit.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/CommitResolver.java.txt"),
                getFileByName(files, "CommitResolver.java"));
    }

    @Test
    void generate_CustomFieldsResolvers() throws Exception {
        mappingConfig.setModelNamePrefix("Github");
        mappingConfig.setModelNameSuffix("TO");
        mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(true);
        mappingConfig.setFieldsWithResolvers(Collections.singleton("AcceptTopicSuggestionPayload.topic"));

        new GraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/GithubAcceptTopicSuggestionPayloadTO.java.txt"),
                getFileByName(files, "GithubAcceptTopicSuggestionPayloadTO.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/AcceptTopicSuggestionPayloadResolver.java.txt"),
                getFileByName(files, "AcceptTopicSuggestionPayloadResolver.java"));
    }

    @Test
    void generate_ResolverForWholeType() throws Exception {
        mappingConfig.setFieldsWithResolvers(Collections.singleton("CommentDeletedEvent"));

        new GraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/CommentDeletedEventResolver.java.txt"),
                getFileByName(files, "CommentDeletedEventResolver.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/CommentDeletedEvent.java.txt"),
                getFileByName(files, "CommentDeletedEvent.java"));
    }

}