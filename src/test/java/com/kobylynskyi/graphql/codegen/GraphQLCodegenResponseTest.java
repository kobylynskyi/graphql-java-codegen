package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Collections.singletonList;

class GraphQLCodegenResponseTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/github/graphql");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void init() {
        mappingConfig.setPackageName("com.github.graphql");
        mappingConfig.setResponseSuffix("Response");
        mappingConfig.setGenerateClient(true);
        mappingConfig.setGenerateApis(false);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(new File("build/generated"));
    }

    @Test
    void generate_RequestAndResponseProjections() throws Exception {
        mappingConfig.setModelNameSuffix("TO");
        new GraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/response/EventsByCategoryAndStatusQueryResponse.java.txt"),
                getFileByName(files, "EventsByCategoryAndStatusQueryResponse.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/response/VersionQueryResponse.java.txt"),
                getFileByName(files, "VersionQueryResponse.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/response/EventsByIdsQueryResponse.java.txt"),
                getFileByName(files, "EventsByIdsQueryResponse.java"));
    }

}