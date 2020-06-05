package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.SerialVersionUIDGenerator;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {

    public static File getFileByName(File[] files, String fileName) throws FileNotFoundException {
        return Arrays.stream(files)
                .filter(f -> f.getName().equalsIgnoreCase(fileName))
                .findFirst()
                .orElseThrow(FileNotFoundException::new);
    }

    public static void assertSameTrimmedContent(File expected, File file) throws IOException {
        String expectedContent = Utils.getFileContent(expected.getPath()).trim();
        String actualContent = Utils.getFileContent(file.getPath()).trim();
        assertEquals(expectedContent, actualContent);
    }

    public static MappingConfig initMappingConfig() {
        MappingConfig mappingConfig = new MappingConfig();
        mappingConfig.setSerialVersionUIDGenerator(getSerialVersionUIDGenerator());
        return mappingConfig;
    }

    public static SerialVersionUIDGenerator getSerialVersionUIDGenerator() {
        SerialVersionUIDGenerator serialVersionUIDGenerator = Mockito.mock(SerialVersionUIDGenerator.class);
        Mockito.when(serialVersionUIDGenerator.randomLong()).thenReturn(123456L);
        return serialVersionUIDGenerator;
    }

}
