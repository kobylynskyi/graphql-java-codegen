package com.kobylynskyi.graphql.codegen.model.graphql;

public class UnableToBuildJsonQueryException extends IllegalArgumentException {

    public UnableToBuildJsonQueryException(Exception e) {
        super(e);
    }

}
