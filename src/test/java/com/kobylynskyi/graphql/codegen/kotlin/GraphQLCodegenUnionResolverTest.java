package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
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
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.KOTLIN);
    }

    private List<File> generate(String s) throws IOException {
        return new KotlinGraphQLCodegen(singletonList(s), outputBuildDir, mappingConfig,
                TestUtils.getStaticGeneratedInfo()).generate();
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
        List<String> expectedClasses = Arrays.asList("GraphqlJacksonTypeIdResolver.kt", "UnionMemberA.kt",
                "UnionMemberB.kt", "UnionToResolve.kt");
        assertEquals(expectedClasses, generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format(
                            "src/test/resources/expected-classes/kt/jackson-resolver-union/%s.txt",
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
        List<String> expectedClasses = Arrays.asList("GraphqlJacksonTypeIdResolver.kt", "MyUnionMemberASuffix.kt",
                "MyUnionMemberBSuffix.kt", "MyUnionToResolveSuffix.kt");
        assertEquals(expectedClasses, generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format(
                            "src/test/resources/expected-classes/kt/jackson-resolver-union/without-model-package/%s.txt",
                            file.getName())),
                    file);
        }
    }
}
