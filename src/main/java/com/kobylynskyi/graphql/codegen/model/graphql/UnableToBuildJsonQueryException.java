package com.kobylynskyi.graphql.codegen.model.graphql;

/**
 * Exception is thrown when it is unable to serialize GraphQL request to a string
 */
public class UnableToBuildJsonQueryException extends IllegalArgumentException {

    public UnableToBuildJsonQueryException(Exception e) {
        super(e);
    }

}
