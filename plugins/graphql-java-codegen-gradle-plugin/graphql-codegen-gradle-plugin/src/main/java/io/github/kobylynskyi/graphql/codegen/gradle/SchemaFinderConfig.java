package io.github.kobylynskyi.graphql.codegen.gradle;

import com.kobylynskyi.graphql.codegen.supplier.SchemaFinder;

import java.util.Collections;
import java.util.Set;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;

public class SchemaFinderConfig {

    private String rootDir;

    private boolean recursive = SchemaFinder.DEFAULT_RECURSIVE;

    private String includePattern = SchemaFinder.DEFAULT_INCLUDE_PATTERN;

    private Set<String> excludedFiles = Collections.emptySet();

    @InputFile
    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    @Input
    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    @Input
    public String getIncludePattern() {
        return includePattern;
    }

    public void setIncludePattern(String includePattern) {
        this.includePattern = includePattern;
    }

    @InputFiles
    public Set<String> getExcludedFiles() {
        return excludedFiles;
    }

    public void setExcludedFiles(Set<String> excludedFiles) {
        this.excludedFiles = excludedFiles;
    }
}
