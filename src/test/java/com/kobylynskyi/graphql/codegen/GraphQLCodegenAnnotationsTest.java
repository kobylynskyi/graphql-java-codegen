package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertFileContainsElements;
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
                "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)")));

        new GraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
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
                "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)")));

        new GraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
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
                "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)")));

        new GraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.DateTimeScalarDeserializer.class)"
                        + System.lineSeparator() + "    private org.joda.time.DateTime createdDateTime;");
    }

    @Test
    void generate_CustomAnnotationMappings_Class() throws Exception {
        Map<String, String> customAnnotationsMapping = new HashMap<>();
        // input
        customAnnotationsMapping.put("AcceptTopicSuggestionInput", "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = AcceptTopicSuggestionInputDeserializer.class)");
        // type
        customAnnotationsMapping.put("AcceptTopicSuggestionPayload", "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = AcceptTopicSuggestionPayloadDeserializer.class)");
        // interface
        customAnnotationsMapping.put("Actor", "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = ActorDeserializer.class)");
        // union
        customAnnotationsMapping.put("Assignee", "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = AssigneeDeserializer.class)");
        // enum
        customAnnotationsMapping.put("DeploymentOrderField", "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = DeploymentOrderFieldDeserializer.class)");
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping);

        new GraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
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
    void generate_CustomAnnotationMappings_RequestResponseClasses() throws Exception {
        Map<String, String> customAnnotationsMapping = new HashMap<>();
        // request
        customAnnotationsMapping.put("CodeOfConductQueryRequest", "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = CodeOfConductQueryRequestDeserializer.class)");
        // response
        customAnnotationsMapping.put("CodeOfConductQueryResponse", "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = CodeOfConductQueryResponseDeserializer.class)");
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping);
        mappingConfig.setGenerateClient(true);

        new GraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "CodeOfConductQueryRequest.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = CodeOfConductQueryRequestDeserializer.class)"
                        + System.lineSeparator() + "public class CodeOfConductQueryRequest implements GraphQLOperationRequest {");
        assertFileContainsElements(files, "CodeOfConductQueryResponse.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = CodeOfConductQueryResponseDeserializer.class)"
                        + System.lineSeparator() + "public class CodeOfConductQueryResponse extends GraphQLResult<Map<String, CodeOfConduct>> {");
    }

}
