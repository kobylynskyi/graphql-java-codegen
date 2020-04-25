package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class MapperUtils {

    private static final Set<String> JAVA_RESTRICTED_KEYWORDS = new HashSet<>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double", "else", "extends", "false", "final", "finally", "float", "for", "goto", "if",
            "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package",
            "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while"));

    /**
     * Capitalize field name if it is Java-restricted.
     * Examples:
     * * class -> Class
     * * int -> Int
     *
     * @param fieldName any string
     * @return capitalized value if it is restricted in Java, same value as parameter otherwise
     */
    static String capitalizeIfRestricted(String fieldName) {
        if (JAVA_RESTRICTED_KEYWORDS.contains(fieldName)) {
            return Utils.capitalize(fieldName);
        }
        return fieldName;
    }

    /**
     * Generates a class name including prefix and suffix (if any)
     *
     * @param mappingConfig      Global mapping configuration
     * @param extendedDefinition GraphQL extended definition
     * @return Class name of GraphQL node
     */
    static String getClassNameWithPrefixAndSuffix(MappingConfig mappingConfig,
                                                  ExtendedDefinition<?, ?> extendedDefinition) {
        return getClassNameWithPrefixAndSuffix(mappingConfig, extendedDefinition.getName());
    }

    /**
     * Generates a class name including prefix and suffix (if any)
     *
     * @param mappingConfig  Global mapping configuration
     * @param definitionName GraphQL node name
     * @return Class name of GraphQL node
     */
    static String getClassNameWithPrefixAndSuffix(MappingConfig mappingConfig, String definitionName) {
        StringBuilder classNameBuilder = new StringBuilder();
        if (!Utils.isBlank(mappingConfig.getModelNamePrefix())) {
            classNameBuilder.append(mappingConfig.getModelNamePrefix());
        }
        classNameBuilder.append(Utils.capitalize(definitionName));
        if (!Utils.isBlank(mappingConfig.getModelNameSuffix())) {
            classNameBuilder.append(mappingConfig.getModelNameSuffix());
        }
        return classNameBuilder.toString();
    }

    /**
     * Get java package name for api class.
     *
     * @param mappingConfig Global mapping configuration
     * @return api package name if present. Generic package name otherwise
     */
    static String getApiPackageName(MappingConfig mappingConfig) {
        if (!Utils.isBlank(mappingConfig.getApiPackageName())) {
            return mappingConfig.getApiPackageName();
        } else {
            return mappingConfig.getPackageName();
        }
    }

    /**
     * Get java package name for model class.
     *
     * @param mappingConfig Global mapping configuration
     * @return model package name if present. Generic package name otherwise
     */
    static String getModelPackageName(MappingConfig mappingConfig) {
        if (!Utils.isBlank(mappingConfig.getModelPackageName())) {
            return mappingConfig.getModelPackageName();
        } else {
            return mappingConfig.getPackageName();
        }
    }

    /**
     * Returns imports required for a generated class:
     * - model package name
     * - generic package name
     *
     * @param mappingConfig Global mapping configuration
     * @param packageName   Package name of the generated class which will be ignored
     * @return all imports required for a generated class
     */
    static Set<String> getImports(MappingConfig mappingConfig, String packageName) {
        Set<String> imports = new HashSet<>();
        String modelPackageName = mappingConfig.getModelPackageName();
        if (!Utils.isBlank(modelPackageName) && !modelPackageName.equals(packageName)) {
            imports.add(modelPackageName);
        }
        String genericPackageName = mappingConfig.getPackageName();
        if (!Utils.isBlank(genericPackageName) && !genericPackageName.equals(packageName)) {
            imports.add(genericPackageName);
        }
        // not adding apiPackageName because it should not be imported in any other generated classes
        return imports;
    }

    /**
     * Determines if the methods of the given type should use async return types.
     *
     * @param mappingConfig Global mapping configuration
     * @param typeName      Name of the type (Query, Mutation, Subscription or any POJO type in case of a resolver)
     * @return true if the methods of the given type should be generated with async return types, false otherwise
     */
    static boolean shouldUseAsyncMethods(MappingConfig mappingConfig, String typeName) {
        boolean isAsyncApi = mappingConfig.getGenerateAsyncApi() != null && mappingConfig.getGenerateAsyncApi();

        return isAsyncApi && !GraphQLOperation.SUBSCRIPTION.name().equalsIgnoreCase(typeName);
    }
}
