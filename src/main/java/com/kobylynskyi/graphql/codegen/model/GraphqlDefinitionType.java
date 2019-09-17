package com.kobylynskyi.graphql.codegen.model;

public enum GraphqlDefinitionType {

    SCHEMA,
    OPERATION, // Query/Mutation/Subscription
    TYPE,
    INTERFACE,
    INPUT,
    UNION,
    ENUM,
    SCALAR;

}
