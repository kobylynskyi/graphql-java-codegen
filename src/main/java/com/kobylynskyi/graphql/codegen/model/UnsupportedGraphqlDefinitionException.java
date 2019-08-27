package com.kobylynskyi.graphql.codegen.model;

import graphql.language.Definition;

/**
 * Exception that indicates unknown/unsupported GraphQL definition
 *
 * @author kobylynskyi
 */
public class UnsupportedGraphqlDefinitionException extends RuntimeException {

    public UnsupportedGraphqlDefinitionException(Definition unsupported) {
        super("Unsupported GraphQL definition type: " + unsupported.getClass());
    }

}
