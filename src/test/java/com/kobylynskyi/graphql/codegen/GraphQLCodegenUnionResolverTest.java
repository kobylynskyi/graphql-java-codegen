package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphQLCodegenUnionResolverTest {

    private final File outputBuildDir = new File("build/generated");

    private MappingConfig mappingConfig;

    @BeforeEach
    void init() {
        mappingConfig = new MappingConfig();
        mappingConfig.setGenerateJacksonTypeIdResolver(true);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_CheckFiles_with_model_package() throws Exception {
        mappingConfig.setPackageName("com.kobylynskyi.graphql.unionresolver");
        generate("src/test/resources/schemas/union-resolver.graphqls");

        File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/unionresolver");
        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        List<String> expectedClasses = Arrays.asList("GraphqlJacksonTypeIdResolver.java", "ResultObject.java",
                "UnionMemberA.java", "UnionMemberB.java", "UnionToResolve.java");
        assertEquals(expectedClasses, generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format(
                            "src/test/resources/expected-classes/jackson-resolver-union/%s.txt",
                            file.getName())),
                    file);
        }
    }

    @Test
    void generate_CheckFiles_without_model_package_and_with_prefix_and_suffix() throws Exception {
        mappingConfig.setModelNamePrefix("My");
        mappingConfig.setModelNameSuffix("Suffix");
        generate("src/test/resources/schemas/union-resolver.graphqls");

        File outputJavaClassesDir = new File("build/generated");
        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        List<String> expectedClasses = Arrays.asList("GraphqlJacksonTypeIdResolver.java", "MyResultObjectSuffix.java",
                "MyUnionMemberASuffix.java", "MyUnionMemberBSuffix.java", "MyUnionToResolveSuffix.java");
        assertEquals(expectedClasses, generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format(
                            "src/test/resources/expected-classes/jackson-resolver-union/without-model-package/%s.txt",
                            file.getName())),
                    file);
        }
    }

    private void generate(String path) throws IOException {
        new JavaGraphQLCodegen(singletonList(path), outputBuildDir, mappingConfig,
                TestUtils.getStaticGeneratedInfo()).generate();
    }

}
