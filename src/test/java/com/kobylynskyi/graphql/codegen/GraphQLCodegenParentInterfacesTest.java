package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;

class GraphQLCodegenParentInterfacesTest {

    private GraphQLCodegen generator;
    private final MappingConfig mappingConfig = new MappingConfig();

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/interfaces");

    @BeforeEach
    void init() {
        mappingConfig.setPackageName("com.kobylynskyi.graphql.interfaces");
        generator = new GraphQLCodegen(Collections.singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig);
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(new File("build/generated"));
    }

    @Test
    void generate_CheckFiles() throws Exception {
        mappingConfig.setModelNameSuffix("TO");
        mappingConfig.setFieldsWithResolvers(Collections.singleton("Event"));
        mappingConfig.setQueryResolverParentInterface("graphql.kickstart.tools.GraphQLQueryResolver");
        mappingConfig.setMutationResolverParentInterface("graphql.kickstart.tools.GraphQLMutationResolver");
        mappingConfig.setSubscriptionResolverParentInterface("graphql.kickstart.tools.GraphQLSubscriptionResolver");
        mappingConfig.setResolverParentInterface("graphql.kickstart.tools.GraphQLResolver<{{TYPE}}>");
        generator.generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/parent-interfaces/Query.java.txt"),
                getFileByName(files, "Query.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/parent-interfaces/Mutation.java.txt"),
                getFileByName(files, "Mutation.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/parent-interfaces/Subscription.java.txt"),
                getFileByName(files, "Subscription.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/parent-interfaces/VersionQuery.java.txt"),
                getFileByName(files, "VersionQuery.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/parent-interfaces/CreateEventMutation.java.txt"),
                getFileByName(files, "CreateEventMutation.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/parent-interfaces/EventsCreatedSubscription.java.txt"),
                getFileByName(files, "EventsCreatedSubscription.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/parent-interfaces/EventResolver.java.txt"),
                getFileByName(files, "EventResolver.java"));
    }
}
