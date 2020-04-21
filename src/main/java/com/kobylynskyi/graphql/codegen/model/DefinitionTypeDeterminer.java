package com.kobylynskyi.graphql.codegen.model;

import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.*;
import lombok.NonNull;

public class DefinitionTypeDeterminer {

    /**
     * Determine the type of GraphQL definition.
     * Comparing using <code>.getClass()</code> in order to ignore subclasses (ExtensionDefinitions in our case)
     *
     * @param definition GraphQL definition
     * @return one of {@link GraphqlDefinitionType}
     */
    public static GraphqlDefinitionType determine(@NonNull Definition<?> definition) {
        Class<?> definitionClass = definition.getClass();
        if (definitionClass.equals(ObjectTypeDefinition.class)) {
            ObjectTypeDefinition typeDef = (ObjectTypeDefinition) definition;
            if (Utils.isGraphqlOperation(typeDef.getName())) {
                return GraphqlDefinitionType.OPERATION;
            } else {
                return GraphqlDefinitionType.TYPE;
            }
        } else if (definitionClass.equals(EnumTypeDefinition.class)) {
            return GraphqlDefinitionType.ENUM;
        } else if (definitionClass.equals(InputObjectTypeDefinition.class)) {
            return GraphqlDefinitionType.INPUT;
        } else if (definitionClass.equals(SchemaDefinition.class)) {
            return GraphqlDefinitionType.SCHEMA;
        } else if (definitionClass.equals(UnionTypeDefinition.class)) {
            return GraphqlDefinitionType.UNION;
        } else if (definitionClass.equals(ScalarTypeDefinition.class)) {
            return GraphqlDefinitionType.SCALAR;
        } else if (definitionClass.equals(InterfaceTypeDefinition.class)) {
            return GraphqlDefinitionType.INTERFACE;
        } else if (definitionClass.equals(DirectiveDefinition.class)) {
            return GraphqlDefinitionType.DIRECTIVE;
        } else {
            throw new UnsupportedGraphqlDefinitionException(definition);
        }
    }

}
