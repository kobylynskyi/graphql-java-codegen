package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static org.hamcrest.MatcherAssert.assertThat;

class GraphQLCodegenGitHubTest {

    private GraphQLCodegen generator;

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/github/graphql");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void init() {
        mappingConfig.setGenerateParameterizedFieldsResolvers(false);
        mappingConfig.setPackageName("com.github.graphql");
        generator = new GraphQLCodegen(Collections.singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig);
    }

    @AfterEach
    void cleanup() throws IOException {
        Utils.deleteDir(new File("build/generated"));
    }

    @Test
    void generate_MultipleInterfacesPerType() throws Exception {
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        File commitFile = getGeneratedFile(files, "Commit.java");
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Commit.java.txt"), commitFile);

        File profileOwner = getGeneratedFile(files, "ProfileOwner.java");
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/ProfileOwner.java.txt"), profileOwner);
    }

    @Test
    void generate_ClassNameWithSuffix_Prefix() throws Exception {
        mappingConfig.setModelNamePrefix("Github");
        mappingConfig.setModelNameSuffix("TO");

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        // verify proper class name for GraphQL interface
        assertThat(getGeneratedFileContent(files, "GithubActorTO.java"),
                StringContains.containsString("public interface GithubActorTO "));

        // verify proper class name for GraphQL enum
        assertThat(getGeneratedFileContent(files, "GithubIssueStateTO.java"),
                StringContains.containsString("public enum GithubIssueStateTO "));

        // verify proper class name for GraphQL union
        assertThat(getGeneratedFileContent(files, "GithubAssigneeTO.java"),
                StringContains.containsString("public interface GithubAssigneeTO "));

        // verify proper class name for GraphQL input
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/GithubAcceptTopicSuggestionInputTO.java.txt"),
                getGeneratedFile(files, "GithubAcceptTopicSuggestionInputTO.java"));

        // verify proper class name for GraphQL type and references to interfaces/types/unions for GraphQL type
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/GithubCommitTO.java.txt"),
                getGeneratedFile(files, "GithubCommitTO.java"));
    }

    @Test
    void generate_NoValidationAnnotation() throws Exception {
        mappingConfig.setModelValidationAnnotation(null);

        generator.generate();
        File commitFile = getGeneratedFile(Objects.requireNonNull(outputJavaClassesDir.listFiles()), "Commit.java");
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Commit_noValidationAnnotation.java.txt"),
                commitFile);
    }

    private static String getGeneratedFileContent(File[] files, String fileName) throws IOException {
        File file = getGeneratedFile(files, fileName);
        return Utils.getFileContent(file.getPath());
    }

    private static File getGeneratedFile(File[] files, String fileName) throws FileNotFoundException {
        return Arrays.stream(files)
                     .filter(f -> f.getName().equalsIgnoreCase(fileName))
                     .findFirst()
                     .orElseThrow(FileNotFoundException::new);
    }
}