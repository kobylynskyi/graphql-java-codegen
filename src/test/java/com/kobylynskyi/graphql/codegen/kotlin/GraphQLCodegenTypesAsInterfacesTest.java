package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

class GraphQLCodegenTypesAsInterfacesTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/github/graphql");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void init() {
        mappingConfig.setPackageName("com.github.graphql");
        mappingConfig.setFieldsWithResolvers(Collections.singleton("@customResolver"));
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.KOTLIN);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_typesAsInterfaces() throws Exception {
        mappingConfig.setTypesAsInterfaces(new HashSet<>(asList("@customInterface", "Order")));

        new KotlinGraphQLCodegen(singletonList("src/test/resources/schemas/types-as-interfaces.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/types-as-interfaces/" +
                "Order.kt.txt"), getFileByName(files, "Order.kt"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/types-as-interfaces/" +
                "QueryResolver.kt.txt"), getFileByName(files, "QueryResolver.kt"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/types-as-interfaces/" +
                "User.kt.txt"), getFileByName(files, "User.kt"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/types-as-interfaces/" +
                "UserCurrentQueryResolver.kt.txt"), getFileByName(files, "UserCurrentQueryResolver.kt"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/types-as-interfaces/" +
                "UserResolver.kt.txt"), getFileByName(files, "UserResolver.kt"));
    }

    @Test
    void generate_typesAsInterfacesExtendsInterface() throws Exception {
        mappingConfig.setTypesAsInterfaces(new HashSet<>(asList("@customInterface")));

        new KotlinGraphQLCodegen(singletonList("src/test/resources/schemas/" +
                "types-as-interfaces-extends-interface.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/" +
                "types-as-interfaces-extends-interface/Node.kt.txt"), getFileByName(files, "Node.kt"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/" +
                "types-as-interfaces-extends-interface/Profile.kt.txt"), getFileByName(files, "Profile.kt"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/" +
                "types-as-interfaces-extends-interface/QueryResolver.kt.txt"),
                getFileByName(files, "QueryResolver.kt"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/" +
                "types-as-interfaces-extends-interface/User.kt.txt"), getFileByName(files, "User.kt"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/kt/" +
                "types-as-interfaces-extends-interface/UserCurrentQueryResolver.kt.txt"),
                getFileByName(files, "UserCurrentQueryResolver.kt"));
    }

}
