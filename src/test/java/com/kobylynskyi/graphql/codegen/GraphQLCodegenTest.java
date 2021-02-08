package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.GeneratedInformation;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertFileContainsElements;
import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphQLCodegenTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/test1");

    private MappingConfig mappingConfig;

    @BeforeEach
    void init() {
        mappingConfig = new MappingConfig();
        mappingConfig.setPackageName("com.kobylynskyi.graphql.test1");
        mappingConfig.setGenerateParameterizedFieldsResolvers(false);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_CheckFileReferences() throws Exception {
        List<File> generatedFiles = generate("src/test/resources/schemas/test.graphqls");

        List<File> filesInTheFolder = Arrays.asList(Objects.requireNonNull(outputJavaClassesDir.listFiles()));
        assertEquals(generatedFiles.size(), filesInTheFolder.size());
        assertTrue(generatedFiles.containsAll(filesInTheFolder));
        assertTrue(filesInTheFolder.containsAll(generatedFiles));
    }

    @Test
    void generate_CheckFiles() throws Exception {
        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList(
                "CreateEventMutationResolver.java", "Event.java", "EventByIdQueryResolver.java", "EventProperty.java",
                "EventStatus.java", "EventsByCategoryAndStatusQueryResolver.java", "EventsByIdsQueryResolver.java",
                "EventsCreatedSubscriptionResolver.java", "MutationResolver.java", "QueryResolver.java", "SubscriptionResolver.java",
                "VersionQueryResolver.java"), generatedFileNames);

        for (File file : files) {
            File expected = new File(String.format("src/test/resources/expected-classes/%s.txt", file.getName()));
            assertSameTrimmedContent(expected, file);
        }
    }

    @Test
    void generate_NoBuilder() throws Exception {
        mappingConfig.setGenerateBuilder(false);

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Event_noBuilder.java.txt"),
                getFileByName(files, "Event.java"));
    }

    @Test
    void generate_CustomMappings() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "java.util.Date")));

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java", "java.util.Date createdDateTime;");
    }

    @Test
    void generate_CustomMappings_Nested() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("EventProperty.intVal", "java.math.BigInteger")));

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        // As per mapping, only EventProperty.intVal should be mapped to java.math.BigInteger
        assertFileContainsElements(files, "EventProperty.java", "private java.math.BigInteger intVal;");
        assertFileContainsElements(files, "Event.java", "private Integer rating;");
    }

    @Test
    void generate_NoCustomMappings() throws Exception {
        mappingConfig.setModelNameSuffix(" ");

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java", "String createdDateTime;");
    }

    @Test
    void generate_NullCustomMappings() throws Exception {
        mappingConfig.setCustomTypesMapping(null);

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java", "String createdDateTime;");
    }

    @Test
    void generate_NoPackage() throws Exception {
        mappingConfig.setPackageName(null);

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputBuildDir.listFiles());
        assertFileContainsElements(files, "Event.java", System.lineSeparator() +
                "/**" + System.lineSeparator() +
                " * An event that describes a thing that happens" + System.lineSeparator() +
                " */" + System.lineSeparator());
    }

    @Test
    void generate_CustomModelAndApiPackages() throws Exception {
        mappingConfig.setModelPackageName("com.kobylynskyi.graphql.test1.model");
        mappingConfig.setApiPackageName("com.kobylynskyi.graphql.test1.api");

        generate("src/test/resources/schemas/test.graphqls");

        File[] apiFiles = Objects.requireNonNull(new File(outputJavaClassesDir, "api").listFiles());
        List<String> generatedApiFileNames = Arrays.stream(apiFiles).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("CreateEventMutationResolver.java", "EventByIdQueryResolver.java",
                "EventsByCategoryAndStatusQueryResolver.java", "EventsByIdsQueryResolver.java", "EventsCreatedSubscriptionResolver.java", "MutationResolver.java", "QueryResolver.java",
                "SubscriptionResolver.java", "VersionQueryResolver.java"), generatedApiFileNames);

        for (File apiFile : apiFiles) {
            assertThat(Utils.getFileContent(apiFile.getPath()),
                    StringStartsWith.startsWith("package com.kobylynskyi.graphql.test1.api;"));
        }

        File[] modelFiles = Objects.requireNonNull(new File(outputJavaClassesDir, "model").listFiles());
        List<String> generatedModelFileNames = Arrays.stream(modelFiles).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("Event.java", "EventProperty.java", "EventStatus.java"), generatedModelFileNames);

        for (File modelFile : modelFiles) {
            assertThat(Utils.getFileContent(modelFile.getPath()),
                    StringStartsWith.startsWith("package com.kobylynskyi.graphql.test1.model;"));
        }
    }

    @Test
    void generate_EqualsAndHashCode() throws Exception {
        mappingConfig.setGenerateEqualsAndHashCode(true);
        mappingConfig.setModelNameSuffix("TO");

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertNotEquals(0, files.length);

        for (File eventFile : files) {
            if (eventFile.getName().endsWith("TO.java")) {
                String content = Utils.getFileContent(eventFile.getPath());

                if (content.contains("public interface ") || content.contains("public enum ")) {
                    continue;
                }

                assertThat(content, StringContains.containsString("public boolean equals(Object obj)"));

                assertThat(content, StringContains.containsString("public int hashCode()"));
            }
        }

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/EventPropertyTO_withEqualsAndHashCode.java.txt"),
                getFileByName(files, "EventPropertyTO.java"));
    }

    @Test
    void generate_toString() throws Exception {
        mappingConfig.setGenerateToString(true);
        mappingConfig.setModelNameSuffix("TO");

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertNotEquals(0, files.length);

        for (File eventFile : files) {
            if (eventFile.getName().endsWith("TO.java")) {
                String content = Utils.getFileContent(eventFile.getPath());

                if (content.contains("public interface ") || content.contains("public enum ")) {
                    continue;
                }

                assertThat(content, StringContains.containsString("public String toString()"));
            }
        }
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/EventPropertyTO_toString.java.txt"),
                getFileByName(files, "EventPropertyTO.java"));
    }

    @Test
    void generate_GeneratedAnnotation() throws Exception {
        mappingConfig.setAddGeneratedAnnotation(false);
        mappingConfig.setModelNameSuffix("TO");

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertNotEquals(0, files.length);

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/EventPropertyTO_withoutGeneratedAnnotation.java.txt"),
                getFileByName(files, "EventPropertyTO.java"));
    }

    @Test
    void generate_NoSchemas() {
        GeneratedInformation staticGeneratedInfo = TestUtils.getStaticGeneratedInfo();
        List<String> schemas = emptyList();
        assertThrows(IllegalArgumentException.class, () ->
                new JavaGraphQLCodegen(schemas, outputBuildDir, mappingConfig, staticGeneratedInfo));
    }

    @Test
    void generate_WrongSchema() {
        GraphQLCodegen graphQLCodegen = new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/wrong.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo());

        assertThrows(NoSuchFileException.class, graphQLCodegen::generate);

        assertEquals(0, Objects.requireNonNull(outputBuildDir.listFiles()).length);
    }

    @Test
    void generate_NoQueries() throws Exception {
        mappingConfig.setPackageName("");

        generate("src/test/resources/schemas/no-queries.graphqls");

        File[] files = Objects.requireNonNull(outputBuildDir.listFiles());
        assertEquals(2, files.length);
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/EmptyMutationResolver.java.txt"),
                getFileByName(files, "MutationResolver.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/EmptyQueryResolver.java.txt"),
                getFileByName(files, "QueryResolver.java"));
    }

    @Test
    void generate_Empty() throws Exception {
        generate("src/test/resources/schemas/empty.graphqls");

        File[] files = Objects.requireNonNull(outputBuildDir.listFiles());
        assertEquals(0, files.length);
    }

    @Test
    void generate_OnlyModel() throws Exception {
        mappingConfig.setGenerateApis(false);

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("Event.java", "EventProperty.java", "EventStatus.java"), generatedFileNames);
    }

    @Test
    void generate_QueryApis_CustomWithApiReturnTypeApiReturnListType() throws Exception {
        mappingConfig.setApiReturnType("reactor.core.publisher.Mono");
        mappingConfig.setApiReturnListType("reactor.core.publisher.Flux");

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertFileContainsElements(files, "VersionQueryResolver.java",
                "reactor.core.publisher.Mono<String> version()");
        assertFileContainsElements(files, "EventsByCategoryAndStatusQueryResolver.java",
                "reactor.core.publisher.Flux<Event> eventsByCategoryAndStatus(");
        assertFileContainsElements(files, "EventByIdQueryResolver.java",
                "reactor.core.publisher.Mono<Event> eventById(");
    }

    @Test
    void generate_QueryApis_CustomWithApiReturnType() throws Exception {
        mappingConfig.setApiReturnType("reactor.core.publisher.Mono");

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertFileContainsElements(files, "VersionQueryResolver.java",
                "reactor.core.publisher.Mono<String> version()");
        assertFileContainsElements(files, "EventsByCategoryAndStatusQueryResolver.java",
                "reactor.core.publisher.Mono<java.util.List<Event>> eventsByCategoryAndStatus(");
        assertFileContainsElements(files, "EventByIdQueryResolver.java",
                "reactor.core.publisher.Mono<Event> eventById(");
    }

    @Test
    void generate_CustomSubscriptionReturnType() throws Exception {
        mappingConfig.setSubscriptionReturnType("org.reactivestreams.Publisher");

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "EventsCreatedSubscriptionResolver.java",
                "org.reactivestreams.Publisher<java.util.List<Event>> eventsCreated() throws Exception;");
    }

    @Test
    void generate_deprecated() throws Exception {
        generate("src/test/resources/schemas/deprecated.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("CreateEventMutationResolver.java", "Event.java", "EventInput.java", "EventsQueryResolver.java",
                "MutationResolver.java", "Node.java", "PinnableItem.java", "QueryResolver.java", "Status.java"), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format("src/test/resources/expected-classes/deprecated/%s.txt", file.getName())),
                    file);
        }
    }

    @Test
    void generate_QueriesWithSameName() throws Exception {
        generate("src/test/resources/schemas/queries-same-name.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/ProductsByCategoryIdAndStatusQueryResolver.java.txt"),
                getFileByName(files, "ProductsByCategoryIdAndStatusQueryResolver.java"));

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/ProductsByIdsQueryResolver.java.txt"),
                getFileByName(files, "ProductsByIdsQueryResolver.java"));
    }

    @Test
    void generate_InterfaceAndTypeHavingDuplicateFields() throws Exception {
        generate("src/test/resources/schemas/type-interface-duplicate-fields.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Person.java.txt"),
                getFileByName(files, "Person.java"));
    }

    @Test
    void generate_InterfaceAndTypeHavingDuplicateFields1() throws Exception {
        generate("src/test/resources/schemas/type-interface-duplicate-fields1.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Person1.java.txt"),
                getFileByName(files, "Person.java"));
    }

    private List<File> generate(String s) throws IOException {
        return new JavaGraphQLCodegen(singletonList(s), outputBuildDir, mappingConfig,
                TestUtils.getStaticGeneratedInfo()).generate();
    }

}
