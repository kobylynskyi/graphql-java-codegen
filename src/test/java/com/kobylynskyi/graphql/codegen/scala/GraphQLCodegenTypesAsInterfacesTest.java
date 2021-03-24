package com.kobylynskyi.graphql.codegen.scala;

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
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.SCALA);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_typeAsInterface() throws Exception {
        mappingConfig.setTypesAsInterfaces(new HashSet<>(asList("@customInterface", "Order")));

        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/types-as-interfaces.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/types-as-interfaces/" +
                "Order.scala.txt"), getFileByName(files, "Order.scala"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/types-as-interfaces/" +
                "QueryResolver.scala.txt"), getFileByName(files, "QueryResolver.scala"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/types-as-interfaces/" +
                "User.scala.txt"), getFileByName(files, "User.scala"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/types-as-interfaces/" +
                "UserCurrentQueryResolver.scala.txt"), getFileByName(files, "UserCurrentQueryResolver.scala"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/types-as-interfaces/" +
                "UserResolver.scala.txt"), getFileByName(files, "UserResolver.scala"));
    }

    @Test
    void generate_typeAsInterfaceExtendsInterface() throws Exception {
        mappingConfig.setTypesAsInterfaces(new HashSet<>(asList("@customInterface")));

        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/" +
                "types-as-interfaces-extends-interface.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/" +
                "types-as-interfaces-extends-interface/Node.scala.txt"), getFileByName(files, "Node.scala"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/" +
                "types-as-interfaces-extends-interface/Profile.scala.txt"),
                getFileByName(files, "Profile.scala"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/" +
                "types-as-interfaces-extends-interface/QueryResolver.scala.txt"), 
                getFileByName(files, "QueryResolver.scala"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/" +
                "types-as-interfaces-extends-interface/User.scala.txt"), getFileByName(files, "User.scala"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/" +
                "types-as-interfaces-extends-interface/UserCurrentQueryResolver.scala.txt"), 
                getFileByName(files, "UserCurrentQueryResolver.scala"));
    }

}
