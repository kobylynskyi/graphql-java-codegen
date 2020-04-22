package com.kobylynskyi.graphql.codegen.model;

/**
 * Exception that indicates error while creating directory
 *
 * @author kobylynskyi
 */
public class UnableToCreateDirectoryException extends RuntimeException {

    public UnableToCreateDirectoryException(String directoryPath) {
        super("Unable to create directory by path: " + directoryPath);
    }

}
