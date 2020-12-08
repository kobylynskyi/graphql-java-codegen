package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphQLCodegenDefaultsTest {

    private final MappingConfig mappingConfig = new MappingConfig();

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/testdefaults");

    @BeforeEach
    void init() {
        mappingConfig.setPackageName("com.kobylynskyi.graphql.testdefaults");
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_CheckFiles() throws Exception {
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/defaults.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("InputWithDefaults.java", "MyEnum.java", "SomeObject.java"), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(new File(String.format("src/test/resources/expected-classes/defaults/%s.txt",
                    file.getName())),
                    file);
        }
    }

    @Test
    void generate_CheckFiles_WithPrefixSuffix() throws Exception {
        mappingConfig.setModelNameSuffix("TO");

        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/defaults.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("InputWithDefaultsTO.java", "MyEnumTO.java", "SomeObjectTO.java"), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(new File(String.format("src/test/resources/expected-classes/defaults/%s.txt",
                    file.getName())),
                    file);
        }
    }

    @Test
    void generate_CheckFiles_OnLongDefault() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("Long", "java.lang.Long")));
        mappingConfig.setModelNameSuffix("DTO");
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/defaults-with-Long.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();
        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("InputWithDefaultsDTO.java", "MyEnumDTO.java", "SomeObjectDTO.java"), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(new File(String.format("src/test/resources/expected-classes/defaults/%s.txt",
                    file.getName())),
                    file);
        }
    }
}
