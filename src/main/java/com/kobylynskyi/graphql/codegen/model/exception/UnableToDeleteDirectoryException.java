package com.kobylynskyi.graphql.codegen.model.exception;

/**
 * Exception that indicates error while deleting directory
 *
 * @author kobylynskyi
 */
public class UnableToDeleteDirectoryException extends RuntimeException {

    public UnableToDeleteDirectoryException(Exception e) {
        super("Unable to delete directory", e);
    }

}
