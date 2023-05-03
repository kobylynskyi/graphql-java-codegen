package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.TestUtils;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertFileContainsElements;
import static java.util.Collections.*;

class GraphQLCodegenApiReturnTypeTest {

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/test1");

    private MappingConfig mappingConfig;

    @BeforeEach
    void init() {
        mappingConfig = new MappingConfig();
        mappingConfig.setPackageName("com.kobylynskyi.graphql.test1");
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.KOTLIN);
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

        assertFileContainsElements(files, "EventPropertyResolver.kt",
                "fun child(eventProperty: EventProperty, first: Int?, last: Int?): java.util.concurrent.CompletionStage<graphql.execution.DataFetcherResult<List<EventProperty?>?>>");

        assertFileContainsElements(files, "EventPropertyResolver.kt",
                "fun parent(eventProperty: EventProperty, withStatus: EventStatus?, createdAfter: String?): java.util.concurrent.CompletionStage<graphql.execution.DataFetcherResult<Event?>>");
    }

    @Test
    void generate_ApiReturnType_And_ApiReturnListType_WithPlaceHolder() throws Exception {
        mappingConfig.setApiReturnType("java.util.concurrent.CompletionStage<graphql.execution.DataFetcherResult<{{TYPE}}>>");
        mappingConfig.setApiReturnListType("reactor.core.publisher.Mono<graphql.execution.DataFetcherResult<{{TYPE}}>>");

        generate("src/test/resources/schemas/test.graphqls");

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertFileContainsElements(files, "EventPropertyResolver.kt",
                "fun child(eventProperty: EventProperty, first: Int?, last: Int?): reactor.core.publisher.Mono<graphql.execution.DataFetcherResult<EventProperty?>>");

        assertFileContainsElements(files, "EventPropertyResolver.kt",
                "fun parent(eventProperty: EventProperty, withStatus: EventStatus?, createdAfter: String?): java.util.concurrent.CompletionStage<graphql.execution.DataFetcherResult<Event?>>");
    }

    private void generate(String path) throws IOException {
        new KotlinGraphQLCodegen(singletonList(path), outputBuildDir, mappingConfig,
                TestUtils.getStaticGeneratedInfo(mappingConfig)).generate();
    }

}
