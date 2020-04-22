package com.kobylynskyi.graphql.codegen;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphQLCodegenDefaultsTest {

    private GraphQLCodegen generator;

    private File outputBuildDir = new File("build/generated");
    private File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/testdefaults");

    @BeforeEach
    void init() {
        MappingConfig mappingConfig = new MappingConfig();
        mappingConfig.setPackageName("com.kobylynskyi.graphql.testdefaults");
        generator = new GraphQLCodegen(Collections.singletonList("src/test/resources/schemas/defaults.graphqls"),
                outputBuildDir, mappingConfig);
    }

    @AfterEach
    void cleanup() throws IOException {
        Utils.deleteDir(new File("build/generated"));
    }

    @Test
    void generate_CheckFiles() throws Exception {
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("InputWithDefaults.java", "MyEnum.java", "SomeObject.java"), generatedFileNames);

        for (File file : files) {
            File expected = new File(String.format("src/test/resources/expected-classes/%s.txt", file.getName()));
            TestUtils.assertSameTrimmedContent(expected, file);
        }
    }
}
