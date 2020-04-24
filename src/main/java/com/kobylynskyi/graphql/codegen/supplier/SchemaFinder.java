package com.kobylynskyi.graphql.codegen.supplier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Walks a directory tree to find GraphQL schema files.
 */
public class SchemaFinder {

    public static final String DEFAULT_INCLUDE_PATTERN = ".*\\.graphqls?";

    public static final boolean DEFAULT_RECURSIVE = true;

    private final Path rootDir;

    private boolean recursive = DEFAULT_RECURSIVE;

    private Pattern includePattern = Pattern.compile(DEFAULT_INCLUDE_PATTERN);

    private Set<Path> excludedFiles = Collections.emptySet();

    /**
     * Creates a new SchemaFinder with the given directory as root of the search.
     *
     * @param rootDir the starting directory of the search. Must not be null.
     */
    public SchemaFinder(Path rootDir) {
        if (rootDir == null) {
            throw new IllegalArgumentException("rootDir is required for schema search");
        }
        this.rootDir = rootDir;
    }

    /**
     * Sets whether this finder should recursively search in nested directories.
     *
     * @param recursive whether the file search should be recursive
     */
    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    /**
     * Sets the pattern that filenames should match to be included in the result. Matching files are only included if
     * they're not part of the excluded files.
     *
     * @param includePattern a regular expression pattern as defined by the {@link Pattern} class
     * @see #setExcludedFiles(Set)
     */
    public void setIncludePattern(String includePattern) {
        this.includePattern = Pattern.compile(includePattern);
    }

    /**
     * Sets a set of paths to exclude from the search even if they match the include pattern. The provided paths are
     * either absolute or relative to the root directory provided in the constructor.
     *
     * @param excludedFiles a set of exact file paths to exclude from the search
     * @see #setIncludePattern(String)
     */
    public void setExcludedFiles(Set<String> excludedFiles) {
        this.excludedFiles = excludedFiles.stream().map(rootDir::resolve).collect(Collectors.toSet());
    }

    /**
     * Walks the directory tree starting at the root provided in the constructor to find GraphQL schemas.
     *
     * @return an alphabetically-sorted list of file paths matching the current configuration
     * @throws IOException if any I/O error occurs while reading the file system
     */
    public List<String> findSchemas() throws IOException {
        int maxDepth = recursive ? Integer.MAX_VALUE : 1;
        try (Stream<Path> paths = Files.find(rootDir, maxDepth, (path, attrs) -> shouldInclude(path))) {
            return paths.map(Path::toString).sorted().collect(Collectors.toList());
        }
    }

    private boolean shouldInclude(Path path) {
        if (Files.isDirectory(path)) {
            return false;
        }
        if (excludedFiles.contains(path)) {
            return false;
        }
        String filename = path.getFileName().toString();
        return includePattern.matcher(filename).matches();
    }
}
