package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

class GraphQLCodegenTypesAsInterfacesTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/github/graphql");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void init() {
        mappingConfig.setPackageName("com.github.graphql");
        mappingConfig.setFieldsWithResolvers(singleton("@customResolver"));
        mappingConfig.setFieldsWithoutResolvers(singleton("@noResolver"));
        mappingConfig.setTypesAsInterfaces(new HashSet<>(singleton("@asInterface")));
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_typesAsInterfaces() throws Exception {
        mappingConfig.getTypesAsInterfaces().add("Order");

        generate("src/test/resources/schemas/types-as-interfaces.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/types-as-interfaces/" +
                "Order.java.txt"), getFileByName(files, "Order.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/types-as-interfaces/" +
                "QueryResolver.java.txt"), getFileByName(files, "QueryResolver.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/types-as-interfaces/" +
                "User.java.txt"), getFileByName(files, "User.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/types-as-interfaces/" +
                "UserCurrentQueryResolver.java.txt"), getFileByName(files, "UserCurrentQueryResolver.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/types-as-interfaces/" +
                "UserResolver.java.txt"), getFileByName(files, "UserResolver.java"));
    }

    @Test
    void generate_typesAsInterfacesExtendsInterface() throws Exception {
        mappingConfig.setTypesAsInterfaces(new HashSet<>(singletonList("@asInterface")));

        generate("src/test/resources/schemas/types-as-interfaces-extends-interface.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/" +
                "types-as-interfaces-extends-interface/Node.java.txt"), getFileByName(files, "Node.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/" +
                        "types-as-interfaces-extends-interface/Profile.java.txt"),
                getFileByName(files, "Profile.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/" +
                        "types-as-interfaces-extends-interface/QueryResolver.java.txt"),
                getFileByName(files, "QueryResolver.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/" +
                "types-as-interfaces-extends-interface/User.java.txt"), getFileByName(files, "User.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/" +
                        "types-as-interfaces-extends-interface/UserCurrentQueryResolver.java.txt"),
                getFileByName(files, "UserCurrentQueryResolver.java"));
    }

    @Test
    void generate_typeAsInterfaceParametrized() throws Exception {
        mappingConfig.setGenerateParameterizedFieldsResolvers(true);

        generate("src/test/resources/schemas/types-as-interfaces-parametrized.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File(
                        "src/test/resources/expected-classes/types-as-interfaces-parametrized/Foo.java.txt"),
                getFileByName(files, "Foo.java"));
    }

    private void generate(String o) throws IOException {
        new JavaGraphQLCodegen(singletonList(o), outputBuildDir, mappingConfig,
                TestUtils.getStaticGeneratedInfo(mappingConfig))
                .generate();
    }

}
