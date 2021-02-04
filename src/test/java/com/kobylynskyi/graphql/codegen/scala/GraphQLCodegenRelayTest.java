package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.supplier.SchemaFinder;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphQLCodegenRelayTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated");
    private final MappingConfig mappingConfig = new MappingConfig();
    private final SchemaFinder schemaFinder = new SchemaFinder(Paths.get("src/test/resources"));

    @BeforeEach
    void init() {
        mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(true);
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.SCALA);
        schemaFinder.setIncludePattern("relay.graphqls");
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generateServerSideRelayClasses() throws Exception {
        new ScalaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        Set<String> generatedFileNames = Arrays.stream(files).map(File::getName).collect(toSet());
        assertEquals(new HashSet<>(asList("Organization.scala", "User.scala", "OrganizationsQueryResolver.scala",
                "QueryResolver.scala", "UsersQueryResolver.scala")), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format("src/test/resources/expected-classes/scala/relay/%s.txt", file.getName())),
                    file);
        }
    }

    @Test
    void generateServerSideRelayClasses_CustomGenericsConnectionType() throws Exception {
        mappingConfig.getRelayConfig()
                .setConnectionType("reactor.core.publisher.Mono[graphql.relay.Connection[%s]]");
        new ScalaGraphQLCodegen(schemaFinder.findSchemas(), outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/scala/relay/UsersQueryResolver_reactive.scala.txt"),
                getFileByName(files, "UsersQueryResolver.scala"));

    }

}