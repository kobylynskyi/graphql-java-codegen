package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertFileContainsElements;
import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Collections.singleton;
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
        mappingConfig.setCustomTypesMapping(new HashMap<>(
                singletonMap("Event.createdDateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Event.createdDateTime",
                singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = com.example.json.DateTimeScalarDeserializer.class)"))));

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = com.example.json.DateTimeScalarDeserializer.class)",
                "    private org.joda.time.DateTime createdDateTime;");
    }

    @Test
    void generate_CustomAnnotationMappings_Type() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("DateTime",
                singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = com.example.json.DateTimeScalarDeserializer.class)"))));

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = com.example.json.DateTimeScalarDeserializer.class)",
                "    private org.joda.time.DateTime createdDateTime;");
    }

    @Test
    void generate_CustomAnnotationMappings_Regexp() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Date.*",
                singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = com.example.json.DateTimeScalarDeserializer.class)"))));

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = com.example.json.DateTimeScalarDeserializer.class)",
                "    private org.joda.time.DateTime createdDateTime;");
    }

    @Test
    void generate_CustomAnnotationMappings_FieldType() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Event.createdDateTime",
                singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = com.example.json.DateTimeScalarDeserializer.class)"))));

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = com.example.json.DateTimeScalarDeserializer.class)",
                "    private org.joda.time.DateTime createdDateTime;");
    }

    @Test
    void generate_CustomAnnotationMappings_FieldType_Regexp() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Event..*Date.*",
                singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = com.example.json.DateTimeScalarDeserializer.class)"))));

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = com.example.json.DateTimeScalarDeserializer.class)",
                "    private org.joda.time.DateTime createdDateTime;");
    }

    @Test
    void generate_CustomAnnotationMappings_Class() throws Exception {
        Map<String, List<String>> customAnnotationsMapping = new HashMap<>();
        // input
        customAnnotationsMapping.put("AcceptTopicSuggestionInput",
                singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = AcceptTopicSuggestionInputDeserializer.class)"));
        // type
        customAnnotationsMapping.put("AcceptTopicSuggestionPayload",
                singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = AcceptTopicSuggestionPayloadDeserializer.class)"));
        // interface
        customAnnotationsMapping.put("Actor",
                singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = ActorDeserializer.class)"));
        // union
        customAnnotationsMapping.put("Assignee",
                singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = AssigneeDeserializer.class)"));
        // enum
        customAnnotationsMapping.put("DeploymentOrderField",
                singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = DeploymentOrderFieldDeserializer.class)"));
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping);

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "AcceptTopicSuggestionInput.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = AcceptTopicSuggestionInputDeserializer.class)",
                "public class AcceptTopicSuggestionInput ");
        assertFileContainsElements(files, "AcceptTopicSuggestionPayload.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = AcceptTopicSuggestionPayloadDeserializer.class)",
                "public class AcceptTopicSuggestionPayload ");
        assertFileContainsElements(files, "Actor.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = ActorDeserializer.class)",
                "public interface Actor ");
        assertFileContainsElements(files, "Assignee.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = AssigneeDeserializer.class)",
                "public interface Assignee ");
        assertFileContainsElements(files, "DeploymentOrderField.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = DeploymentOrderFieldDeserializer.class)",
                "public enum DeploymentOrderField ");
    }

    @Test
    void generate_CustomAnnotationMappings_Multiple() throws Exception {
        Map<String, List<String>> customAnnotationsMapping = new HashMap<>();
        // type
        customAnnotationsMapping.put("AcceptTopicSuggestionPayload", Arrays.asList(
                "@com.fasterxml.jackson.annotation.JsonTypeInfo(use = " +
                        "com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = \"__typename\")",
                "@com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(" +
                        "io.github.kobylynskyi.order.external.starwars." +
                        "AcceptTopicSuggestionPayloadTypeResolver.class)"));
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping);

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "AcceptTopicSuggestionPayload.java",
                "@com.fasterxml.jackson.annotation.JsonTypeInfo(",
                "use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = \"__typename\")",
                "@com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(",
                "io.github.kobylynskyi.order.external.starwars.AcceptTopicSuggestionPayloadTypeResolver.class)",
                "public class AcceptTopicSuggestionPayload ");
    }

    @Test
    void generate_ResolverArgumentAnnotations() throws Exception {
        mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(true);
        mappingConfig.setResolverArgumentAnnotations(singleton(
                "@org.springframework.graphql.data.method.annotation.Argument"));

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/annotation/" +
                        "CreateEventMutationResolver_ArgumentAnnotations.java.txt"),
                getFileByName(files, "CreateEventMutationResolver.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/annotation/QueryResolver_ArgumentAnnotations.java.txt"),
                getFileByName(files, "QueryResolver.java"));
    }

    @Test
    void generate_ParametrizedResolverAnnotations() throws Exception {
        mappingConfig.setModelNameSuffix("TO");
        mappingConfig.setFieldsWithResolvers(singleton("@customResolver"));
        mappingConfig.setParametrizedResolverAnnotations(singleton(
                "@org.springframework.graphql.data.method.annotation.SchemaMapping(typeName=\"{{TYPE_NAME}}\")"));

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/annotation/" +
                        "EventPropertyResolver_ParametrizedResolverAnnotations.java.txt"),
                getFileByName(files, "EventPropertyResolver.java"));
    }

    @Test
    void generate_CustomAnnotationMappings_RequestResponseClasses() throws Exception {
        Map<String, List<String>> customAnnotationsMapping = new HashMap<>();
        // request
        customAnnotationsMapping.put("CodeOfConductQueryRequest",
                singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = CodeOfConductQueryRequestDeserializer.class)"));
        // response
        customAnnotationsMapping.put("CodeOfConductQueryResponse",
                singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = CodeOfConductQueryResponseDeserializer.class)"));
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping);
        mappingConfig.setGenerateClient(true);

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "CodeOfConductQueryRequest.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = CodeOfConductQueryRequestDeserializer.class)",
                "public class CodeOfConductQueryRequest implements GraphQLOperationRequest {");
        assertFileContainsElements(files, "CodeOfConductQueryResponse.java",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = CodeOfConductQueryResponseDeserializer.class)",
                "public class CodeOfConductQueryResponse extends GraphQLResult<Map<String, CodeOfConduct>> {");
    }

    @Test
    void generate_Directives() throws Exception {
        Map<String, List<String>> directiveAnnotationsMapping = new HashMap<>();
        directiveAnnotationsMapping.put("auth",
                singletonList("@com.example.CustomAnnotation(" +
                        "roles={{roles?toArray}}, " +
                        "boo={{boo?toArray}}, " +
                        "float={{float?toArrayOfStrings}}, " +
                        "int={{int}}, " +
                        "n={{n?toString}})"));
        directiveAnnotationsMapping.put("valid", singletonList("@javax.validation.Valid"));
        directiveAnnotationsMapping.put("customResolver", singletonList("@com.example.CustomAnnotation"));
        directiveAnnotationsMapping.put("relationship",
                singletonList("@com.example.Relationship(type = {{type}}, direction = {{direction}})"));
        mappingConfig.setDirectiveAnnotationsMapping(directiveAnnotationsMapping);

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/annotation/CreateEventMutationResolver.java.txt"),
                getFileByName(files, "CreateEventMutationResolver.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/annotation/MutationResolver.java.txt"),
                getFileByName(files, "MutationResolver.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/annotation/EventProperty.java.txt"),
                getFileByName(files, "EventProperty.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/annotation/User.java.txt"),
                getFileByName(files, "User.java"));
    }

    @Test
    void generate_ModelValidationAnnotationForSubType() throws Exception {
        generate("src/test/resources/schemas/nullable-list-type-with-nullable-sub-types.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/annotation/" +
                        "NullableListReturnTypeWithMandatoryElementsQueryResolver.java.txt"),
                getFileByName(files, "NullableListReturnTypeWithMandatoryElementsQueryResolver.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/annotation/TypeWithNullableListType.java.txt"),
                getFileByName(files, "TypeWithNullableListType.java"));
    }

    @Test
    void generate_GeneratedAnnotation() throws Exception {
        mappingConfig.setGeneratedAnnotation("jakarta.annotation.Generated");

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.java", " */",
                "@jakarta.annotation.Generated(",
                "    value = \"com.kobylynskyi.graphql.codegen.GraphQLCodegen\",",
                "    date = \"2020-12-31T23:59:59-0500\"",
                ")",
                "public class Event implements java.io.Serializable {");
    }

    private void generate(String path) throws IOException {
        new JavaGraphQLCodegen(singletonList(path), outputBuildDir, mappingConfig,
                TestUtils.getStaticGeneratedInfo(mappingConfig)).generate();
    }

}
