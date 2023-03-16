package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.MaxQueryTokensExtension;
import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Collections.singletonList;

@ExtendWith(MaxQueryTokensExtension.class)
class GraphQLCodegenSealedInterfacesTest {
    private final File outputBuildDir = new File("build/generated");
    private final File outputScalaClassesDir = new File("build/generated/com/github/graphql");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void init() {
        mappingConfig.setGenerateParameterizedFieldsResolvers(false);
        mappingConfig.setPackageName("com.github.graphql");
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.KOTLIN);
        mappingConfig.setGenerateToString(true);
        mappingConfig.setGenerateApis(true);
        mappingConfig.setGenerateClient(true);
        mappingConfig.setGenerateEqualsAndHashCode(true);
        mappingConfig.setGenerateBuilder(true);
        mappingConfig.setGenerateSealedInterfaces(true);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_MultipleInterfacesPerType() throws Exception {
        new KotlinGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo(mappingConfig)).generate();
        File[] files = Objects.requireNonNull(outputScalaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/Comment_sealed_interfaces.kt.txt"),
                getFileByName(files, "Comment.kt"));
    }
}
