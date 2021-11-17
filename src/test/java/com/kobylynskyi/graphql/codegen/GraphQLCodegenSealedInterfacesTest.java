package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;

public class GraphQLCodegenSealedInterfacesTest {
    private final MappingConfig mappingConfig = new MappingConfig();
    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/interfaces");
    private GraphQLCodegen generator;

    @BeforeEach
    void init() {
        mappingConfig.setPackageName("com.kobylynskyi.graphql.interfaces");
        mappingConfig.setGenerateSealedInterfaces(true);
        generator = new JavaGraphQLCodegen(Collections.singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo());
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_CheckFiles() throws Exception {
        generator.generate();
        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Comment_sealed_interfaces.txt"),
                getFileByName(files, "Comment.java"));
    }
}
