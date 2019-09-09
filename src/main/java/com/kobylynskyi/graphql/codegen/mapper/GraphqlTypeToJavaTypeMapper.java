package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.Type;
import graphql.language.TypeName;

import java.util.Map;

/**
 * Map GraphQL type to Java type
 *
 * @author kobylynskyi
 */
class GraphqlTypeToJavaTypeMapper {

    static String mapToJavaType(MappingConfig mappingConfig, Type type) {
        return mapToJavaType(mappingConfig, type, null, null);
    }

    static String mapToJavaType(MappingConfig mappingConfig, Type type, String name, String parentTypeName) {
        if (type instanceof TypeName) {
            return mapToJavaType(mappingConfig, ((TypeName) type).getName(), name, parentTypeName);
        } else if (type instanceof ListType) {
            String mappedCollectionType = mapToJavaType(mappingConfig, ((ListType) type).getType(), name, parentTypeName);
            return wrapIntoJavaCollection(mappedCollectionType);
        } else if (type instanceof NonNullType) {
            return mapToJavaType(mappingConfig, ((NonNullType) type).getType(), name, parentTypeName);
        }
        return null;
    }

    private static String mapToJavaType(MappingConfig mappingConfig, String graphlType, String name, String parentTypeName) {
        Map<String, String> customTypesMapping = mappingConfig.getCustomTypesMapping();
        if (name != null && parentTypeName != null && customTypesMapping.containsKey(parentTypeName + "." + name)) {
            return customTypesMapping.get(parentTypeName + "." + name);
        } else if (customTypesMapping.containsKey(graphlType)) {
            return customTypesMapping.get(graphlType);
        }
        switch (graphlType) {
            case "ID":
                return "String";
            case "Int":
                return "Integer";
            case "String":
            case "Float":
            case "Boolean":
                return graphlType;
            default:
                // We need to refer other custom types/interfaces/unions with prefix and suffix
                return MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, graphlType);
        }
    }

    private static String wrapIntoJavaCollection(String type) {
        return String.format("Collection<%s>", type);
    }

}
