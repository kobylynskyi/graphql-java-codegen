package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.Objects;

import static com.kobylynskyi.graphql.codegen.TestUtils.assertSameTrimmedContent;
import static com.kobylynskyi.graphql.codegen.TestUtils.getFileByName;
import static java.util.Collections.singletonList;

class GraphQLCodegenParentInterfacesTest {

    private final MappingConfig mappingConfig = new MappingConfig();

    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/interfaces");

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_CheckFiles() throws Exception {
        mappingConfig.setPackageName("com.kobylynskyi.graphql.interfaces");
        mappingConfig.setModelNameSuffix("TO");
        mappingConfig.setFieldsWithResolvers(Collections.singleton("Event"));
        mappingConfig.setQueryResolverParentInterface("graphql.kickstart.tools.GraphQLQueryResolver");
        mappingConfig.setMutationResolverParentInterface("graphql.kickstart.tools.GraphQLMutationResolver");
        mappingConfig.setSubscriptionResolverParentInterface("graphql.kickstart.tools.GraphQLSubscriptionResolver");
        mappingConfig.setResolverParentInterface("graphql.kickstart.tools.GraphQLResolver<{{TYPE}}>");

        new JavaGraphQLCodegen(singletonList("src/test/resources/schemas/test.graphqls"),
                outputBuildDir, mappingConfig, TestUtils.getStaticGeneratedInfo()).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());

        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/parent-interfaces/QueryResolver.java.txt"),
                getFileByName(files, "QueryResolver.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/parent-interfaces/MutationResolver.java.txt"),
                getFileByName(files, "MutationResolver.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/parent-interfaces/SubscriptionResolver.java.txt"),
                getFileByName(files, "SubscriptionResolver.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/parent-interfaces/VersionQueryResolver.java.txt"),
                getFileByName(files, "VersionQueryResolver.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/parent-interfaces/CreateEventMutationResolver.java.txt"),
                getFileByName(files, "CreateEventMutationResolver.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/parent-interfaces/EventsCreatedSubscriptionResolver.java.txt"),
                getFileByName(files, "EventsCreatedSubscriptionResolver.java"));
        assertSameTrimmedContent(
                new File("src/test/resources/expected-classes/parent-interfaces/EventResolver.java.txt"),
                getFileByName(files, "EventResolver.java"));
    }
}
