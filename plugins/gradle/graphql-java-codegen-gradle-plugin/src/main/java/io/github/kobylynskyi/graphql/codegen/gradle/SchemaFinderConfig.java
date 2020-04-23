package io.github.kobylynskyi.graphql.codegen.gradle;

import com.kobylynskyi.graphql.codegen.supplier.SchemaFinder;

import java.util.Collections;
import java.util.Set;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;

public class SchemaFinderConfig {

    private String rootDir;

    private Boolean recursive = SchemaFinder.DEFAULT_RECURSIVE;

    private String includePattern = SchemaFinder.DEFAULT_INCLUDE_PATTERN;

    private Set<String> excludedFiles = Collections.emptySet();

    // This should not be replaced by @InputDirectory, because some files in this directory may not be schemas.
    // We only know the actual list of schema files to check after running the SchemaFinder, so @InputFiles is over there.
    @Input
    @Optional
    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    @Input
    @Optional
    public Boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(Boolean recursive) {
        this.recursive = recursive;
    }

    @Input
    @Optional
    public String getIncludePattern() {
        return includePattern;
    }

    public void setIncludePattern(String includePattern) {
        this.includePattern = includePattern;
    }

    @InputFiles
    @Optional
    public Set<String> getExcludedFiles() {
        return excludedFiles;
    }

    public void setExcludedFiles(Set<String> excludedFiles) {
        this.excludedFiles = excludedFiles;
    }
}
