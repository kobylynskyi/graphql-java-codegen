package com.kobylynskyi.graphql.codegen.model;

import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.*;
import lombok.NonNull;

public class DefinitionTypeDeterminer {

    public static GraphqlDefinitionType determine(@NonNull Definition definition) {
        if (definition instanceof ObjectTypeDefinition) {
            ObjectTypeDefinition typeDef = (ObjectTypeDefinition) definition;
            if (Utils.isGraphqlOperation(typeDef.getName())) {
                return GraphqlDefinitionType.OPERATION;
            } else {
                return GraphqlDefinitionType.TYPE;
            }
        } else if (definition instanceof EnumTypeDefinition) {
            return GraphqlDefinitionType.ENUM;
        } else if (definition instanceof InputObjectTypeDefinition) {
            return GraphqlDefinitionType.INPUT;
        } else if (definition instanceof SchemaDefinition) {
            return GraphqlDefinitionType.SCHEMA;
        } else if (definition instanceof UnionTypeDefinition) {
            return GraphqlDefinitionType.UNION;
        } else if (definition instanceof ScalarTypeDefinition) {
            return GraphqlDefinitionType.SCALAR;
        } else if (definition instanceof InterfaceTypeDefinition) {
            return GraphqlDefinitionType.INTERFACE;
        } else {
            throw new UnsupportedGraphqlDefinitionException(definition);
        }
    }

}
