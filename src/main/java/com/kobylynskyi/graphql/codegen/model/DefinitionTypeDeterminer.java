package com.kobylynskyi.graphql.codegen.model;

import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.*;

import javax.annotation.Nonnull;

public class DefinitionTypeDeterminer {

    public static DefinitionType determine(@Nonnull Definition definition) {
        if (definition instanceof ObjectTypeDefinition) {
            ObjectTypeDefinition typeDef = (ObjectTypeDefinition) definition;
            if (Utils.isGraphqlOperation(typeDef.getName())) {
                return DefinitionType.OPERATION;
            } else {
                return DefinitionType.TYPE;
            }
        } else if (definition instanceof EnumTypeDefinition) {
            return DefinitionType.ENUM;
        } else if (definition instanceof InputObjectTypeDefinition) {
            return DefinitionType.INPUT;
        } else if (definition instanceof SchemaDefinition) {
            return DefinitionType.SCHEMA;
        } else if (definition instanceof UnionTypeDefinition) {
            return DefinitionType.UNION;
        } else if (definition instanceof ScalarTypeDefinition) {
            return DefinitionType.SCALAR;
        } else if (definition instanceof InterfaceTypeDefinition) {
            return DefinitionType.INTERFACE;
        } else {
            throw new UnsupportedGraphqlDefinitionException(definition);
        }
    }

}
