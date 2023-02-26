package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
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
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Commit.blame",
                singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = com.example.json.DateTimeScalarDeserializer.class)"))));

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/resolvers/" +
                        "Commit_noParametrizedFields.java.txt"),
                getFileByName(files, "Commit.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/resolvers/" +
                        "CommitResolver.java.txt"),
                getFileByName(files, "CommitResolver.java"));
    }

    @Test
    void generate_ParametrizedFieldsInInterface() throws Exception {
        mappingConfig.setGenerateClient(true);
        mappingConfig.setGenerateApis(false);

        generate("src/test/resources/schemas/parametrized-input-client.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/" +
                        "ProductLinkCodeParametrizedInput.java.txt"),
                getFileByName(files, "ProductLinkCodeParametrizedInput.java"));
    }

    @Test
    void generate_ParametrizedFieldsInInterface_DoNotGenerateResolvers() throws Exception {
        mappingConfig.setGenerateClient(true);
        mappingConfig.setGenerateApis(false);
        mappingConfig.setGenerateParameterizedFieldsResolvers(false);

        generate("src/test/resources/schemas/parametrized-input-client.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/resolvers/A.java.txt"),
                getFileByName(files, "A.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/resolvers/B.java.txt"),
                getFileByName(files, "B.java"));
    }

    @Test
    void generate_CustomFieldsResolvers() throws Exception {
        mappingConfig.setModelNamePrefix("Github");
        mappingConfig.setModelNameSuffix("TO");
        mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(true);
        mappingConfig.setFieldsWithResolvers(Collections.singleton("AcceptTopicSuggestionPayload.topic"));

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/" +
                        "GithubAcceptTopicSuggestionPayloadTO.java.txt"),
                getFileByName(files, "GithubAcceptTopicSuggestionPayloadTO.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/resolvers/" +
                        "AcceptTopicSuggestionPayloadResolver.java.txt"),
                getFileByName(files, "AcceptTopicSuggestionPayloadResolver.java"));
    }

    @Test
    void generate_ResolverForWholeType() throws Exception {
        mappingConfig.setFieldsWithResolvers(Collections.singleton("CommentDeletedEvent"));

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/resolvers/" +
                        "CommentDeletedEventResolver.java.txt"),
                getFileByName(files, "CommentDeletedEventResolver.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/resolvers/" +
                        "CommentDeletedEvent.java.txt"),
                getFileByName(files, "CommentDeletedEvent.java"));
    }

    @Test
    void generate_FieldResolversViaDirective() throws Exception {
        mappingConfig.setFieldsWithResolvers(Collections.singleton("@customResolver"));
        mappingConfig.setFieldsWithoutResolvers(Collections.singleton("@noResolver"));

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/resolvers/EventResolver.java.txt"),
                getFileByName(files, "EventResolver.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/resolvers/Event.java.txt"),
                getFileByName(files, "Event.java"));

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/resolvers/" +
                        "EventPropertyResolver.java.txt"),
                getFileByName(files, "EventPropertyResolver.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/resolvers/EventProperty.java.txt"),
                getFileByName(files, "EventProperty.java"));
    }

    private void generate(String o) throws IOException {
        new JavaGraphQLCodegen(singletonList(o),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo(mappingConfig)).generate();
    }

}