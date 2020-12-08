package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;

class GraphQLCodegenGitHubTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/github/graphql");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void init() {
        mappingConfig.setGenerateParameterizedFieldsResolvers(false);
        mappingConfig.setPackageName("com.github.graphql");
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_MultipleInterfacesPerType() throws Exception {
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Commit.java.txt"),
                getFileByName(files, "Commit.java"));

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/ProfileOwner.java.txt"),
                getFileByName(files, "ProfileOwner.java"));
    }

    @Test
    void generate_ClassNameWithSuffix_Prefix() throws Exception {
        mappingConfig.setModelNamePrefix("Github");
        mappingConfig.setModelNameSuffix("TO");

        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        // verify proper class name for GraphQL interface
        assertThat(getFileContent(files, "GithubActorTO.java"),
                StringContains.containsString("public interface GithubActorTO "));

        // verify proper class name for GraphQL enum
        assertThat(getFileContent(files, "GithubIssueStateTO.java"),
                StringContains.containsString("public enum GithubIssueStateTO "));

        // verify proper class name for GraphQL union
        assertThat(getFileContent(files, "GithubAssigneeTO.java"),
                StringContains.containsString("public interface GithubAssigneeTO "));

        // verify proper class name for GraphQL input
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/GithubAcceptTopicSuggestionInputTO.java.txt"),
                getFileByName(files, "GithubAcceptTopicSuggestionInputTO.java"));

        // verify proper class name for GraphQL type and references to interfaces/types/unions for GraphQL type
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/GithubCommitTO.java.txt"),
                getFileByName(files, "GithubCommitTO.java"));
    }

    @Test
    void generate_NoValidationAnnotation() throws Exception {
        mappingConfig.setModelValidationAnnotation("");

        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File commitFile = getFileByName(Objects.requireNonNull(outputJavaClassesDir.listFiles()), "Commit.java");
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Commit_noValidationAnnotation.java.txt"),
                commitFile);
    }

    @Test
    void generate_Client_ConditionalFragments() throws Exception {
        mappingConfig.setGenerateClient(true);
        mappingConfig.setGenerateApis(false);

        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/response/SearchResultItemConnectionResponseProjection.java.txt"),
                getFileByName(files, "SearchResultItemConnectionResponseProjection.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/response/SearchResultItemResponseProjection.java.txt"),
                getFileByName(files, "SearchResultItemResponseProjection.java"));
    }

    @Test
    void generate_NoPrimitives() throws Exception {
        mappingConfig.putCustomTypeMappingIfAbsent("Int!", "Integer");
        mappingConfig.putCustomTypeMappingIfAbsent("Float!", "Double");
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean!", "Boolean");
        mappingConfig.setUseOptionalForNullableReturnTypes(true);

        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Commit_withoutPrimitives.java.txt"),
                getFileByName(files, "Commit.java"));
    }

    private static String getFileContent(File[] files, String fileName) throws IOException {
        return Utils.getFileContent(getFileByName(files, fileName).getPath());
    }

}