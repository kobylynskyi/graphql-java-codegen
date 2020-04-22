package com.kobylynskyi.graphql.codegen.model;

/**
 * Exception that indicates error while creating a file
 *
 * @author kobylynskyi
 */
public class UnableToCreateFileException extends RuntimeException {

    public UnableToCreateFileException(Exception e) {
        super("Unable to create file", e);
    }

}
