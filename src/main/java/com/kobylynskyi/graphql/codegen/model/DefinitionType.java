package com.kobylynskyi.graphql.codegen.model;

public enum DefinitionType {

    SCHEMA,
    OPERATION, // Query/Mutation/Subscription
    TYPE,
    INTERFACE,
    INPUT,
    UNION,
    ENUM,
    SCALAR;

}
