package com.kobylynskyi.graphql.codegen;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphqlCodegenMultiFilesTest {

    private GraphqlCodegen generator;

    private File outputBuildDir = new File("build/generated");
    private File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/multifiles");

    @BeforeEach
    void init() {
        MappingConfig mappingConfig = new MappingConfig();
        mappingConfig.setPackageName("com.kobylynskyi.graphql.multifiles");
        List<String> schemas = Arrays.asList(
                "src/test/resources/schemas/multi1.graphqls",
                "src/test/resources/schemas/multi2.graphqls"
        );
        generator = new GraphqlCodegen(schemas, outputBuildDir, mappingConfig);
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
        assertEquals(Arrays.asList("MyUnion.java", "UnionMember1.java", "UnionMember2.java"), generatedFileNames);

        for (File file : files) {
            File expected = new File(String.format("src/test/resources/expected-classes/%s.txt", file.getName()));
            TestUtils.assertSameTrimmedContent(expected, file);
        }
    }
}
