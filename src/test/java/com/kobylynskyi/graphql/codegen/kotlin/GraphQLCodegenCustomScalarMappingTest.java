package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Collections.*;

class GraphQLCodegenCustomScalarMappingTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/test1");

    private MappingConfig mappingConfig;

    @BeforeEach
    void init() {
        mappingConfig = new MappingConfig();
        mappingConfig.setPackageName("com.kobylynskyi.graphql.test1");
        mappingConfig.setGenerateClient(true);
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.KOTLIN);

    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_CustomTypeMapping_WholeScalar() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("ZonedDateTime", "String")));

        new KotlinGraphQLCodegen(singletonList("src/test/resources/schemas/date-scalar.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/kt/custom-type/QueryINeedQueryRequest_whole_scalar.kt.txt"),
                getFileByName(files, "QueryINeedQueryRequest.kt"));
    }

    @Test
    void generate_CustomTypeMapping_ScalarOnQueryOnly() throws Exception {
        HashMap<String, String> customTypesMapping = new HashMap<>();
        customTypesMapping.put("queryINeed.input", "String");
        customTypesMapping.put("ZonedDateTime", "java.time.ZonedDateTime");
        mappingConfig.setCustomTypesMapping(customTypesMapping);

        new KotlinGraphQLCodegen(singletonList("src/test/resources/schemas/date-scalar.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/kt/custom-type/QueryINeedQueryRequest.kt.txt"),
                getFileByName(files, "QueryINeedQueryRequest.kt"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/kt/custom-type/ResponseContainingDate.kt.txt"),
                getFileByName(files, "ResponseContainingDate.kt"));
    }

    @Test
    void generate_UseObjectMapperToSerializeFields_Parameter() throws Exception {
        HashMap<String, String> customTypesMapping = new HashMap<>();
        customTypesMapping.put("queryINeed.input", "ZonedDateTime");
        customTypesMapping.put("ZonedDateTime", "java.time.ZonedDateTime");
        mappingConfig.setCustomTypesMapping(customTypesMapping);
        mappingConfig.setUseObjectMapperForRequestSerialization(singleton("ZonedDateTime"));
        new KotlinGraphQLCodegen(singletonList("src/test/resources/schemas/date-scalar.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/QueryINeedQueryRequest_custom_serializer.kt.txt"),
                getFileByName(files, "QueryINeedQueryRequest.kt"));
    }
}
