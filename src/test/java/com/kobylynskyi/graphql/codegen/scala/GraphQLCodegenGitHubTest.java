package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.GraphQLCodegen;
import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
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
    private final File outputScalaClassesDir = new File("build/generated/com/github/graphql");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void init() {
        mappingConfig.setGenerateParameterizedFieldsResolvers(false);
        mappingConfig.setPackageName("com.github.graphql");
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.SCALA);
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

        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();
        File[] files = Objects.requireNonNull(outputScalaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/Commit.scala.txt"),
                getFileByName(files, "Commit.scala"));

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/ProfileOwner.scala.txt"),
                getFileByName(files, "ProfileOwner.scala"));
    }

    @Test
    void generate_ClassNameWithSuffix_Prefix() throws Exception {
        mappingConfig.setModelNamePrefix("Github");
        mappingConfig.setModelNameSuffix("TO");

        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputScalaClassesDir.listFiles());

        // verify proper class name for GraphQL interface
        assertThat(getFileContent(files, "GithubActorTO.scala"),
                StringContains.containsString("trait GithubActorTO "));

        // verify proper class name for GraphQL enum
        assertThat(getFileContent(files, "GithubIssueStateTO.scala"),
                StringContains.containsString("object GithubIssueStateTO "));

        // verify proper class name for GraphQL union
        assertThat(getFileContent(files, "GithubAssigneeTO.scala"),
                StringContains.containsString("trait GithubAssigneeTO "));

        // verify proper class name for GraphQL input
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/scala/GithubAcceptTopicSuggestionInputTO.scala.txt"),
                getFileByName(files, "GithubAcceptTopicSuggestionInputTO.scala"));

        // verify proper class name for GraphQL type and references to interfaces/types/unions for GraphQL type
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/GithubCommitTO.scala.txt"),
                getFileByName(files, "GithubCommitTO.scala"));
    }

    @Test
    void generate_NoValidationAnnotation() throws Exception {
        mappingConfig.setModelValidationAnnotation("");

        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File commitFile = getFileByName(Objects.requireNonNull(outputScalaClassesDir.listFiles()), "Commit.scala");
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/Commit_noValidationAnnotation.scala.txt"),
                commitFile);
    }

    @Test
    void generate_Client_ConditionalFragments() throws Exception {
        mappingConfig.setGenerateClient(true);
        mappingConfig.setGenerateApis(false);

        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputScalaClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/SearchResultItemConnectionResponseProjection.scala.txt"),
                getFileByName(files, "SearchResultItemConnectionResponseProjection.scala"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/SearchResultItemResponseProjection.scala.txt"),
                getFileByName(files, "SearchResultItemResponseProjection.scala"));
    }

    @Test
    void generate_NoPrimitives() throws Exception {
        mappingConfig.putCustomTypeMappingIfAbsent("Int!", "Integer");
        mappingConfig.putCustomTypeMappingIfAbsent("Float!", "java.lang.Double");
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean!", "java.lang.Boolean");
        mappingConfig.setUseOptionalForNullableReturnTypes(true);

        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputScalaClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/Commit_withoutPrimitives.scala.txt"),
                getFileByName(files, "Commit.scala"));
    }

    @Test
    void generate_Primitives() throws Exception {
        mappingConfig.putCustomTypeMappingIfAbsent("Int!", "Int");
        mappingConfig.putCustomTypeMappingIfAbsent("Float!", "Double");
        mappingConfig.putCustomTypeMappingIfAbsent("Boolean!", "Boolean");
        mappingConfig.setUseOptionalForNullableReturnTypes(true);

        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputScalaClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/Commit_withPrimitives.scala.txt"),
                getFileByName(files, "Commit.scala"));
    }

    private static String getFileContent(File[] files, String fileName) throws IOException {
        return Utils.getFileContent(getFileByName(files, fileName).getPath());
    }

    @Test
    void generate_ResponseWithPrimitiveType() throws Exception {
        mappingConfig.putCustomTypeMappingIfAbsent("Int!", "Int");
        mappingConfig.putCustomTypeMappingIfAbsent("Int", "Int");
        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/primitive-query-response-type.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputScalaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/VersionQueryResponse_int.scala.txt"),
                getFileByName(files, "VersionQueryResponse.scala"));
    }

    @Test
    void generate_ScalaList() throws Exception {
        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputScalaClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/AddLabelsToLabelableInput.scala.txt"),
                getFileByName(files, "AddLabelsToLabelableInput.scala"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/AddLabelsToLabelableMutationRequest.scala.txt"),
                getFileByName(files, "AddLabelsToLabelableMutationRequest.scala"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/AddLabelsToLabelableMutationResolver.scala.txt"),
                getFileByName(files, "AddLabelsToLabelableMutationResolver.scala"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/AddLabelsToLabelableMutationResponse.scala.txt"),
                getFileByName(files, "AddLabelsToLabelableMutationResponse.scala"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/AddLabelsToLabelablePayload.scala.txt"),
                getFileByName(files, "AddLabelsToLabelablePayload.scala"));

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/CodesOfConductQueryResolver.scala.txt"),
                getFileByName(files, "CodesOfConductQueryResolver.scala"));
    }

    @Test
    void generate_Var_Field() throws Exception {
        mappingConfig.setGenerateImmutableModels(false);

        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputScalaClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/Commit_Var_Field.scala.txt"),
                getFileByName(files, "Commit.scala"));
    }


}