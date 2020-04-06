package com.kobylynskyi.graphql.codegen.model.request;

import graphql.language.OperationDefinition;

import java.util.Map;

public interface GraphQLOperationRequest {

    OperationDefinition.Operation getOperationType();

    String getOperationName();

    Map<String, Object> getInput();

}
