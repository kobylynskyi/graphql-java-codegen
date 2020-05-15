package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.InputValueDefinition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

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
     * @param mappingContext     Global mapping context
     * @param extendedDefinition GraphQL extended definition
     * @return Class name of GraphQL node
     */
    static String getClassNameWithPrefixAndSuffix(MappingContext mappingContext,
                                                  ExtendedDefinition<?, ?> extendedDefinition) {
        return getClassNameWithPrefixAndSuffix(mappingContext, extendedDefinition.getName());
    }

    /**
     * Generates a class name including prefix and suffix (if any)
     *
     * @param mappingContext Global mapping context
     * @param definitionName GraphQL node name
     * @return Class name of GraphQL node
     */
    static String getClassNameWithPrefixAndSuffix(MappingContext mappingContext, String definitionName) {
        StringBuilder classNameBuilder = new StringBuilder();
        if (Utils.isNotBlank(mappingContext.getModelNamePrefix())) {
            classNameBuilder.append(mappingContext.getModelNamePrefix());
        }
        classNameBuilder.append(Utils.capitalize(definitionName));
        if (Utils.isNotBlank(mappingContext.getModelNameSuffix())) {
            classNameBuilder.append(mappingContext.getModelNameSuffix());
        }
        return classNameBuilder.toString();
    }

    /**
     * Get java package name for api class.
     *
     * @param mappingContext Global mapping context
     * @return api package name if present. Generic package name otherwise
     */
    static String getApiPackageName(MappingContext mappingContext) {
        if (Utils.isNotBlank(mappingContext.getApiPackageName())) {
            return mappingContext.getApiPackageName();
        } else {
            return mappingContext.getPackageName();
        }
    }

    /**
     * Get java package name for model class.
     *
     * @param mappingContext Global mapping context
     * @return model package name if present. Generic package name otherwise
     */
    static String getModelPackageName(MappingContext mappingContext) {
        if (Utils.isNotBlank(mappingContext.getModelPackageName())) {
            return mappingContext.getModelPackageName();
        } else {
            return mappingContext.getPackageName();
        }
    }

    /**
     * Returns imports required for a generated class:
     * - model package name
     * - generic package name
     *
     * @param mappingContext Global mapping context
     * @param packageName    Package name of the generated class which will be ignored
     * @return all imports required for a generated class
     */
    static Set<String> getImports(MappingContext mappingContext, String packageName) {
        Set<String> imports = new HashSet<>();
        String modelPackageName = mappingContext.getModelPackageName();
        if (Utils.isNotBlank(modelPackageName) && !modelPackageName.equals(packageName)) {
            imports.add(modelPackageName);
        }
        String genericPackageName = mappingContext.getPackageName();
        if (Utils.isNotBlank(genericPackageName) && !genericPackageName.equals(packageName)) {
            imports.add(genericPackageName);
        }
        // not adding apiPackageName because it should not be imported in any other generated classes
        return imports;
    }

    /**
     * Determines if the methods of the given type should use async return types.
     *
     * @param mappingContext Global mapping context
     * @param typeName       Name of the type (Query, Mutation, Subscription or any POJO type in case of a resolver)
     * @return true if the methods of the given type should be generated with async return types, false otherwise
     */
    static boolean shouldUseAsyncMethods(MappingContext mappingContext, String typeName) {
        boolean isAsyncApi = mappingContext.getGenerateAsyncApi() != null && mappingContext.getGenerateAsyncApi();

        return isAsyncApi && !GraphQLOperation.SUBSCRIPTION.name().equalsIgnoreCase(typeName);
    }

    /**
     * Builds a className suffix based on the input values.
     * Examples:
     * 1. fieldDefinition has some input values:
     * "ids" => "ByIds"
     * "category", "status" => "ByCategoryAndStatus"
     *
     * @param fieldDefinition field definition that has some InputValueDefinitions
     * @return className suffix based on the input values
     */
    static String getClassNameSuffixWithInputValues(ExtendedFieldDefinition fieldDefinition) {
        StringJoiner inputValueNamesJoiner = new StringJoiner("And");
        fieldDefinition.getInputValueDefinitions().stream()
                .map(InputValueDefinition::getName).map(Utils::capitalize)
                .forEach(inputValueNamesJoiner::add);
        String inputValueNames = inputValueNamesJoiner.toString();
        if (inputValueNames.isEmpty()) {
            return inputValueNames;
        }
        return "By" + inputValueNames;
    }
}
