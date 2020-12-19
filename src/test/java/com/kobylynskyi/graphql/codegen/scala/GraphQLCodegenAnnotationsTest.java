package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
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
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.SCALA);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_CustomAnnotationMappings() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("Event.createdDateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Event.createdDateTime",
                singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[com.example.json.DateTimeScalarDeserializer])"))));

        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.scala",
                "    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[com.example.json.DateTimeScalarDeserializer])\n" +
                        "    createdDateTime: org.joda.time.DateTime,");
    }

    @Test
    void generate_CustomAnnotationMappings_Class() throws Exception {
        Map<String, List<String>> customAnnotationsMapping = new HashMap<>();
        // input
        customAnnotationsMapping.put("AcceptTopicSuggestionInput", singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[AcceptTopicSuggestionInputDeserializer])"));
        // type
        customAnnotationsMapping.put("AcceptTopicSuggestionPayload", singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[AcceptTopicSuggestionPayloadDeserializer])"));
        // interface
        customAnnotationsMapping.put("Actor", singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[ActorDeserializer])"));
        // union
        customAnnotationsMapping.put("Assignee", singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[AssigneeDeserializer])"));
        // enum
        customAnnotationsMapping.put("DeploymentOrderField", singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[DeploymentOrderFieldDeserializer])"));
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping);

        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "AcceptTopicSuggestionInput.scala",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[AcceptTopicSuggestionInputDeserializer])\n" +
                        "case class AcceptTopicSuggestionInput");
        assertFileContainsElements(files, "AcceptTopicSuggestionPayload.scala",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[AcceptTopicSuggestionPayloadDeserializer])\n" +
                        "case class AcceptTopicSuggestionPayload");
        assertFileContainsElements(files, "Actor.scala",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[ActorDeserializer])\n" +
                        "trait Actor");
        assertFileContainsElements(files, "Assignee.scala",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[AssigneeDeserializer])\n" +
                        "trait Assignee");
        assertFileContainsElements(files, "DeploymentOrderField.scala",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[DeploymentOrderFieldDeserializer])\n" +
                        "object DeploymentOrderField extends Enumeration");
    }

    @Test
    void generate_CustomAnnotationMappings_Multiple() throws Exception {
        Map<String, List<String>> customAnnotationsMapping = new HashMap<>();
        // type
        customAnnotationsMapping.put("AcceptTopicSuggestionPayload", Arrays.asList(
                "@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = \"__typename\")",
                "@com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(classOf[io.github.kobylynskyi.order.external.starwars.AcceptTopicSuggestionPayloadTypeResolver])"));
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping);

        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "AcceptTopicSuggestionPayload.scala", "@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = \"__typename\")\n" +
                "@com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(classOf[io.github.kobylynskyi.order.external.starwars.AcceptTopicSuggestionPayloadTypeResolver])\n" +
                "case class AcceptTopicSuggestionPayload");
    }

    @Test
    void generate_CustomAnnotationMappings_RequestResponseClasses() throws Exception {
        Map<String, List<String>> customAnnotationsMapping = new HashMap<>();
        // request
        customAnnotationsMapping.put("CodeOfConductQueryRequest", singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[CodeOfConductQueryRequestDeserializer])"));
        // response
        customAnnotationsMapping.put("CodeOfConductQueryResponse", singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[CodeOfConductQueryResponseDeserializer])"));
        mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping);
        mappingConfig.setGenerateClient(true);

        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "CodeOfConductQueryRequest.scala",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[CodeOfConductQueryRequestDeserializer])\n" +
                        "class CodeOfConductQueryRequest");
        assertFileContainsElements(files, "CodeOfConductQueryResponse.scala",
                "@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = classOf[CodeOfConductQueryResponseDeserializer])\n" +
                        "class CodeOfConductQueryResponse extends GraphQLResult[JMap[String, CodeOfConduct]]");
    }

    @Test
    void generate_Directives() throws Exception {
        Map<String, List<String>> directiveAnnotationsMapping = new HashMap<>();
        directiveAnnotationsMapping.put("auth",
                singletonList("@com.example.CustomAnnotation(roles={{roles?toArray}}, boo={{boo?toArray}}, float={{float?toArrayOfStrings}}, int={{int}}, n={{n?toString}})"));
        mappingConfig.setDirectiveAnnotationsMapping(directiveAnnotationsMapping);

        new ScalaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/scala/annotation/CreateEventMutationResolver.scala.txt"),
                getFileByName(files, "CreateEventMutationResolver.scala"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/scala/annotation/MutationResolver.scala.txt"),
                getFileByName(files, "MutationResolver.scala"));
    }

}
