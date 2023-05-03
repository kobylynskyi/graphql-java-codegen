package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.scala.ScalaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertFileContainsElements;
import static java.util.Collections.singletonList;

class GraphQLCodegenApiReturnTypeTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/test1");

    private MappingConfig mappingConfig;

    @BeforeEach
    void init() {
        mappingConfig = new MappingConfig();
        mappingConfig.setPackageName("com.kobylynskyi.graphql.test1");
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.JAVA);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_ApiReturnType_WithPlaceHolder() throws Exception {
        mappingConfig.setApiReturnType("java.util.concurrent.CompletionStage<graphql.execution.DataFetcherResult<{{TYPE}}>>");

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertFileContainsElements(files, "EventPropertyResolver.java",
                "java.util.concurrent.CompletionStage<graphql.execution.DataFetcherResult<java.util.List<EventProperty>>> child(EventProperty eventProperty, Integer first, Integer last) throws Exception;");

        assertFileContainsElements(files, "EventPropertyResolver.java",
                "java.util.concurrent.CompletionStage<graphql.execution.DataFetcherResult<Event>> parent(EventProperty eventProperty, EventStatus withStatus, String createdAfter) throws Exception;");
    }

    @Test
    void generate_ApiReturnType_And_ApiReturnListType_WithPlaceHolder() throws Exception {
        mappingConfig.setApiReturnType("java.util.concurrent.CompletionStage<graphql.execution.DataFetcherResult<{{TYPE}}>>");
        mappingConfig.setApiReturnListType("reactor.core.publisher.Mono<graphql.execution.DataFetcherResult<{{TYPE}}>>");

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertFileContainsElements(files, "EventPropertyResolver.java",
                "reactor.core.publisher.Mono<graphql.execution.DataFetcherResult<EventProperty>> child(EventProperty eventProperty, Integer first, Integer last) throws Exception;");

        assertFileContainsElements(files, "EventPropertyResolver.java",
                "java.util.concurrent.CompletionStage<graphql.execution.DataFetcherResult<Event>> parent(EventProperty eventProperty, EventStatus withStatus, String createdAfter) throws Exception;");
    }

    private void generate(String path) throws IOException {
        new JavaGraphQLCodegen(singletonList(path), outputBuildDir, mappingConfig,
                TestUtils.getStaticGeneratedInfo(mappingConfig)).generate();
    }

}
