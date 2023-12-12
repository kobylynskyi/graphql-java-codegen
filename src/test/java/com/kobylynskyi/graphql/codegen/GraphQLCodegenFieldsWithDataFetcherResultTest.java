package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphQLCodegenFieldsWithDataFetcherResultTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/github/graphql");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void init() {
        mappingConfig.setPackageName("com.github.graphql");
        mappingConfig.setFieldsWithDataFetcherResult(new HashSet<>(singleton("@dataFetcherResult")));
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_fieldsWithDataFetcherResult() throws Exception {
        mappingConfig.getFieldsWithDataFetcherResult().add("orders");
        mappingConfig.getFieldsWithDataFetcherResult().add("cart");

        generate("src/test/resources/schemas/fields-with-data-fetcher-result.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Assertions.assertNotNull(files);

        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(asList("Cart.java", "Order.java", "QueryResolver.java", "User.java", "UserCurrentQueryResolver.java"), generatedFileNames);

        File user = getFileByName(files, "User.java");
        String userContext = Utils.getFileContent(user.getPath()).trim();

        assertTrue(userContext.contains("java.util.List<graphql.execution.DataFetcherResult<Order>>"));
        assertTrue(userContext.contains("graphql.execution.DataFetcherResult<Cart>"));
    }

    private void generate(String o) throws IOException {
        new JavaGraphQLCodegen(singletonList(o), outputBuildDir, mappingConfig,
                TestUtils.getStaticGeneratedInfo(mappingConfig))
                .generate();
    }
}
