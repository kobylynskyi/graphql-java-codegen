package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.utils.Utils;

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

}
