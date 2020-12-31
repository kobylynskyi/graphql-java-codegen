package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GraphQLCodegenRequestTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/github/graphql");
    private final MappingConfig mappingConfig = new MappingConfig();

    @BeforeEach
    void init() {
        mappingConfig.setPackageName("com.github.graphql");
        mappingConfig.setResponseProjectionSuffix("ResponseProjection");
        mappingConfig.setRequestSuffix("Request");
        mappingConfig.setGenerateClient(true);
        mappingConfig.setGenerateEqualsAndHashCode(true);
        mappingConfig.setGenerateToString(false); // should be overridden to true
        mappingConfig.setGenerateApis(false);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_RequestAndResponseProjections() throws Exception {
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventResponseProjection.java.txt"),
                getFileByName(files, "EventResponseProjection.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventPropertyResponseProjection.java.txt"),
                getFileByName(files, "EventPropertyResponseProjection.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventPropertyParentParametrizedInput.java.txt"),
                getFileByName(files, "EventPropertyParentParametrizedInput.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventPropertyChildParametrizedInput.java.txt"),
                getFileByName(files, "EventPropertyChildParametrizedInput.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventsByCategoryAndStatusQueryRequest.java.txt"),
                getFileByName(files, "EventsByCategoryAndStatusQueryRequest.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/VersionQueryRequest.java.txt"),
                getFileByName(files, "VersionQueryRequest.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventsByIdsQueryRequest.java.txt"),
                getFileByName(files, "EventsByIdsQueryRequest.java"));
    }

    @Test
    void generate_WithModelSuffix() throws Exception {
        mappingConfig.setModelNameSuffix("TO");
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventStatusTO.java.txt"),
                getFileByName(files, "EventStatusTO.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventsByCategoryAndStatusQueryRequest_withModelSuffix.java.txt"),
                getFileByName(files, "EventsByCategoryAndStatusQueryRequest.java"));
        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventPropertyParentParametrizedInput_withModelSuffix.java.txt"),
                getFileByName(files, "EventPropertyParentParametrizedInput.java"));
    }

    @Test
    void generate_PrimitivesInsideParametrizedInput() throws Exception {
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/parametrized-input-client.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/ClientDataParametrizedInput.java.txt"),
                getFileByName(files, "ClientDataParametrizedInput.java"));
    }

    @Test
    void generate_RequestAndResponseProjections_github() throws Exception {
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/CodeOfConductResponseProjection.java.txt"),
                getFileByName(files, "CodeOfConductResponseProjection.java"));

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/UpdateRepositoryMutationRequest.java.txt"),
                getFileByName(files, "UpdateRepositoryMutationRequest.java"));
    }

    @Test
    void generate_ToStringIsEnabledForInput() throws Exception {
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/github.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/AcceptTopicSuggestionInput.java.txt"),
                getFileByName(files, "AcceptTopicSuggestionInput.java"));
    }

    @Test
    void generate_emptyRequestSuffix() throws Exception {
        mappingConfig.setRequestSuffix("");
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertNotNull(getFileByName(files, "EventsByCategoryAndStatusQuery.java"));
    }

    @Test
    void generate_noApiImportForModelClasses() throws Exception {
        mappingConfig.setApiPackageName("com.github.graphql.api");
        mappingConfig.setModelPackageName("com.github.graphql");
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertNotNull(getFileByName(files, "EventsByCategoryAndStatusQueryRequest.java"));
    }

    @Test
    void generate_apiImportForModelClassesIfResolverIsPresent() throws Exception {
        mappingConfig.setApiPackageName("com.github.graphql.api");
        mappingConfig.setModelPackageName("com.github.graphql");
        mappingConfig.setFieldsWithResolvers(singleton("Event"));
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventsByCategoryAndStatusQueryRequest_withApiImport.java.txt"),
                getFileByName(files, "EventsByCategoryAndStatusQueryRequest.java"));
    }

    @Test
    void generate_apiImportForModelClassesIfResolversExtensions() throws Exception {
        mappingConfig.setApiPackageName("com.github.graphql.api");
        mappingConfig.setModelPackageName("com.github.graphql");
        mappingConfig.setGenerateExtensionFieldsResolvers(true);
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventsByCategoryAndStatusQueryRequest_withApiImport.java.txt"),
                getFileByName(files, "EventsByCategoryAndStatusQueryRequest.java"));
    }

    @Test
    void generate_QueriesWithSameName() throws Exception {
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/queries-same-name.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/ProductsByCategoryIdAndStatusQueryRequest.java.txt"),
                getFileByName(files, "ProductsByCategoryIdAndStatusQueryRequest.java"));

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/ProductsByIdsQueryRequest.java.txt"),
                getFileByName(files, "ProductsByIdsQueryRequest.java"));
    }

    @Test
    void generate_ClassesWithoutPrimitiveTypes() throws Exception {
        mappingConfig.putCustomTypeMappingIfAbsent("Int!", "Integer");
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/EventPropertyChildParametrizedInput_withoutPrimitives.java.txt"),
                getFileByName(files, "EventPropertyChildParametrizedInput.java"));
    }

    @Test
    void generate_UseObjectMapperToSerializeFields_Type() throws Exception {
        mappingConfig.putCustomTypeMappingIfAbsent("DateTime", "java.time.ZonedDateTime");
        mappingConfig.setUseObjectMapperForRequestSerialization(singleton("DateTime"));
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/Event_useObjectMapperForRequestSerialization.java.txt"),
                getFileByName(files, "Event.java"));
    }

    @Test
    void generate_UseObjectMapperToSerializeFields_Field() throws Exception {
        mappingConfig.putCustomTypeMappingIfAbsent("Event.createdDateTime", "java.time.ZonedDateTime");
        mappingConfig.setUseObjectMapperForRequestSerialization(singleton("DateTime"));
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/Event_useObjectMapperForRequestSerialization.java.txt"),
                getFileByName(files, "Event.java"));
    }

    @Test
    void generate_UseObjectMapperToSerializeFields_Parameter() throws Exception {
        mappingConfig.setUseObjectMapperForRequestSerialization(singleton("ZonedDateTime"));
        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/date-scalar.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(new File("src/test/resources/expected-classes/request/QueryINeedQueryRequest_custom_serializer.java.txt"),
                getFileByName(files, "QueryINeedQueryRequest.java"));
    }

}