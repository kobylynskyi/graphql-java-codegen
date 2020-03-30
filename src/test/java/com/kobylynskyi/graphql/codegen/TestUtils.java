package com.kobylynskyi.graphql.codegen;

import java.io.File;
import java.io.IOException;

import com.kobylynskyi.graphql.codegen.utils.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {

    public static void assertSameTrimmedContent(File expected, File file) throws IOException {
        String expectedContent = Utils.getFileContent(expected.getPath()).trim();
        String actualContent = Utils.getFileContent(file.getPath()).trim();
        assertEquals(expectedContent, actualContent);
    }
}
