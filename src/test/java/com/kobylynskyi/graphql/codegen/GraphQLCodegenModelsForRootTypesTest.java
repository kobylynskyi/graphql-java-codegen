package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.GeneratedInformation;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GraphQLCodegenModelsForRootTypesTest {

    private final MappingConfig mappingConfig = new MappingConfig();
    private final GeneratedInformation staticGeneratedInfo = TestUtils.getStaticGeneratedInfo();
    private final File outputBuildDir = new File("build/generated");
    private final File outputJavaClassesDir = new File("build/generated/com/kobylynskyi/graphql/rootmodels");
    public static final List<String> SCHEMAS = singletonList("src/test/resources/schemas/test.graphqls");

    @BeforeEach
    void init() {
        mappingConfig.setGenerateModelsForRootTypes(true);
        mappingConfig.setPackageName("com.kobylynskyi.graphql.rootmodels");
    }

    @AfterEach
    void cleanup() {
        Utils.deleteDir(outputBuildDir);
    }

    @Test
    void generate_sameModelSuffixes() {
        mappingConfig.setApiNameSuffix("");
        mappingConfig.setModelNameSuffix(null);
        mappingConfig.setApiNamePrefix(null);
        mappingConfig.setModelNamePrefix("");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> new JavaGraphQLCodegen(SCHEMAS, outputBuildDir, mappingConfig, staticGeneratedInfo),
                "Expected generate() to throw, but it didn't");

        assertEquals("Either disable APIs generation or set different Prefix/Suffix for API classes and model classes",
                thrown.getMessage());
    }

    @Test
    void generate_sameResolverSuffixes() {
        // by default apiNamePrefix is same as typeResolverSuffix
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> new JavaGraphQLCodegen(SCHEMAS, outputBuildDir, mappingConfig, staticGeneratedInfo),
                "Expected generate() to throw, but it didn't");

        assertEquals("Either disable APIs generation or set different Prefix/Suffix for API classes and type resolver classes",
                thrown.getMessage());
    }

    @Test
    void generate_CheckFiles_generateApisFalse() throws Exception {
        mappingConfig.setGenerateApis(false);

        new JavaGraphQLCodegen(SCHEMAS, outputBuildDir, mappingConfig, staticGeneratedInfo).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("Event.java", "EventProperty.java", "EventStatus.java",
                "Mutation.java", "Query.java", "Subscription.java"),
                generatedFileNames);
    }

    @Test
    void generate_CheckFiles_generateApisTrue_CustomTypeResolverSuffix() throws Exception {
        mappingConfig.setTypeResolverSuffix("TypeResolver");

        new JavaGraphQLCodegen(SCHEMAS, outputBuildDir, mappingConfig, staticGeneratedInfo).generate();

        File[] files = Objects.requireNonNull(outputJavaClassesDir.listFiles());
        List<String> generatedFileNames = Arrays.stream(files).map(File::getName).sorted().collect(toList());
        assertEquals(Arrays.asList("CreateEventMutationResolver.java", "Event.java", "EventByIdQueryResolver.java",
                "EventProperty.java", "EventPropertyTypeResolver.java", "EventStatus.java",
                "EventsByCategoryAndStatusQueryResolver.java", "EventsByIdsQueryResolver.java",
                "EventsCreatedSubscriptionResolver.java", "Mutation.java", "MutationResolver.java",
                "MutationTypeResolver.java", "Query.java", "QueryResolver.java", "QueryTypeResolver.java",
                "Subscription.java", "SubscriptionResolver.java", "VersionQueryResolver.java"), generatedFileNames);
    }

}
