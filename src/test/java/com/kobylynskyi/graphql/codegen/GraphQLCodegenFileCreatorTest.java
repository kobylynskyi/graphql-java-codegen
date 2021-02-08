package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.java.JavaMapperFactoryImpl;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapperFactory;
import com.kobylynskyi.graphql.codegen.model.DataModelFields;
import com.kobylynskyi.graphql.codegen.model.GeneratedInformation;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDocument;
import com.kobylynskyi.graphql.codegen.model.exception.UnableToCreateFileException;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GraphQLCodegenFileCreatorTest {

    public static final File OUTPUT_DIR = new File("build/dir");
    private static final MapperFactory MAPPER_FACTORY = new JavaMapperFactoryImpl();

    @AfterEach
    void cleanup() {
        Utils.deleteDir(OUTPUT_DIR);
    }

    @Test
    void generateFile() throws IOException {
        MappingConfig mappingConfig = new MappingConfig();
        mappingConfig.setGeneratedLanguage(GeneratedLanguage.JAVA);
        ExtendedDocument extendedDocument = GraphQLDocumentParser.getDocumentFromSchemas(mappingConfig, singletonList("src/test/resources/schemas/test.graphqls"));
        MappingContext mappingContext = new MappingContext(mappingConfig, extendedDocument, new GeneratedInformation(), new DataModelMapperFactory(MAPPER_FACTORY));

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put(DataModelFields.CLASS_NAME, "Class1");
        dataModel.put(DataModelFields.ANNOTATIONS, Collections.emptyList());
        dataModel.put(DataModelFields.GENERATED_ANNOTATION, false);
        dataModel.put(DataModelFields.GENERATED_INFO, new GeneratedInformation());

        GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.ENUM, dataModel, OUTPUT_DIR);
        assertThrows(UnableToCreateFileException.class,
                () -> GraphQLCodegenFileCreator.generateFile(mappingContext, FreeMarkerTemplateType.ENUM, dataModel, OUTPUT_DIR));
    }
}
