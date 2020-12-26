package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphQLCodegenRestrictedWordsAndParameterInputTest {

    private final MappingConfig mappingConfig = new MappingConfig();

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/codegen/prot");

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate() throws Exception {
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.KOTLIN);
        mappingConfig.setPackageName("com.kobylynskyi.graphql.codegen.prot");
        mappingConfig.setGenerateClient(true);
        mappingConfig.setGenerateBuilder(true);
        mappingConfig.setGenerateEqualsAndHashCode(true);
        mappingConfig.setGenerateToString(true);
        mappingConfig.setGenerateModelsForRootTypes(true);
        mappingConfig.setApiNameSuffix("API");

        new KotlinGraphQLCodegen(singletonList("src/test/resources/schemas/kt/restricted-words.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<?> filters = Arrays.asList("Char.kt", "CharResponseProjection.kt", "FunQueryRequest.kt", "FunQueryResponse.kt", "QueryFunParametrizedInput.kt",
                "Super.kt", "TestEnum.kt", "WhenQueryAPI.kt");
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).filter(f -> filters.contains(f)).sorted().collect(toList());
        assertEquals(Arrays.asList("Char.kt", "CharResponseProjection.kt", "FunQueryRequest.kt", "FunQueryResponse.kt", "QueryFunParametrizedInput.kt",
                "Super.kt", "TestEnum.kt", "WhenQueryAPI.kt"), generatedFileNames);

        for (File file : files) {
            if (filters.contains(file.getName())) {
                assertSameTrimmedContent(
                        new File(String.format("src/test/resources/expected-classes/kt/restricted-words/%s.txt", file.getName())),
                        file);
            }
        }
    }

}
