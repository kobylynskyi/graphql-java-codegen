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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.*;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GraphqlCodegenTest {

    private MappingConfig mappingConfig;
    private GraphqlCodegen generator;

    private File outputBuildDir = new File("build/generated");
    private File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/test1");

    @BeforeEach
    void init() {
        mappingConfig = new MappingConfig();
        mappingConfig.setPackageName("com.kobylynskyi.graphql.test1");
        generator = new GraphqlCodegen(Collections.singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig);
    }

    @AfterEach
    void cleanup() throws IOException {
        Utils.deleteDir(new File("build/generated"));
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
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        File eventFile = Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase("Event.java")).findFirst()
                .orElseThrow(FileNotFoundException::new);
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/Event_noBuilder.java.txt"), eventFile);
    }

    @Test
    void generate_CustomMappings() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "java.util.Date")));

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        File eventFile = Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase("Event.java")).findFirst()
                .orElseThrow(FileNotFoundException::new);

        assertThat(Utils.getFileContent(eventFile.getPath()),
                StringContains.containsString("java.util.Date createdDateTime;"));
    }

    @Test
    void generate_CustomMappings_Nested() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("EventProperty.intVal", "java.math.BigInteger")));

        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        // As per mapping, only EventProperty.intVal should be mapped to java.math.BigInteger
        File eventPropertyFile = Arrays.stream(files)
                .filter(file -> file.getName().equalsIgnoreCase("EventProperty.java")).findFirst()
                .orElseThrow(FileNotFoundException::new);
        assertThat(Utils.getFileContent(eventPropertyFile.getPath()),
                StringContains.containsString("private java.math.BigInteger intVal;"));
        File eventFile = Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase("Event.java")).findFirst()
                .orElseThrow(FileNotFoundException::new);
        assertThat(Utils.getFileContent(eventFile.getPath()), StringContains.containsString("private Integer rating;"));
    }

    @Test
    void generate_NoCustomMappings() throws Exception {
        mappingConfig.setModelNameSuffix(" ");
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        File eventFile = Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase("Event.java")).findFirst()
                .orElseThrow(FileNotFoundException::new);

        assertThat(Utils.getFileContent(eventFile.getPath()), StringContains.containsString("String createdDateTime;"));
    }

    @Test
    void generate_NullCustomMappings() throws Exception {
        mappingConfig.setCustomTypesMapping(null);
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        File eventFile = Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase("Event.java")).findFirst()
                .orElseThrow(FileNotFoundException::new);

        assertThat(Utils.getFileContent(eventFile.getPath()), StringContains.containsString("String createdDateTime;"));
    }

    @Test
    void generate_CustomAnnotationMappings() throws Exception {
        mappingConfig.setCustomTypesMapping(
                new HashMap<>(singletonMap("Event.createdDateTime", "org.joda.time.DateTime")));

        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Event.createdDateTime",
                "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)")));

        generator.generate();

        File eventFile = Arrays.stream(Objects.requireNonNull(outputJavaClassesDir.listFiles()))
                .filter(file -> file.getName().equalsIgnoreCase("Event.java")).findFirst()
                .orElseThrow(FileNotFoundException::new);
        assertThat(Utils.getFileContent(eventFile.getPath()), StringContains.containsString(
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)"
                        + System.lineSeparator() + "    private org.joda.time.DateTime createdDateTime;"));
    }

    @Test
    void generate_CustomAnnotationMappings_Type() throws Exception {
        mappingConfig.setCustomTypesMapping(
                new HashMap<>(singletonMap("DateTime", "org.joda.time.DateTime")));

        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("DateTime",
                "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)")));

        generator.generate();

        File eventFile = Arrays.stream(Objects.requireNonNull(outputJavaClassesDir.listFiles()))
                .filter(file -> file.getName().equalsIgnoreCase("Event.java")).findFirst()
                .orElseThrow(FileNotFoundException::new);
        assertThat(Utils.getFileContent(eventFile.getPath()), StringContains.containsString(
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)"
                        + System.lineSeparator() + "    private org.joda.time.DateTime createdDateTime;"));
    }

    @Test
    void generate_CustomAnnotationMappings_FieldType() throws Exception {
        mappingConfig
                .setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "org.joda.time.DateTime")));

        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Event.createdDateTime",
                "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)")));

        generator.generate();

        File eventFile = Arrays.stream(Objects.requireNonNull(outputJavaClassesDir.listFiles()))
                .filter(file -> file.getName().equalsIgnoreCase("Event.java")).findFirst()
                .orElseThrow(FileNotFoundException::new);
        assertThat(Utils.getFileContent(eventFile.getPath()), StringContains.containsString(
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)"
                        + System.lineSeparator() + "    private org.joda.time.DateTime createdDateTime;"));
    }

    @Test
    void generate_CustomSubscriptionReturnType() throws Exception {
        mappingConfig.setSubscriptionReturnType("org.reactivestreams.Publisher");

        generator.generate();

        File eventFile = Arrays.stream(Objects.requireNonNull(outputJavaClassesDir.listFiles()))
                .filter(file -> file.getName().equalsIgnoreCase("EventsCreatedSubscription.java")).findFirst()
                .orElseThrow(FileNotFoundException::new);
        assertThat(Utils.getFileContent(eventFile.getPath()), StringContains.containsString(
                "org.reactivestreams.Publisher<Collection<Event>> eventsCreated() throws Exception;"));
    }

    @Test
    void generate_NoPackage() throws Exception {
        mappingConfig.setPackageName(null);
        generator.generate();

        File[] files = Objects.requireNonNull(outputBuildDir.listFiles());
        File eventFile = Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase("Event.java")).findFirst()
                .orElseThrow(FileNotFoundException::new);

        assertThat(Utils.getFileContent(eventFile.getPath()), StringStartsWith.startsWith(
                "import java.util.*;" + System.lineSeparator() + System.lineSeparator() + "public class Event {"));
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
        Arrays.stream(apiFiles).forEach(file -> {
            try {
                assertThat(Utils.getFileContent(file.getPath()),
                        StringStartsWith.startsWith("package com.kobylynskyi.graphql.test1.api;"));
            } catch (IOException e) {
                fail(e);
            }
        });

        File[] modelFiles = Objects.requireNonNull(new File(outputJavaClassesDir, "model").listFiles());
        List<String> generatedModelFileNames = Arrays.stream(modelFiles).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("Event.java", "EventProperty.java", "EventStatus.java"), generatedModelFileNames);
        Arrays.stream(modelFiles).forEach(file -> {
            try {
                assertThat(Utils.getFileContent(file.getPath()),
                        StringStartsWith.startsWith("package com.kobylynskyi.graphql.test1.model;"));
            } catch (IOException e) {
                fail(e);
            }
        });
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

        assertEquals(Utils.getFileContent(
                "src/test/resources/expected-classes/EventPropertyTO_withEqualsAndHashCode.java.txt"),
                Utils.getFileContent(
                        Arrays.stream(files).filter(f -> f.getName().equals("EventPropertyTO.java")).map(File::getPath)
                                .findFirst().orElseThrow(FileNotFoundException::new)));
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
        generator.setSchemas(Collections.singletonList("src/test/resources/schemas/wrong.graphqls"));

        Assertions.assertThrows(NoSuchFileException.class, () -> generator.generate());
    }

    @Test
    void generate_NoQueries() throws Exception {
        mappingConfig.setPackageName("");
        generator.setSchemas(Collections.singletonList("src/test/resources/schemas/no-queries.graphqls"));
        generator.generate();

        File[] files = Objects.requireNonNull(outputBuildDir.listFiles());
        assertEquals(2, files.length);
        assertEquals(Utils.getFileContent("src/test/resources/expected-classes/EmptyMutation.java.txt"),
                Utils.getFileContent(
                        Arrays.stream(files).filter(f -> f.getName().equals("Mutation.java")).map(File::getPath)
                                .findFirst().orElseThrow(FileNotFoundException::new)));
        assertEquals(Utils.getFileContent("src/test/resources/expected-classes/EmptyQuery.java.txt"),
                Utils.getFileContent(
                        Arrays.stream(files).filter(f -> f.getName().equals("Query.java")).map(File::getPath)
                                .findFirst().orElseThrow(FileNotFoundException::new)));
    }

    @Test
    void generate_Empty() throws Exception {
        generator.setSchemas(Collections.singletonList("src/test/resources/schemas/empty.graphqls"));
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

        String importJavaUtilConcurrent = "import java.util.concurrent";

        assertFileContainsElements(files, "VersionQuery.java", importJavaUtilConcurrent,
                "CompletableFuture<String> version()");

        assertFileContainsElements(files, "EventsByCategoryAndStatusQuery.java", importJavaUtilConcurrent,
                "CompletableFuture<Collection<Event>> eventsByCategoryAndStatus(");

        assertFileContainsElements(files, "EventByIdQuery.java", importJavaUtilConcurrent,
                "CompletableFuture<Event> eventById(");

    }

    @Test
    void generate_AsyncMutationApis() throws Exception {
        mappingConfig.setGenerateAsyncApi(true);
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertFileContainsElements(files, "CreateEventMutation.java",
                "import java.util.concurrent", "CompletableFuture<Event> createEvent(");

    }

    private void assertFileContainsElements(File[] files, String fileName, String... elements)
            throws IOException {
        File file = getFile(files, fileName);

        assertNotNull(file);

        String fileContent = Utils.getFileContent(file.getPath());
        assertThat(fileContent, Matchers.stringContainsInOrder(elements));
    }

    private File getFile(File[] files, String fileName) {
        return Arrays.stream(files).filter(f -> f.getName().equals(fileName)).findFirst().get();
    }

}
