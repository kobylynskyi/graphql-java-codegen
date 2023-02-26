package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

class GraphQLCodegenCustomScalarMappingTest {

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
    void generate_CustomTypeMapping_WholeScalar() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("ZonedDateTime", "String")));

        generate("src/test/resources/schemas/date-scalar.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/custom-type/QueryINeedQueryRequest.java.txt"),
                getFileByName(files, "QueryINeedQueryRequest.java"));
    }

    @Test
    void generate_CustomTypeMapping_ScalarOnQueryOnly() throws Exception {
        HashMap<String, String> customTypesMapping = new HashMap<>();
        customTypesMapping.put("queryINeed.input", "String");
        customTypesMapping.put("ZonedDateTime", "java.time.ZonedDateTime");
        mappingConfig.setCustomTypesMapping(customTypesMapping);

        generate("src/test/resources/schemas/date-scalar.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/custom-type/QueryINeedQueryRequest.java.txt"),
                getFileByName(files, "QueryINeedQueryRequest.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/custom-type/ResponseContainingDate.java.txt"),
                getFileByName(files, "ResponseContainingDate.java"));
    }

    /**
     * See #964
     */
    @Test
    void generate_CustomTypeMapping_ForExtensionProperty() throws Exception {
        mappingConfig.setGenerateExtensionFieldsResolvers(true);
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("External", "com.example.External")));
        mappingConfig.setGenerateClient(false);

        generate("src/test/resources/schemas/external-type-extend.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/custom-type/ExternalResolver.java.txt"),
                getFileByName(files, "ExternalResolver.java"));
    }

    private void generate(String path) throws IOException {
        new JavaGraphQLCodegen(singletonList(path),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo(mappingConfig))
                .generate();
    }

}
