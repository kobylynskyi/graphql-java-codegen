package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertFileContainsElements;
import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

class GraphQLCodegenGitHubTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputktClassesDir = new File("build/generated/com/github/graphql");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void init() {
        mappingConfig.setGenerateParameterizedFieldsResolvers(false);
        mappingConfig.setPackageName("com.github.graphql");
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.KOTLIN);
        mappingConfig.setGenerateToString(true);
        mappingConfig.setGenerateApis(true);
        mappingConfig.setGenerateClient(true);
        mappingConfig.setGenerateEqualsAndHashCode(true);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_MultipleInterfacesPerType() throws Exception {
        mappingConfig.putCustomTypeMappingIfAbsent("Int!", "Int");
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean!", "Boolean");
        mappingConfig.setUseOptionalForNullableReturnTypes(true);

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputktClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/Commit.kt.txt"),
                getFileByName(files, "Commit.kt"));

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/ProfileOwner.kt.txt"),
                getFileByName(files, "ProfileOwner.kt"));

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/kt/AcceptTopicSuggestionMutationResponse.kt.txt"),
                getFileByName(files, "AcceptTopicSuggestionMutationResponse.kt"));
    }

    @Test
    void generate_ClassNameWithSuffix_Prefix() throws Exception {
        mappingConfig.setModelNamePrefix("Github");
        mappingConfig.setModelNameSuffix("TO");
        mappingConfig.setGenerateImmutableModels(false);

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputktClassesDir.listFiles());

        // verify proper class name for GraphQL interface
        assertFileContainsElements(files, "GithubActorTO.kt", "interface GithubActorTO");

        // verify proper class name for GraphQL enum
        assertFileContainsElements(files, "GithubIssueStateTO.kt", "enum class GithubIssueStateTO");

        // verify proper class name for GraphQL union
        assertFileContainsElements(files, "GithubAssigneeTO.kt", "interface GithubAssigneeTO");

        // verify proper class name for GraphQL input
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/kt/GithubAcceptTopicSuggestionInputTO.kt.txt"),
                getFileByName(files, "GithubAcceptTopicSuggestionInputTO.kt"));

        // verify proper class name for GraphQL type and references to interfaces/types/unions for GraphQL type
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/GithubCommitTO.kt.txt"),
                getFileByName(files, "GithubCommitTO.kt"));
    }

    @Test
    void generate_Client_ConditionalFragments() throws Exception {
        mappingConfig.setGenerateClient(true);
        mappingConfig.setGenerateApis(false);

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputktClassesDir.listFiles());
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/kt/SearchResultItemConnectionResponseProjection.kt.txt"),
                getFileByName(files, "SearchResultItemConnectionResponseProjection.kt"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/kt/SearchResultItemResponseProjection.kt.txt"),
                getFileByName(files, "SearchResultItemResponseProjection.kt"));
    }

    @Test
    void generate_ResponseWithPrimitiveType() throws Exception {
        mappingConfig.putCustomTypeMappingIfAbsent("Int!", "Int");
        mappingConfig.putCustomTypeMappingIfAbsent("Int", "Int?");
        generate("src/test/resources/schemas/primitive-query-response-type.graphqls");

        File[] files = Objects.requireNonNull(outputktClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/VersionQueryResponse_int.kt.txt"),
                getFileByName(files, "VersionQueryResponse.kt"));
    }

    @Test
    void generate_ktList() throws Exception {
        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputktClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/AddLabelsToLabelableInput.kt.txt"),
                getFileByName(files, "AddLabelsToLabelableInput.kt"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/kt/AddLabelsToLabelableMutationRequest.kt.txt"),
                getFileByName(files, "AddLabelsToLabelableMutationRequest.kt"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/kt/AddLabelsToLabelableMutationResolver.kt.txt"),
                getFileByName(files, "AddLabelsToLabelableMutationResolver.kt"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/kt/AddLabelsToLabelableMutationResponse.kt.txt"),
                getFileByName(files, "AddLabelsToLabelableMutationResponse.kt"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/AddLabelsToLabelablePayload.kt.txt"),
                getFileByName(files, "AddLabelsToLabelablePayload.kt"));

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/CodesOfConductQueryResolver.kt.txt"),
                getFileByName(files, "CodesOfConductQueryResolver.kt"));
    }

    @Test
    void generate_Var_Field() throws Exception {
        mappingConfig.setGenerateImmutableModels(false);

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputktClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/Commit_Var_Field.kt.txt"),
                getFileByName(files, "Commit.kt"));
    }

    @Test
    void generate_CustomFieldsResolvers() throws Exception {
        mappingConfig.setModelNamePrefix("Github");
        mappingConfig.setModelNameSuffix("TO");
        mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(true);
        mappingConfig.setFieldsWithResolvers(Collections.singleton("AcceptTopicSuggestionPayload.topic"));

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputktClassesDir.listFiles());

        assertSameTrimmedContent(new File(
                        "src/test/resources/expected-classes/kt/field-resolver/" +
                                "GithubAcceptTopicSuggestionPayloadTO.kt.txt"),
                getFileByName(files, "GithubAcceptTopicSuggestionPayloadTO.kt"));
        assertSameTrimmedContent(new File(
                        "src/test/resources/expected-classes/kt/field-resolver/" +
                                "AcceptTopicSuggestionPayloadResolver.kt.txt"),
                getFileByName(files, "AcceptTopicSuggestionPayloadResolver.kt"));
    }

    @Test
    void generate_CustomFieldsResolversWithAnnotation() throws Exception {
        mappingConfig.setModelNamePrefix("Github");
        mappingConfig.setModelNameSuffix("TO");
        mappingConfig.setApiNameSuffix("WithAnnotation");
        mappingConfig.setResolverArgumentAnnotations(singleton("some.Annotation"));
        mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(true);
        mappingConfig.setFieldsWithResolvers(Collections.singleton("AcceptTopicSuggestionPayload.topic"));

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputktClassesDir.listFiles());

        assertSameTrimmedContent(new File(
                        "src/test/resources/expected-classes/kt/field-resolver/" +
                                "AcceptTopicSuggestionMutationWithAnnotation.kt.txt"),
                getFileByName(files, "AcceptTopicSuggestionMutationWithAnnotation.kt"));
    }

    @Test
    void generate_CustomFieldsResolversWithMultipleAnnotations() throws Exception {
        Set<String> annotations = new HashSet<>();
        annotations.add("some.Annotation");
        annotations.add("another.Annotation");

        mappingConfig.setModelNamePrefix("Github");
        mappingConfig.setModelNameSuffix("TO");
        mappingConfig.setApiNameSuffix("WithAnnotations");
        mappingConfig.setResolverArgumentAnnotations(annotations);
        mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(true);
        mappingConfig.setFieldsWithResolvers(Collections.singleton("AcceptTopicSuggestionPayload.topic"));

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputktClassesDir.listFiles());

        assertSameTrimmedContent(new File(
                        "src/test/resources/expected-classes/kt/field-resolver/" +
                                "AcceptTopicSuggestionMutationWithAnnotations.kt.txt"),
                getFileByName(files, "AcceptTopicSuggestionMutationWithAnnotations.kt"));
    }

    @Test
    void generate_RequestWithDefaultValue() throws Exception {
        mappingConfig.setGenerateBuilder(true);
        mappingConfig.setGenerateClient(true);

        generate("src/test/resources/schemas/kt/default.graphqls");

        File[] files = Objects.requireNonNull(outputktClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/default/" +
                        "FriendsQueryRequest.kt.txt"),
                getFileByName(files, "FriendsQueryRequest.kt"));
    }

    private void generate(String path) throws IOException {
        new KotlinGraphQLCodegen(singletonList(path),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo(mappingConfig)).generate();
    }

}
