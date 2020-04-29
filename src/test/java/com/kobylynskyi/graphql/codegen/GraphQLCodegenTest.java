package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.hamcrest.Matchers;
import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.*;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GraphQLCodegenTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/test1");

    private MappingConfig mappingConfig;
    private GraphQLCodegen generator;

    @BeforeEach
    void init() {
        mappingConfig = new MappingConfig();
        mappingConfig.setPackageName("com.kobylynskyi.graphql.test1");
        generator = new GraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig);
    }

    @AfterEach
    void cleanup() throws IOException {
        Utils.deleteDir(new File("build/generated"));
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
                "CreateEventMutation.java", "Event.java", "EventByIdQuery.java", "EventProperty.java",
                "EventStatus.java", "EventsByCategoryAndStatusQuery.java", "EventsByIdsQuery.java",
                "EventsCreatedSubscription.java", "Mutation.java", "Query.java", "Subscription.java",
                "VersionQuery.java"), generatedFileNames);

        for (File file : files) {
            File expected = new File(String.format("src/test/resources/expected-classes/%s.txt", file.getName()));
            assertSameTrimmedContent(expected, file);
        }
    }

    @Test
    void generate_NoBuilder() throws Exception {
        mappingConfig.setGenerateBuilder(false);
        new GraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig).generate();

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
    void generate_CustomAnnotationMappings() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("Event.createdDateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Event.createdDateTime",
                "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)")));

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)"
                        + System.lineSeparator() + "    private org.joda.time.DateTime createdDateTime;");
    }

    @Test
    void generate_CustomAnnotationMappings_Type() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("DateTime",
                "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)")));

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)"
                        + System.lineSeparator() + "    private org.joda.time.DateTime createdDateTime;");
    }

    @Test
    void generate_CustomAnnotationMappings_FieldType() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Event.createdDateTime",
                "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)")));

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)"
                        + System.lineSeparator() + "    private org.joda.time.DateTime createdDateTime;");
    }

    @Test
    void generate_CustomSubscriptionReturnType() throws Exception {
        mappingConfig.setSubscriptionReturnType("org.reactivestreams.Publisher");

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertFileContainsElements(files, "EventsCreatedSubscription.java",
                "org.reactivestreams.Publisher<java.util.Collection<Event>> eventsCreated() throws Exception;");
    }

    @Test
    void generate_NoPackage() throws Exception {
        mappingConfig.setPackageName(null);
        generator.generate();

        File[] files = Objects.requireNonNull(outputBuildDir.listFiles());

        assertFileContainsElements(files, "Event.java", System.lineSeparator() +
                "/**" + System.lineSeparator() +
                " * An event that describes a thing that happens" + System.lineSeparator() +
                " */" + System.lineSeparator() +
                "public class Event {");
    }

    @Test
    void generate_CustomModelAndApiPackages() throws Exception {
        mappingConfig.setModelPackageName("com.kobylynskyi.graphql.test1.model");
        mappingConfig.setApiPackageName("com.kobylynskyi.graphql.test1.api");
        generator.generate();

        File[] apiFiles = Objects.requireNonNull(new File(outputJavaClassesDir, "api").listFiles());
        List<String> generatedApiFileNames = Arrays.stream(apiFiles).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("CreateEventMutation.java", "EventByIdQuery.java",
                "EventsByCategoryAndStatusQuery.java", "EventsByIdsQuery.java", "EventsCreatedSubscription.java", "Mutation.java", "Query.java",
                "Subscription.java", "VersionQuery.java"), generatedApiFileNames);

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
        assertNotEquals(files.length, 0);

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
        assertNotEquals(files.length, 0);

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
        generator.setSchemas(Collections.emptyList());
        generator.generate();

        File[] files = Objects.requireNonNull(outputBuildDir.listFiles());
        assertEquals(0, files.length);
    }

    @Test
    void generate_WrongSchema() {
        generator.setSchemas(singletonList("src/test/resources/schemas/wrong.graphqls"));

        Assertions.assertThrows(NoSuchFileException.class, () -> generator.generate());
    }

    @Test
    void generate_NoQueries() throws Exception {
        mappingConfig.setPackageName("");
        generator.setSchemas(singletonList("src/test/resources/schemas/no-queries.graphqls"));
        generator.generate();

        File[] files = Objects.requireNonNull(outputBuildDir.listFiles());
        assertEquals(2, files.length);
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/EmptyMutation.java.txt"),
                getFileByName(files, "Mutation.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/EmptyQuery.java.txt"),
                getFileByName(files, "Query.java"));
    }

    @Test
    void generate_Empty() throws Exception {
        generator.setSchemas(singletonList("src/test/resources/schemas/empty.graphqls"));
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
        assertFileContainsElements(files, "VersionQuery.java", "String version()");
    }

    @Test
    void generate_AsyncQueryApis() throws Exception {
        mappingConfig.setGenerateAsyncApi(true);
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertFileContainsElements(files, "VersionQuery.java",
                "java.util.concurrent.CompletableFuture<String> version()");

        assertFileContainsElements(files, "EventsByCategoryAndStatusQuery.java",
                "java.util.concurrent.CompletableFuture<java.util.Collection<Event>> eventsByCategoryAndStatus(");

        assertFileContainsElements(files, "EventByIdQuery.java",
                "java.util.concurrent.CompletableFuture<Event> eventById(");
    }

    @Test
    void generate_AsyncMutationApis() throws Exception {
        mappingConfig.setGenerateAsyncApi(true);
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertFileContainsElements(files, "CreateEventMutation.java",
                "java.util.concurrent.CompletableFuture<Event> createEvent(");
    }

    @Test
    void generate_deprecated() throws Exception {
        new GraphQLCodegen(singletonList("src/test/resources/schemas/deprecated.graphqls"),
                outputBuildDir, mappingConfig).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("CreateEventMutation.java", "Event.java", "EventInput.java", "EventsQuery.java",
                "Mutation.java", "Node.java", "PinnableItem.java", "Query.java", "Status.java"), generatedFileNames);

        for (File file : files) {
            assertSameTrimmedContent(
                    new File(String.format("src/test/resources/expected-classes/deprecated/%s.txt", file.getName())),
                    file);
        }
    }

    @Test
    void generate_QueriesWithSameName() throws Exception {
        new GraphQLCodegen(singletonList("src/test/resources/schemas/queries-same-name.graphqls"),
                outputBuildDir, mappingConfig).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/ProductsByCategoryIdAndStatusQuery.java.txt"),
                getFileByName(files, "ProductsByCategoryIdAndStatusQuery.java"));

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/ProductsByIdsQuery.java.txt"),
                getFileByName(files, "ProductsByIdsQuery.java"));
    }

    @Test
    void generate_InterfaceAndTypeHavingDuplicateFields() throws Exception {
        new GraphQLCodegen(singletonList("src/test/resources/schemas/type-interface-duplicate-fields.graphqls"),
                outputBuildDir, mappingConfig).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Person.java.txt"),
                getFileByName(files, "Person.java"));
    }

    @Test
    void generate_InterfaceAndTypeHavingDuplicateFields1() throws Exception {
        new GraphQLCodegen(singletonList("src/test/resources/schemas/type-interface-duplicate-fields1.graphqls"),
                outputBuildDir, mappingConfig).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Person1.java.txt"),
                getFileByName(files, "Person.java"));
    }

    private void assertFileContainsElements(File[] files, String fileName, String... elements)
            throws IOException {
        File file = getFileByName(files, fileName);
        String fileContent = Utils.getFileContent(file.getPath());
        assertThat(fileContent, Matchers.stringContainsInOrder(elements));
    }

}
