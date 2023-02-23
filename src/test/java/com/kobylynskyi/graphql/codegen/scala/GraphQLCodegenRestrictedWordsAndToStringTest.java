package com.kobylynskyi.graphql.codegen.scala;

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
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphQLCodegenRestrictedWordsAndToStringTest {

    private final MappingConfig mappingConfig = new MappingConfig();

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/codegen/prot");

    @BeforeEach
    public void setup() {
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.SCALA);
        mappingConfig.setPackageName("com.kobylynskyi.graphql.codegen.prot");
        mappingConfig.setGenerateEqualsAndHashCode(true);
        mappingConfig.setGenerateModelsForRootTypes(true);
        mappingConfig.setApiNameSuffix("API");
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_SetGenerateBuilder_False() throws Exception {
        // fix an issue when generateBuilder=false => can not use object.OPERATION_NAME
        mappingConfig.setGenerateBuilder(false);
        mappingConfig.setGenerateClient(true);

        generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName)
                .filter(f -> f.equals("CaseQueryRequest.scala")).sorted()
                .collect(toList());
        assertEquals(singletonList("CaseQueryRequest.scala"), generatedFileNames);

        for (File file : files) {
            if (file.getName().equals("CaseQueryRequest.scala")) {
                assertSameTrimmedContent(
                        new File("src/test/resources/expected-classes/scala/builder/CaseQueryRequest.scala.txt"), file);
            }
        }
    }

    @Test
    void generate_SetGenerateClient_True() throws Exception {
        mappingConfig.setGenerateClient(true);
        mappingConfig.setGenerateBuilder(true);
        mappingConfig.setUseObjectMapperForRequestSerialization(singleton("TestEnum"));
        mappingConfig.putCustomTypeMappingIfAbsent("DateTime", "java.time.ZonedDateTime");
        mappingConfig.setGenerateApis(true);
        mappingConfig.setUseObjectMapperForRequestSerialization(singleton("DateTime"));

        generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName)
                .filter(f -> Arrays.asList("QueryPrivateParametrizedInput.scala", "Synchronized.scala").contains(f))
                .sorted()
                .collect(toList());
        assertEquals(Arrays.asList("QueryPrivateParametrizedInput.scala", "Synchronized.scala"), generatedFileNames);

        for (File file : files) {
            if (Arrays.asList("QueryPrivateParametrizedInput.scala", "Synchronized.scala").contains(file.getName())) {
                assertSameTrimmedContent(
                        new File(String.format("src/test/resources/expected-classes/scala/tostring/%s.txt",
                                file.getName())),
                        file);
            }
        }
    }

    @Test
    void generate_SetGenerateClient_False() throws Exception {
        mappingConfig.setGenerateClient(false);
        mappingConfig.setGenerateToString(true);
        mappingConfig.setUseObjectMapperForRequestSerialization(singleton("TestEnum"));
        mappingConfig.putCustomTypeMappingIfAbsent("DateTime", "java.time.ZonedDateTime");
        mappingConfig.setGenerateApis(true);

        generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName)
                .filter(f -> Objects.equals("Synchronized.scala", f)
                        || Objects.equals("QueryCaseParametrizedInput.scala", f))
                .sorted().collect(toList());
        assertEquals(singletonList("Synchronized.scala"), generatedFileNames);

        for (File file : files) {
            if (Arrays.asList("QueryCaseParametrizedInput.scala", "Synchronized.scala").contains(file.getName())) {
                assertSameTrimmedContent(
                        new File(String.format("src/test/resources/expected-classes/scala/tostring/%s.txt",
                                "TOSTRING_Synchronized.scala",
                                "QueryCaseParametrizedInput")),
                        file);
            }
        }
    }

    private void generate() throws IOException {
        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/scala/restricted-words.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo(mappingConfig)).generate();
    }

}
