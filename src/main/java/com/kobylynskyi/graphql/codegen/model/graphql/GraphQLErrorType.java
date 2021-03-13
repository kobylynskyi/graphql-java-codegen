package com.kobylynskyi.graphql.codegen.model.graphql;

/**
 * Possible types of GraphQL errors
 * Copied from graphql-java library
 */
@SuppressWarnings({"java:S115"})
public enum GraphQLErrorType {
    InvalidSyntax,
    ValidationError,
    DataFetchingException,
    OperationNotSupported,
    ExecutionAborted
}
