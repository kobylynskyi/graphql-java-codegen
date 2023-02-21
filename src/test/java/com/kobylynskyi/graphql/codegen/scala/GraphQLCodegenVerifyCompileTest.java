package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static java.util.Collections.singletonList;

class GraphQLCodegenVerifyCompileTest {

    private final String path = "src/test/java/com/kobylynskyi/graphql/codegen/scala/compile-pass";
    private final File outputBuildDir = new File(path);

    private MappingConfig mappingConfig;

    @BeforeEach
    void init() {
        mappingConfig = new MappingConfig();
        mappingConfig.setGenerateParameterizedFieldsResolvers(true);
        mappingConfig.setPackageName("com.github.graphql");
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.SCALA);
        mappingConfig.setGenerateToString(true);
        mappingConfig.setGenerateApis(true);
        mappingConfig.setGenerateClient(true);
        mappingConfig.setAddGeneratedAnnotation(false);
        mappingConfig.setGenerateEqualsAndHashCode(true);
        mappingConfig.setModelValidationAnnotation("");
    }

    @Test
    void generate_CheckFiles_Scala_Files_Compile_Pass() throws Exception {
        mappingConfig.setPackageName("");
        generate("src/test/resources/schemas/test.graphqls");

        File outputJavaClassesDir = new File(path);
        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assert files.length > 0;
    }

    private void generate(String path) throws IOException {
        new ScalaGraphQLCodegen(singletonList(path), outputBuildDir, mappingConfig,
                TestUtils.getStaticGeneratedInfo()).generate();
    }

}
