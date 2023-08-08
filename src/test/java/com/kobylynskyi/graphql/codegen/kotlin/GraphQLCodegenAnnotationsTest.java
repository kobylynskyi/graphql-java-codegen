package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.MaxQueryTokensExtension;
import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertFileContainsElements;
import static java.lang.System.lineSeparator;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

@ExtendWith(MaxQueryTokensExtension.class)
class GraphQLCodegenAnnotationsTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/test1");

    private MappingConfig mappingConfig;

    @BeforeEach
    void init() {
        mappingConfig = new MappingConfig();
        mappingConfig.setPackageName("com.kobylynskyi.graphql.test1");
        mappingConfig.setGenerateParameterizedFieldsResolvers(false);
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.KOTLIN);
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
        assertFileContainsElements(files, "Event.kt",
                "@field:com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = com.example.json.DateTimeScalarDeserializer.class)",
                "    val createdDateTime: org.joda.time.DateTime?");
    }

    @Test
    void generate_CustomAnnotationMappings_Type() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("DateTime",
                singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = com.example.json.DateTimeScalarDeserializer.class)"))));

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.kt",
                "@field:com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = com.example.json.DateTimeScalarDeserializer.class)",
                "    val createdDateTime: org.joda.time.DateTime?");
    }

    @Test
    void generate_CustomAnnotationMappings_Input() throws Exception {
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("ReproInput.reproField",
                singletonList("@com.fasterxml.jackson.annotation.JsonProperty(\"reproField\")"))));

        generate("src/test/resources/schemas/input.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "ReproInput.kt",
                "    @field:com.fasterxml.jackson.annotation.JsonProperty(\"reproField\")" + lineSeparator() +
                        "    val reproField: List<String>");
    }

    @Test
    void generate_CustomAnnotationMappings_Regexp() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Date.*",
                singletonList("com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = com.example.json.DateTimeScalarDeserializer.class)"))));

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.kt",
                "@field:com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = com.example.json.DateTimeScalarDeserializer.class)",
                "    val createdDateTime: org.joda.time.DateTime?");
    }

    @Test
    void generate_CustomAnnotationMappings_FieldType() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(singletonMap("DateTime", "org.joda.time.DateTime")));
        mappingConfig.setCustomAnnotationsMapping(new HashMap<>(singletonMap("Event.createdDateTime",
                singletonList("@com.fasterxml.jackson.databind.annotation.JsonDeserialize(" +
                        "using = com.example.json.DateTimeScalarDeserializer.class)"))));

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "Event.kt",
                "@field:com.fasterxml.jackson.databind.annotation.JsonDeserialize(",
                "using = com.example.json.DateTimeScalarDeserializer.class)",
                "    val createdDateTime: org.joda.time.DateTime?");
    }

    @Test
    void generate_CustomAnnotationMappings_With_Annotations() throws Exception {
        mappingConfig.setCustomTypesMapping(new HashMap<>(
                singletonMap("CAMS", "com.intuit.identity.manage.enum.CamsGroup::class")));
        mappingConfig.setDirectiveAnnotationsMapping(new HashMap<>(singletonMap("NotNull",
                singletonList("@javax.validation.constraints.NotNull(message = {{message}}, groups = {{groups}})"))));

        generate("src/test/resources/schemas/kt/customTypesMapping-directive.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        assertFileContainsElements(files, "TrustAccountInput.kt",
                "@field:javax.validation.constraints.NotNull(message = \"test\", " +
                "groups = com.intuit.identity.manage.enum.CamsGroup::class)");
    }
    
    private void generate(String path) throws IOException {
        new KotlinGraphQLCodegen(singletonList(path), outputBuildDir, mappingConfig,
                TestUtils.getStaticGeneratedInfo(mappingConfig)).generate();
    }

}
