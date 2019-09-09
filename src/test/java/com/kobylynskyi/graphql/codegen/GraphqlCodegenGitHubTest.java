package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphqlCodegenGitHubTest {

    private GraphqlCodegen generator;

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/github/graphql");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void init() {
        mappingConfig.setPackageName("com.github.graphql");
        generator = new GraphqlCodegen(Collections.singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig);
    }

    //@AfterEach
    void cleanup() throws IOException {
        Utils.deleteDir(new File("build/generated"));
    }

    @Test
    void generate_MultipleInterfacesPerType() throws Exception {
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        File commitFile = Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase("Commit.java"))
                .findFirst().orElseThrow(FileNotFoundException::new);
        assertEquals(Utils.getFileContent(new File("src/test/resources/expected-classes/Commit.java.txt").getPath()),
                Utils.getFileContent(commitFile.getPath()));


        File profileOwner = Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase("ProfileOwner.java"))
                .findFirst().orElseThrow(FileNotFoundException::new);
        assertEquals(Utils.getFileContent(new File("src/test/resources/expected-classes/ProfileOwner.java.txt").getPath()),
                Utils.getFileContent(profileOwner.getPath()));
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
        assertThat(getGeneratedFileContent(files, "GithubChangeUserStatusInputTO.java"),
                StringContains.containsString("public class GithubChangeUserStatusInputTO "));

        // verify proper class name for GraphQL type and references to interfaces/types/unions for GraphQL type
        assertEquals(Utils.getFileContent(new File("src/test/resources/expected-classes/GithubCommitTO.java.txt").getPath()),
                getGeneratedFileContent(files, "GithubCommitTO.java"));
    }

    private static String getGeneratedFileContent(File[] files, String fileName) throws IOException {
        File file = Arrays.stream(files).filter(f -> f.getName().equalsIgnoreCase(fileName))
                .findFirst().orElseThrow(FileNotFoundException::new);
        return Utils.getFileContent(file.getPath());
    }

}