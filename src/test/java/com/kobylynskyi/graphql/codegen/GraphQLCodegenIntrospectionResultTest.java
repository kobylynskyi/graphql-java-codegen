package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.ApiNamePrefixStrategy;
import com.kobylynskyi.graphql.codegen.model.ApiRootInterfaceStrategy;
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
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphQLCodegenIntrospectionResultTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/test1");

    private MappingConfig mappingConfig;

    @BeforeEach
    void init() {
        mappingConfig = new MappingConfig();
        mappingConfig.setPackageName("com.kobylynskyi.graphql.test1");
        mappingConfig.setGenerateClient(true);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generateClientFromIntrospectionResult() throws Exception {
        new JavaGraphQLCodegen("src/test/resources/introspection-result/sample-introspection-query-result.json",
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        checkGeneratedFiles();
    }

    @Test
    void generateClientFromIntrospectionResultWrappedInData() throws Exception {
        new JavaGraphQLCodegen("src/test/resources/introspection-result/sample-introspection-query-result-wrapped.json",
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        checkGeneratedFiles();
    }

    @Test
    void generateClientFromIntrospectionResult_SetApiStrategies() throws Exception {
        mappingConfig.setApiRootInterfaceStrategy(ApiRootInterfaceStrategy.INTERFACE_PER_SCHEMA);
        mappingConfig.setApiNamePrefixStrategy(ApiNamePrefixStrategy.FOLDER_NAME_AS_PREFIX);

        new JavaGraphQLCodegen("src/test/resources/introspection-result/sample-introspection-query-result.json",
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        checkGeneratedFiles();
    }

    private void checkGeneratedFiles() throws IOException {
        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("CreateMutationRequest.java", "CreateMutationResolver.java",
                "CreateMutationResponse.java", "MutationResolver.java", "Product.java", "ProductByIdQueryRequest.java",
                "ProductByIdQueryResolver.java", "ProductByIdQueryResponse.java", "ProductInput.java",
                "ProductResponseProjection.java", "ProductsByIdsQueryRequest.java", "ProductsByIdsQueryResolver.java",
                "ProductsByIdsQueryResponse.java", "ProductsQueryRequest.java", "ProductsQueryResolver.java",
                "ProductsQueryResponse.java", "QueryResolver.java", "StockStatus.java"), generatedFileNames);

        for (File file : files) {
            File expected = new File(String.format("src/test/resources/expected-classes/from-introspection-result/%s.txt", file.getName()));
            assertSameTrimmedContent(expected, file);
        }
    }

}
