package com.kobylynskyi.graphql.codegen.model;

/**
 * Enum containing all supported langugages for code generation
 */
public enum GeneratedLanguage {

    JAVA(".java"),

    SCALA(".scala"),

    KOTLIN(".kt");

    private final String fileExtension;

    GeneratedLanguage(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileExtension() {
        return fileExtension;
    }

}
