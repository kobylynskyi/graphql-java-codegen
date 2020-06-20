package com.kobylynskyi.graphql.codegen.model.graphql;

@SuppressWarnings({"java:S115"}) // comes from graphql-java library
public enum GraphQLErrorType {
    InvalidSyntax,
    ValidationError,
    DataFetchingException,
    OperationNotSupported,
    ExecutionAborted
}
