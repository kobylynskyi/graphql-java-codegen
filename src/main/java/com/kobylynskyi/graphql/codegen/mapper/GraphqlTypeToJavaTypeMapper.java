package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import graphql.language.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Map GraphQL type to Java type
 *
 * @author kobylynskyi
 */
class GraphqlTypeToJavaTypeMapper {

    public static ParameterDefinition map(MappingConfig mappingConfig, FieldDefinition fieldDef, String parentTypeName) {
        ParameterDefinition parameter = new ParameterDefinition();
        parameter.setName(MapperUtils.capitalizeIfRestricted(fieldDef.getName()));
        parameter.setType(getJavaType(mappingConfig, fieldDef.getType(), fieldDef.getName(), parentTypeName));
        parameter.setAnnotations(getAnnotations(mappingConfig, fieldDef.getType(), fieldDef.getName(), parentTypeName, false));
        return parameter;
    }

    public static ParameterDefinition map(MappingConfig mappingConfig, InputValueDefinition inputValueDefinition) {
        ParameterDefinition parameter = new ParameterDefinition();
        parameter.setName(MapperUtils.capitalizeIfRestricted(inputValueDefinition.getName()));
        parameter.setType(getJavaType(mappingConfig, inputValueDefinition.getType()));
        return parameter;
    }

    static String getJavaType(MappingConfig mappingConfig, Type type) {
        return getJavaType(mappingConfig, type, null, null);
    }

    static String getJavaType(MappingConfig mappingConfig, Type type, String name, String parentTypeName) {
        if (type instanceof TypeName) {
            return getJavaType(mappingConfig, ((TypeName) type).getName(), name, parentTypeName);
        } else if (type instanceof ListType) {
            String mappedCollectionType = getJavaType(mappingConfig, ((ListType) type).getType(), name, parentTypeName);
            return wrapIntoJavaCollection(mappedCollectionType);
        } else if (type instanceof NonNullType) {
            return getJavaType(mappingConfig, ((NonNullType) type).getType(), name, parentTypeName);
        }
        return null;
    }

    private static String getJavaType(MappingConfig mappingConfig, String graphlType, String name, String parentTypeName) {
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

    static List<String> getAnnotations(MappingConfig mappingConfig, Type type, String name, String parentTypeName) {
        return getAnnotations(mappingConfig, type, name, parentTypeName, true);
    }

    private static List<String> getAnnotations(MappingConfig mappingConfig, Type type, String name, String parentTypeName,
                                               boolean mandatory) {
        if (type instanceof TypeName) {
            return getAnnotations(mappingConfig, ((TypeName) type).getName(), name, parentTypeName, mandatory);
        } else if (type instanceof ListType) {
            return getAnnotations(mappingConfig, ((ListType) type).getType(), name, parentTypeName, mandatory);
        } else if (type instanceof NonNullType) {
            return getAnnotations(mappingConfig, ((NonNullType) type).getType(), name, parentTypeName, true);
        }
        return null;
    }

    private static List<String> getAnnotations(MappingConfig mappingConfig, String graphlType, String name, String parentTypeName, boolean mandatory) {
        List<String> annotations = new ArrayList<>();
        if (mandatory) {
            String modelValidationAnnotation = mappingConfig.getModelValidationAnnotation();
            if (modelValidationAnnotation != null && !modelValidationAnnotation.trim().isEmpty()) {
                annotations.add(modelValidationAnnotation);
            }
        }
        Map<String, String> customAnnotationsMapping = mappingConfig.getCustomAnnotationsMapping();
        if (name != null && parentTypeName != null && customAnnotationsMapping.containsKey(parentTypeName + "." + name)) {
            annotations.add(customAnnotationsMapping.get(parentTypeName + "." + name));
        } else if (customAnnotationsMapping.containsKey(graphlType)) {
            annotations.add(customAnnotationsMapping.get(graphlType));
        }
        return annotations;
    }

    private static String wrapIntoJavaCollection(String type) {
        return String.format("Collection<%s>", type);
    }

}
