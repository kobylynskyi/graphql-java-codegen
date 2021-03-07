package com.kobylynskyi.graphql.codegen.supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchemaFinderTest {

    private Path root;

    @BeforeEach
    public void setup() throws IOException {
        root = Files.createTempDirectory("test-schema");
        root.toFile().deleteOnExit();
    }

    @Test
    void nullRootDir_fails() {
        assertThrows(IllegalArgumentException.class, () -> new SchemaFinder(null));
    }

    @Test
    void absentRootDir_fails() {
        SchemaFinder finder = new SchemaFinder(Paths.get("does-not-exist"));
        assertThrows(NoSuchFileException.class, finder::findSchemas);
    }

    @Test
    void emptyRootDir() throws IOException {
        SchemaFinder finder = new SchemaFinder(root);
        assertEquals(Collections.emptyList(), finder.findSchemas());
    }

    @Test
    void emptyRootDir_nonRecursive() throws IOException {
        SchemaFinder finder = new SchemaFinder(root);
        finder.setRecursive(false);
        assertEquals(Collections.emptyList(), finder.findSchemas());
    }

    @Test
    void singleFile_matchesDefaultRegex_graphqls() throws IOException {
        Path singleFile = Files.createFile(root.resolve("single.graphqls"));

        SchemaFinder finder = new SchemaFinder(root);
        List<String> expected = Collections.singletonList(singleFile.toString());
        assertEquals(expected, finder.findSchemas());
    }

    @Test
    void singleFile_matchesDefaultRegex_grahql() throws IOException {
        Path singleFile = Files.createFile(root.resolve("single.graphql"));

        SchemaFinder finder = new SchemaFinder(root);
        List<String> expected = Collections.singletonList(singleFile.toString());
        assertEquals(expected, finder.findSchemas());
    }

    @Test
    void singleFile_matchesCustomRegex() throws IOException {
        Path singleFile = Files.createFile(root.resolve("my-file.txt"));

        SchemaFinder finder = new SchemaFinder(root);
        finder.setIncludePattern("my-.*\\.txt");
        List<String> expected = Collections.singletonList(singleFile.toString());
        assertEquals(expected, finder.findSchemas());
    }

    @Test
    void singleFile_filteredOutByRegex() throws IOException {
        Files.createFile(root.resolve("not-a-match.txt"));

        SchemaFinder finder = new SchemaFinder(root);
        finder.setIncludePattern("very-specific-regex");
        assertEquals(Collections.emptyList(), finder.findSchemas());
    }

    @Test
    void singleFile_filteredOutByRelativeExclude() throws IOException {
        String excludedFilename = "excluded.txt";
        Files.createFile(root.resolve(excludedFilename));

        SchemaFinder finder = new SchemaFinder(root);
        finder.setExcludedFiles(Collections.singleton(excludedFilename));
        assertEquals(Collections.emptyList(), finder.findSchemas());
    }

    @Test
    void singleFile_filteredOutByAbsoluteExclude() throws IOException {
        Path excludedFilePath = root.resolve("excluded.txt").toAbsolutePath();
        Files.createFile(excludedFilePath);

        SchemaFinder finder = new SchemaFinder(root);
        finder.setExcludedFiles(Collections.singleton(excludedFilePath.toString()));
        assertEquals(Collections.emptyList(), finder.findSchemas());
    }

    @Test
    void multipleFiles_shouldBeInAlphabeticalOrder() throws IOException {
        Path file1 = Files.createFile(root.resolve("file1.good"));
        Path file3 = Files.createFile(root.resolve("file3.good"));
        Path file2 = Files.createFile(root.resolve("file2.good"));

        List<String> expected = Arrays.asList(file1.toString(), file2.toString(), file3.toString());

        SchemaFinder finder = new SchemaFinder(root);
        finder.setIncludePattern("file\\d\\.good");
        assertEquals(expected, finder.findSchemas());
    }

    @Test
    void multipleFiles_withRegexFilter() throws IOException {
        Path singleFile = Files.createFile(root.resolve("abc.good"));
        Files.createFile(root.resolve("abc.bad"));

        List<String> expected = Collections.singletonList(singleFile.toString());

        SchemaFinder finder = new SchemaFinder(root);
        finder.setIncludePattern(".*\\.good");
        assertEquals(expected, finder.findSchemas());
    }

    @Test
    void multipleFiles_withExclude() throws IOException {
        Path singleFile = Files.createFile(root.resolve("abc.good"));
        String excludedFilename = "excluded.good";
        Files.createFile(root.resolve(excludedFilename));

        List<String> expected = Collections.singletonList(singleFile.toString());

        SchemaFinder finder = new SchemaFinder(root);
        finder.setIncludePattern(".*\\.good");
        finder.setExcludedFiles(Collections.singleton(excludedFilename));
        assertEquals(expected, finder.findSchemas());
    }

    @Test
    void nestedDir_recursive() throws IOException {
        Path rootFile = Files.createFile(root.resolve("abc.good"));
        Path dir1 = Files.createDirectory(root.resolve("dir1"));
        Path dir1File = Files.createFile(dir1.resolve("file1.good"));

        SchemaFinder finder = new SchemaFinder(root);
        finder.setIncludePattern(".*\\.good");
        finder.setRecursive(true);

        List<String> expectedRec = Arrays.asList(rootFile.toString(), dir1File.toString());
        assertEquals(expectedRec, finder.findSchemas());
    }

    @Test
    void nestedDir_nonRecursive() throws IOException {
        Path rootFile = Files.createFile(root.resolve("abc.good"));
        Path dir1 = Files.createDirectory(root.resolve("dir1"));
        Files.createFile(dir1.resolve("file1.good"));

        SchemaFinder finder = new SchemaFinder(root);
        finder.setIncludePattern(".*\\.good");
        finder.setRecursive(false);

        List<String> expected = Collections.singletonList(rootFile.toString());
        assertEquals(expected, finder.findSchemas());
    }

    @Test
    void nestedDir_mixedCases() throws IOException {
        Path rootFile = Files.createFile(root.resolve("abc.good"));
        Path dir1 = Files.createDirectory(root.resolve("dir1"));
        Path dir2 = Files.createDirectory(root.resolve("dir2"));
        Files.createFile(root.resolve("abc.bad"));
        Files.createFile(root.resolve("excluded1.good"));

        // inside dir1 (mix subdirs, good files, bad files)
        Path dir1File = Files.createFile(dir1.resolve("file1.good"));
        Path subdir = Files.createDirectory(dir1.resolve("subdir"));
        Files.createDirectory(dir1.resolve("empty")); // empty subdir
        Files.createFile(dir1.resolve("abc.bad"));

        // inside dir1/subdir (only good files)
        Path subdirFile = Files.createFile(subdir.resolve("subdirFile.good"));

        // inside dir2 (only bad files)
        Files.createFile(dir2.resolve("abc.bad"));
        Files.createFile(dir2.resolve("excluded2.good"));

        List<String> expected = Collections.singletonList(rootFile.toString());

        SchemaFinder finder = new SchemaFinder(root);
        finder.setIncludePattern(".*\\.good");
        finder.setExcludedFiles(new HashSet<>(Arrays.asList("excluded1.good", "dir2/excluded2.good")));
        finder.setRecursive(false);
        assertEquals(expected, finder.findSchemas());

        List<String> expectedRec = Arrays.asList(rootFile.toString(), dir1File.toString(), subdirFile.toString());
        finder.setRecursive(true);
        assertEquals(expectedRec, finder.findSchemas());
    }
}