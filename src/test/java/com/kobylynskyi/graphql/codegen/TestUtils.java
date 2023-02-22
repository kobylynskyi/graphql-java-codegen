package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.GeneratedInformation;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import org.hamcrest.Matchers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {

    public static final ZonedDateTime GENERATED_DATE_TIME =
            ZonedDateTime.parse("2020-12-31T23:59:59-0500", GeneratedInformation.DATE_TIME_FORMAT);

    public static File getFileByName(File[] files, String fileName) throws FileNotFoundException {
        return Arrays.stream(files)
                .filter(f -> f.getName().equalsIgnoreCase(fileName))
                .findFirst()
                .orElseThrow(FileNotFoundException::new);
    }

    public static void assertFileContainsElements(File[] files, String fileName, String... elements)
            throws IOException {
        File file = getFileByName(files, fileName);
        String fileContent = Utils.getFileContent(file.getPath());
        assertThat(fileContent, Matchers.stringContainsInOrder(elements));
    }

    public static void assertSameTrimmedContent(File expected, File file) throws IOException {
        String expectedContent = Utils.getFileContent(expected.getPath()).trim();
        String actualContent = Utils.getFileContent(file.getPath()).trim();
        if (atLeastJava9()) {
            // doing this hack in order to make the build work on all Java versions
            actualContent = actualContent.replace(
                    "javax.annotation.processing.Generated",
                    "javax.annotation.Generated");
        }
        assertEquals(expectedContent, actualContent);
    }

    private static boolean atLeastJava9() {
        try {
            Class.forName("javax.annotation.processing.Generated");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static GeneratedInformation getStaticGeneratedInfo() {
        GeneratedInformation generatedInformation = new GeneratedInformation();
        generatedInformation.setDateTimeSupplier(() -> GENERATED_DATE_TIME);
        return generatedInformation;
    }

}
