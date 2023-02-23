package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
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
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.SCALA);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_CustomAnnotationMappings() throws Exception {
        mappingConfig
                .setCustomTypesMapping(new HashMap<>(singletonMap("Event.createdDateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Event.createdDateTime",
                singletonList(
                        "@com.fasterxml.jackson.databind" +
                                ".annotation.JsonDeserialize(using =" +
                                " classOf[com.example.json" +
                                ".DateTimeScalarDeserializer])"))));

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.scala",
                "    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = " +
                        "classOf[com.example.json.DateTimeScalarDeserializer])" + System.lineSeparator() +
                        "    createdDateTime: org.joda.time.DateTime,");
    }

    @Test
    void generate_CustomAnnotationMappings_Class() throws Exception {
        Map<String, List<String>> customAnnotationsMapping = new HashMap<>();
        // input
        customAnnotationsMapping.put("AcceptTopicSuggestionInput", singletonList(
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = " +
                        "classOf[AcceptTopicSuggestionInputDeserializer])"));
        // type
        customAnnotationsMapping.put("AcceptTopicSuggestionPayload", singletonList(
                "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = " +
                        "classOf[AcceptTopicSuggestionPayloadDeserializer])"));
        // interface
        customAnnotationsMapping.put("Actor", singletonList(
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[ActorDeserializer])"));
        // union
        customAnnotationsMapping.put("Assignee", singletonList(
                "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[AssigneeDeserializer])"));
        // enum
        customAnnotationsMapping.put("DeploymentOrderField", singletonList(
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = " +
                        "classOf[DeploymentOrderFieldDeserializer])"));
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping);

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "AcceptTopicSuggestionInput.scala",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = " +
                        "classOf[AcceptTopicSuggestionInputDeserializer])" + System.lineSeparator() +
                        "case class AcceptTopicSuggestionInput");
        assertFileContainsElements(files, "AcceptTopicSuggestionPayload.scala",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = " +
                        "classOf[AcceptTopicSuggestionPayloadDeserializer])" + System.lineSeparator() +
                        "case class AcceptTopicSuggestionPayload");
        assertFileContainsElements(files, "Actor.scala",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = " +
                        "classOf[ActorDeserializer])" + System.lineSeparator() +
                        "trait Actor");
        assertFileContainsElements(files, "Assignee.scala",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = " +
                        "classOf[AssigneeDeserializer])" + System.lineSeparator() +
                        "trait Assignee");
        assertFileContainsElements(files, "DeploymentOrderField.scala",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = " +
                        "classOf[DeploymentOrderFieldDeserializer])" + System.lineSeparator() +
                        "object DeploymentOrderField extends Enumeration");
    }

    @Test
    void generate_CustomAnnotationMappings_Multiple() throws Exception {
        Map<String, List<String>> customAnnotationsMapping = new HashMap<>();
        // type
        customAnnotationsMapping.put("AcceptTopicSuggestionPayload", Arrays.asList(
                "@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id"
                        + ".NAME, property = \"__typename\")",
                "@com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(classOf[io.github.kobylynskyi.order"
                        + ".external.starwars.AcceptTopicSuggestionPayloadTypeResolver])"));
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping);

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "AcceptTopicSuggestionPayload.scala",
                "@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson" +
                        ".annotation.JsonTypeInfo.Id.NAME, property = \"__typename\")" + System.lineSeparator() +
                        "@com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(classOf[io.github" +
                        ".kobylynskyi.order.external.starwars.AcceptTopicSuggestionPayloadTypeResolver])" +
                        System.lineSeparator() +
                        "case class AcceptTopicSuggestionPayload");
    }

    @Test
    void generate_CustomAnnotationMappings_RequestResponseClasses() throws Exception {
        Map<String, List<String>> customAnnotationsMapping = new HashMap<>();
        // request
        customAnnotationsMapping.put("CodeOfConductQueryRequest", singletonList(
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = " +
                        "classOf[CodeOfConductQueryRequestDeserializer])"));
        // response
        customAnnotationsMapping.put("CodeOfConductQueryResponse", singletonList(
                "com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = " +
                        "classOf[CodeOfConductQueryResponseDeserializer])"));
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping);
        mappingConfig.setGenerateClient(true);

        generate("src/test/resources/schemas/github.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "CodeOfConductQueryRequest.scala",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = " +
                        "classOf[CodeOfConductQueryRequestDeserializer])" + System.lineSeparator() +
                        "class CodeOfConductQueryRequest");
        assertFileContainsElements(files, "CodeOfConductQueryResponse.scala",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = " +
                        "classOf[CodeOfConductQueryResponseDeserializer])" + System.lineSeparator() +
                        "class CodeOfConductQueryResponse extends GraphQLResult[JMap[String, " +
                        "CodeOfConduct]]");
    }

    @Test
    void generate_Directives() throws Exception {
        Map<String, List<String>> directiveAnnotationsMapping = new HashMap<>();
        directiveAnnotationsMapping.put("auth",
                singletonList(
                        "@com.example.CustomAnnotation(roles={{roles?toArray}}, " +
                                "boo={{boo?toArray}}, float={{float?toArrayOfStrings}}, int={{int}}, " +
                                "n={{n?toString}})"));
        directiveAnnotationsMapping.put("relationship",
                singletonList("@com.example.Relationship(type = {{type}}, direction = {{direction}})"));
        mappingConfig.setDirectiveAnnotationsMapping(directiveAnnotationsMapping);

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/scala/annotation/CreateEventMutationResolver.scala.txt"),
                getFileByName(files, "CreateEventMutationResolver.scala"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/scala/annotation/MutationResolver.scala.txt"),
                getFileByName(files, "MutationResolver.scala"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/scala/annotation/User.scala.txt"),
                getFileByName(files, "User.scala"));
    }

    private void generate(String path) throws IOException {
        new ScalaGraphQLCodegen(singletonList(path), outputBuildDir, mappingConfig,
                TestUtils.getStaticGeneratedInfo(mappingConfig)).generate();
    }

}
