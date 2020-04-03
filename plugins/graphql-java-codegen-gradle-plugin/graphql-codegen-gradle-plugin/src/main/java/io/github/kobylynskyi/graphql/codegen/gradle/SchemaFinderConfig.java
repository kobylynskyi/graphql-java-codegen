package io.github.kobylynskyi.graphql.codegen.gradle;

import com.kobylynskyi.graphql.codegen.supplier.SchemaFinder;

import java.util.Collections;
import java.util.Set;

public class SchemaFinderConfig {

    private String rootDir;

    private boolean recursive = SchemaFinder.DEFAULT_RECURSIVE;

    private String includePattern = SchemaFinder.DEFAULT_INCLUDE_PATTERN;

    private Set<String> excludedFiles = Collections.emptySet();

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public String getIncludePattern() {
        return includePattern;
    }

    public void setIncludePattern(String includePattern) {
        this.includePattern = includePattern;
    }

    public Set<String> getExcludedFiles() {
        return excludedFiles;
    }

    public void setExcludedFiles(Set<String> excludedFiles) {
        this.excludedFiles = excludedFiles;
    }
}
