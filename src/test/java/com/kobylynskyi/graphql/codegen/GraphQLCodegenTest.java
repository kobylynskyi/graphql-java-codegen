package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphQLCodegenTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/test1");

    private MappingConfig mappingConfig;
    private GraphQLCodegen generator;

    @BeforeEach
    void init() {
        mappingConfig = new MappingConfig();
        mappingConfig.setPackageName("com.kobylynskyi.graphql.test1");
        mappingConfig.setGenerateParameterizedFieldsResolvers(false);
        generator = new GraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo());
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_CheckFileReferences() throws Exception {
        List<File> generatedFiles = generator.generate();
        List<File> filesInTheFolder = Arrays.asList(Objects.requireNonNull(outputJavaClassesDir.listFiles()));
        assertEquals(generatedFiles.size(), filesInTheFolder.size());
        assertTrue(generatedFiles.containsAll(filesInTheFolder));
        assertTrue(filesInTheFolder.containsAll(generatedFiles));
    }

    @Test
    void generate_CheckFiles() throws Exception {
        generator.generate();

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
        new GraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Event_noBuilder.java.txt"),
                getFileByName(files, "Event.java"));
    }

    @Test
    void generate_CustomMappings() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "java.util.Date")));

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertFileContainsElements(files, "Event.java", "java.util.Date createdDateTime;");
    }

    @Test
    void generate_CustomMappings_Nested() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("EventProperty.intVal", "java.math.BigInteger")));

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        // As per mapping, only EventProperty.intVal should be mapped to java.math.BigInteger
        assertFileContainsElements(files, "EventProperty.java", "private java.math.BigInteger intVal;");
        assertFileContainsElements(files, "Event.java", "private Integer rating;");
    }

    @Test
    void generate_NoCustomMappings() throws Exception {
        mappingConfig.setModelNameSuffix(" ");

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java", "String createdDateTime;");
    }

    @Test
    void generate_NullCustomMappings() throws Exception {
        mappingConfig.setCustomTypesMapping(null);

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java", "String createdDateTime;");
    }

    @Test
    void generate_NoPackage() throws Exception {
        mappingConfig.setPackageName(null);
        generator.generate();

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
        generator.generate();

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

        generator.generate();

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

        generator.generate();

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
    void generate_NoSchemas() throws Exception {
        new GraphQLCodegen(emptyList(),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputBuildDir.listFiles());
        assertEquals(0, files.length);
    }

    @Test
    void generate_WrongSchema() {
        generator = new GraphQLCodegen(singletonList("src/test/resources/schemas/wrong.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo());

        Assertions.assertThrows(NoSuchFileException.class, () -> generator.generate());
    }

    @Test
    void generate_NoQueries() throws Exception {
        mappingConfig.setPackageName("");
        generator = new GraphQLCodegen(singletonList("src/test/resources/schemas/no-queries.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo());
        generator.generate();

        File[] files = Objects.requireNonNull(outputBuildDir.listFiles());
        assertEquals(2, files.length);
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/EmptyMutationResolver.java.txt"),
                getFileByName(files, "MutationResolver.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/EmptyQueryResolver.java.txt"),
                getFileByName(files, "QueryResolver.java"));
    }

    @Test
    void generate_Empty() throws Exception {
        generator = new GraphQLCodegen(singletonList("src/test/resources/schemas/empty.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo());
        generator.generate();

        File[] files = Objects.requireNonNull(outputBuildDir.listFiles());
        assertEquals(0, files.length);
    }

    @Test
    void generate_OnlyModel() throws Exception {
        mappingConfig.setGenerateApis(false);
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("Event.java", "EventProperty.java", "EventStatus.java"), generatedFileNames);
    }

    @Test
    void generate_WithoutAsyncApis() throws Exception {
        mappingConfig.setGenerateAsyncApi(false);
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "VersionQueryResolver.java", "String version()");
    }

    @Test
    void generate_AsyncQueryApis() throws Exception {
        mappingConfig.setGenerateAsyncApi(true);
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertFileContainsElements(files, "VersionQueryResolver.java",
                "java.util.concurrent.CompletableFuture<String> version()");
        assertFileContainsElements(files, "EventsByCategoryAndStatusQueryResolver.java",
                "java.util.concurrent.CompletableFuture<java.util.List<Event>> eventsByCategoryAndStatus(");
        assertFileContainsElements(files, "EventByIdQueryResolver.java",
                "java.util.concurrent.CompletableFuture<Event> eventById(");
    }

    @Test
    void generate_AsyncQueryApis_CustomWithApiAsyncReturnTypeApiAsyncReturnListType() throws Exception {
        mappingConfig.setGenerateAsyncApi(true);
        mappingConfig.setApiAsyncReturnType("reactor.core.publisher.Mono");
        mappingConfig.setApiAsyncReturnListType("reactor.core.publisher.Flux");
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertFileContainsElements(files, "VersionQueryResolver.java",
                "reactor.core.publisher.Mono<String> version()");
        assertFileContainsElements(files, "EventsByCategoryAndStatusQueryResolver.java",
                "reactor.core.publisher.Flux<Event> eventsByCategoryAndStatus(");
        assertFileContainsElements(files, "EventByIdQueryResolver.java",
                "reactor.core.publisher.Mono<Event> eventById(");
    }

    @Test
    void generate_AsyncQueryApis_CustomWithApiAsyncReturnType() throws Exception {
        mappingConfig.setGenerateAsyncApi(true);
        mappingConfig.setApiAsyncReturnType("reactor.core.publisher.Mono");
        generator.generate();

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

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertFileContainsElements(files, "EventsCreatedSubscriptionResolver.java",
                "org.reactivestreams.Publisher<java.util.List<Event>> eventsCreated() throws Exception;");
    }

    @Test
    void generate_AsyncMutationApis() throws Exception {
        mappingConfig.setGenerateAsyncApi(true);
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertFileContainsElements(files, "CreateEventMutationResolver.java",
                "java.util.concurrent.CompletableFuture<Event> createEvent(");
    }

    @Test
    void generate_deprecated() throws Exception {
        new GraphQLCodegen(singletonList("src/test/resources/schemas/deprecated.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

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
        new GraphQLCodegen(singletonList("src/test/resources/schemas/queries-same-name.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/ProductsByCategoryIdAndStatusQueryResolver.java.txt"),
                getFileByName(files, "ProductsByCategoryIdAndStatusQueryResolver.java"));

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/ProductsByIdsQueryResolver.java.txt"),
                getFileByName(files, "ProductsByIdsQueryResolver.java"));
    }

    @Test
    void generate_InterfaceAndTypeHavingDuplicateFields() throws Exception {
        new GraphQLCodegen(singletonList("src/test/resources/schemas/type-interface-duplicate-fields.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Person.java.txt"),
                getFileByName(files, "Person.java"));
    }

    @Test
    void generate_InterfaceAndTypeHavingDuplicateFields1() throws Exception {
        new GraphQLCodegen(singletonList("src/test/resources/schemas/type-interface-duplicate-fields1.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Person1.java.txt"),
                getFileByName(files, "Person.java"));
    }

}
