package com.kobylynskyi.graphql.codegen.mapper;

import static graphql.language.OperationDefinition.*;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;
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

    /**
     * Map GraphQL's FieldDefinition to a Freemarker-understandable format of parameter
     *
     * @param mappingConfig  Global mapping configuration
     * @param fieldDef       GraphQL field definition
     * @param parentTypeName Name of the parent type
     * @return Freemarker-understandable format of parameter (field)
     */
    public static ParameterDefinition map(MappingConfig mappingConfig, FieldDefinition fieldDef, String parentTypeName) {
        ParameterDefinition parameter = new ParameterDefinition();
        parameter.setName(MapperUtils.capitalizeIfRestricted(fieldDef.getName()));
        parameter.setType(getJavaType(mappingConfig, fieldDef.getType(), fieldDef.getName(), parentTypeName));
        parameter.setAnnotations(getAnnotations(mappingConfig, fieldDef.getType(), fieldDef.getName(), parentTypeName, false));
        return parameter;
    }

    /**
     * Map GraphQL's InputValueDefinition to a Freemarker-understandable format of operation
     *
     * @param mappingConfig        Global mapping configuration
     * @param inputValueDefinition GraphQL input value definition
     * @param parentTypeName       Name of the parent type
     * @return Freemarker-understandable format of parameter (field)
     */
    public static ParameterDefinition map(MappingConfig mappingConfig, InputValueDefinition inputValueDefinition, String parentTypeName) {
        ParameterDefinition parameter = new ParameterDefinition();
        parameter.setName(MapperUtils.capitalizeIfRestricted(inputValueDefinition.getName()));
        parameter.setType(getJavaType(mappingConfig, inputValueDefinition.getType()));
        parameter.setAnnotations(getAnnotations(mappingConfig, inputValueDefinition.getType(), inputValueDefinition.getName(), parentTypeName));
        return parameter;
    }

    /**
     * Convert GraphQL type to a corresponding Java type
     *
     * @param mappingConfig Global mapping configuration
     * @param type          GraphQL type
     * @return Corresponding Java type
     */
    static String getJavaType(MappingConfig mappingConfig, Type type) {
        return getJavaType(mappingConfig, type, null, null);
    }

    /**
     * Convert GraphQL type to a corresponding Java type
     *
     * @param mappingConfig  Global mapping configuration
     * @param graphqlType    GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @return Corresponding Java type
     */
    static String getJavaType(MappingConfig mappingConfig, Type graphqlType, String name, String parentTypeName) {
        if (graphqlType instanceof TypeName) {
            return getJavaType(mappingConfig, ((TypeName) graphqlType).getName(), name, parentTypeName);
        } else if (graphqlType instanceof ListType) {
            String mappedCollectionType = getJavaType(mappingConfig, ((ListType) graphqlType).getType(), name, parentTypeName);
            return wrapIntoJavaCollection(mappedCollectionType);
        } else if (graphqlType instanceof NonNullType) {
            return getJavaType(mappingConfig, ((NonNullType) graphqlType).getType(), name, parentTypeName);
        }
        return null;
    }

    /**
     * Convert GraphQL type to a corresponding Java type
     *
     * @param mappingConfig  Global mapping configuration
     * @param graphlType     GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @return Corresponding Java type
     */
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

    /**
     * Get annotations for a given GraphQL type
     *
     * @param mappingConfig  Global mapping configuration
     * @param graphlType     GraphQL type
     * @param name           GraphQL type name
     * @param parentTypeName Name of the parent type
     * @return list of Java annotations for a given GraphQL type
     */
    static List<String> getAnnotations(MappingConfig mappingConfig, Type graphlType, String name, String parentTypeName) {
        return getAnnotations(mappingConfig, graphlType, name, parentTypeName, false);
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
            if (!Utils.isBlank(modelValidationAnnotation)) {
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

    /**
     * Wrap java type into collection. E.g.: "String" becomes "Collection<String"
     *
     * @param type Anything that will be wrapped into Collection<>
     * @return String wrapped into Collection<>
     */
    private static String wrapIntoJavaCollection(String type) {
        return String.format("Collection<%s>", type);
    }

    /**
     * Wraps type into subscriptionReturnType (defined in the mapping configuration.
     * Example:
     * Given GraphQL schema:                           type Subscription { eventsCreated: [Event!]! }
     * Given subscriptionReturnType in mapping config: org.reactivestreams.Publisher
     * Return: org.reactivestreams.Publisher<Event>
     *
     * @param mappingConfig  Global mapping configuration
     * @param javaTypeName   The type that will be wrapped into
     * @param parentTypeName Name of the parent type
     * @return Java type wrapped into the subscriptionReturnType
     */
    static String wrapIntoSubscriptionIfRequired(MappingConfig mappingConfig, String javaTypeName, String parentTypeName) {
        if (parentTypeName.equalsIgnoreCase(Operation.SUBSCRIPTION.name())
                && !Utils.isBlank(mappingConfig.getSubscriptionReturnType())) {
            return String.format("%s<%s>", mappingConfig.getSubscriptionReturnType(), javaTypeName);
        }
        return javaTypeName;
    }

}
