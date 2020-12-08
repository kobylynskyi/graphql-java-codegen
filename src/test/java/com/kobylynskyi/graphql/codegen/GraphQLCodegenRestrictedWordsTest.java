package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
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

class GraphQLCodegenRestrictedWordsTest {

    private final MappingConfig mappingConfig = new MappingConfig();

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/codegen/prot");

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate() throws Exception {
        mappingConfig.setPackageName("com.kobylynskyi.graphql.codegen.prot");
        mappingConfig.setGenerateClient(true);
        mappingConfig.setGenerateBuilder(true);
        mappingConfig.setGenerateEqualsAndHashCode(true);
        mappingConfig.setGenerateToString(true);
        mappingConfig.setGenerateModelsForRootTypes(true);
        mappingConfig.setApiNameSuffix("API");

        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/restricted-words.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("CaseQueryAPI.java", "CaseQueryRequest.java", "CaseQueryResponse.java",
                "Char.java", "CharResponseProjection.java", "NativeQueryAPI.java", "NativeQueryRequest.java",
                "NativeQueryResponse.java", "PrivateQueryAPI.java", "PrivateQueryRequest.java",
                "PrivateQueryResponse.java", "Query.java", "QueryAPI.java", "QueryCaseParametrizedInput.java",
                "QueryPrivateParametrizedInput.java", "QueryResolver.java", "QueryResponseProjection.java",
                "Synchronized.java", "SynchronizedResponseProjection.java", "TestEnum.java"), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format("src/test/resources/expected-classes/restricted-words/%s.txt", file.getName())),
                    file);
        }
    }

}
