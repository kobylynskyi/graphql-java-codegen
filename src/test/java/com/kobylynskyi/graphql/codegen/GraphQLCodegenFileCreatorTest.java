package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.DataModelFields;
import com.kobylynskyi.graphql.codegen.model.GeneratedInformation;
import com.kobylynskyi.graphql.codegen.model.exception.UnableToCreateFileException;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class GraphQLCodegenFileCreatorTest {

    public static final File OUTPUT_DIR = new File("build/dir");

    @AfterEach
    void cleanup() {
        Utils.deleteDir(OUTPUT_DIR);
    }

    @Test
    void generateFile() {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put(DataModelFields.CLASS_NAME, "Class1");
        dataModel.put(DataModelFields.ANNOTATIONS, Collections.emptyList());
        dataModel.put(DataModelFields.GENERATED_INFO, new GeneratedInformation());
        GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.enumTemplate, dataModel, OUTPUT_DIR);
        assertThrows(UnableToCreateFileException.class,
                () -> GraphQLCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.enumTemplate, dataModel, OUTPUT_DIR));
    }
}