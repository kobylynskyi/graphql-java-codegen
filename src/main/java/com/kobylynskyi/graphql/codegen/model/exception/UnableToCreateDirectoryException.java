package com.kobylynskyi.graphql.codegen.model.exception;

/**
 * Exception that indicates error while creating directory
 *
 * @author kobylynskyi
 */
public class UnableToCreateDirectoryException extends RuntimeException {

    public UnableToCreateDirectoryException(String directoryPath, Exception e) {
        super("Unable to create directory by path: " + directoryPath, e);
    }

}
