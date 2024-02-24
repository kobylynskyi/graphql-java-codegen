package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MaxQueryTokensExtension.class)
class GraphQLCodegenInputWrapperTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void before() {
        mappingConfig.setUseWrapperForNullableInputTypes(true);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_CheckFiles() throws Exception {
        generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(asList(
                "InputWithDefaults.java",
                "MyEnum.java",
                "SomeObject.java"
        ), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(new File(
                            String.format("src/test/resources/expected-classes/input-wrapper/%s.txt", file.getName())),
                    file);
        }
    }

    private void generate() throws IOException {
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/input-wrapper.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo(mappingConfig)).generate();
    }

}