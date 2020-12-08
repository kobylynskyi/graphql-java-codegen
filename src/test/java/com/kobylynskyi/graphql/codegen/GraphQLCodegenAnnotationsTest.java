package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertFileContainsElements;
import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

class GraphQLCodegenAnnotationsTest {

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
    void generate_CustomAnnotationMappings() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("Event.createdDateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Event.createdDateTime",
                singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)"))));

        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)"
                        + System.lineSeparator() + "    private org.joda.time.DateTime createdDateTime;");
    }

    @Test
    void generate_CustomAnnotationMappings_Type() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("DateTime",
                singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)"))));

        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)"
                        + System.lineSeparator() + "    private org.joda.time.DateTime createdDateTime;");
    }

    @Test
    void generate_CustomAnnotationMappings_FieldType() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Event.createdDateTime",
                singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)"))));

        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)"
                        + System.lineSeparator() + "    private org.joda.time.DateTime createdDateTime;");
    }

    @Test
    void generate_CustomAnnotationMappings_Class() throws Exception {
        Map<String, List<String>> customAnnotationsMapping = new HashMap<>();
        // input
        customAnnotationsMapping.put("AcceptTopicSuggestionInput", singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = AcceptTopicSuggestionInputDeserializer.class)"));
        // type
        customAnnotationsMapping.put("AcceptTopicSuggestionPayload", singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = AcceptTopicSuggestionPayloadDeserializer.class)"));
        // interface
        customAnnotationsMapping.put("Actor", singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = ActorDeserializer.class)"));
        // union
        customAnnotationsMapping.put("Assignee", singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = AssigneeDeserializer.class)"));
        // enum
        customAnnotationsMapping.put("DeploymentOrderField", singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = DeploymentOrderFieldDeserializer.class)"));
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping);

        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "AcceptTopicSuggestionInput.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = AcceptTopicSuggestionInputDeserializer.class)"
                        + System.lineSeparator() + "public class AcceptTopicSuggestionInput ");
        assertFileContainsElements(files, "AcceptTopicSuggestionPayload.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = AcceptTopicSuggestionPayloadDeserializer.class)"
                        + System.lineSeparator() + "public class AcceptTopicSuggestionPayload ");
        assertFileContainsElements(files, "Actor.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = ActorDeserializer.class)"
                        + System.lineSeparator() + "public interface Actor ");
        assertFileContainsElements(files, "Assignee.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = AssigneeDeserializer.class)"
                        + System.lineSeparator() + "public interface Assignee ");
        assertFileContainsElements(files, "DeploymentOrderField.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = DeploymentOrderFieldDeserializer.class)"
                        + System.lineSeparator() + "public enum DeploymentOrderField ");
    }

    @Test
    void generate_CustomAnnotationMappings_Multiple() throws Exception {
        Map<String, List<String>> customAnnotationsMapping = new HashMap<>();
        // type
        customAnnotationsMapping.put("AcceptTopicSuggestionPayload", Arrays.asList(
                "@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = \"__typename\")",
                "@com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(io.github.kobylynskyi.order.external.starwars.AcceptTopicSuggestionPayloadTypeResolver.class)"));
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping);

        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "AcceptTopicSuggestionPayload.java", System.lineSeparator() +
                "@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = \"__typename\")"
                + System.lineSeparator() +
                "@com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(io.github.kobylynskyi.order.external.starwars.AcceptTopicSuggestionPayloadTypeResolver.class)"
                + System.lineSeparator() +
                "public class AcceptTopicSuggestionPayload ");
    }

    @Test
    void generate_CustomAnnotationMappings_RequestResponseClasses() throws Exception {
        Map<String, List<String>> customAnnotationsMapping = new HashMap<>();
        // request
        customAnnotationsMapping.put("CodeOfConductQueryRequest", singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = CodeOfConductQueryRequestDeserializer.class)"));
        // response
        customAnnotationsMapping.put("CodeOfConductQueryResponse", singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = CodeOfConductQueryResponseDeserializer.class)"));
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping);
        mappingConfig.setGenerateClient(true);

        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "CodeOfConductQueryRequest.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = CodeOfConductQueryRequestDeserializer.class)"
                        + System.lineSeparator() + "public class CodeOfConductQueryRequest implements GraphQLOperationRequest {");
        assertFileContainsElements(files, "CodeOfConductQueryResponse.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = CodeOfConductQueryResponseDeserializer.class)"
                        + System.lineSeparator() + "public class CodeOfConductQueryResponse extends GraphQLResult<Map<String, CodeOfConduct>> {");
    }

    @Test
    void generate_Directives() throws Exception {
        Map<String, List<String>> directiveAnnotationsMapping = new HashMap<>();
        directiveAnnotationsMapping.put("auth",
                singletonList("@com.example.CustomAnnotation(roles={{roles?toArray}}, boo={{boo?toArray}}, float={{float?toArrayOfStrings}}, int={{int}}, n={{n?toString}})"));
        mappingConfig.setDirectiveAnnotationsMapping(directiveAnnotationsMapping);

        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/annotation/CreateEventMutationResolver.java.txt"),
                getFileByName(files, "CreateEventMutationResolver.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/annotation/MutationResolver.java.txt"),
                getFileByName(files, "MutationResolver.java"));
    }

}
