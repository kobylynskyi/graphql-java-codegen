package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.NamedNode;
import graphql.language.UnionTypeDefinition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
     * Iterate through all unions across the document and find all that given <code>definition</code> is part of.
     *
     * @param mappingConfig Global mapping configuration
     * @param definition    GraphQL NamedNode definition
     * @param document      Parent GraphQL document
     * @return Names of all unions that requested <code>definition</code> is part of.
     */
    static List<String> getUnionNamesHavingType(MappingConfig mappingConfig,
                                                NamedNode<?> definition,
                                                Document document) {
        return document.getDefinitions().stream()
                // should cover extensions too
                .filter(def -> UnionTypeDefinition.class.isAssignableFrom(def.getClass()))
                .map(def -> (UnionTypeDefinition) def)
                .filter(union -> isDefinitionPartOfUnion(definition, union))
                .map(UnionTypeDefinition::getName)
                .map(unionName -> getClassNameWithPrefixAndSuffix(mappingConfig, unionName))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of definitions of the specific type and name.
     * It uses {@link java.lang.Class#isAssignableFrom(Class)} for the test
     *
     * @param definitionClass the definition class
     * @param definitionName  the definition name
     * @param <T>             the type of definition
     * @return a list of definitions of that class/name or empty list
     */
    static <T extends Definition<?>> List<T> getDefinitionsOfType(Document document,
                                                                  Class<T> definitionClass,
                                                                  String definitionName) {
        return document.getDefinitions().stream()
                .filter(d -> definitionClass.isAssignableFrom(d.getClass()) &&
                        NamedNode.class.isAssignableFrom(d.getClass()))
                .filter(d -> ((NamedNode<?>) d).getName().equals(definitionName))
                .map(definitionClass::cast)
                .collect(Collectors.toList());
    }

    /**
     * Find out if a definition is a part of a union.
     *
     * @param definition GraphQL definition (type / interface / object / union / etc.)
     * @param union      GraphQL Union definition
     * @return <b>true</b> if <code>definition</code> is a part of <code>union</code>. <b>false</b>if <code>definition</code> is a part of <code>union</code>.
     */
    private static boolean isDefinitionPartOfUnion(NamedNode<?> definition,
                                                   UnionTypeDefinition union) {
        return union.getMemberTypes().stream()
                .filter(member -> member instanceof NamedNode)
                .map(member -> (NamedNode<?>) member)
                .anyMatch(member -> member.getName().equals(definition.getName()));
    }

    /**
     * Generates a class name including prefix and suffix (if any)
     *
     * @param mappingConfig Global mapping configuration
     * @param definition    GraphQL node
     * @return Class name of GraphQL node
     */
    static String getClassNameWithPrefixAndSuffix(MappingConfig mappingConfig, NamedNode<?> definition) {
        return getClassNameWithPrefixAndSuffix(mappingConfig, definition.getName());
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
     * - api package name
     * - generic package name
     * - java.util
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
        String apiPackageName = mappingConfig.getApiPackageName();
        if (!Utils.isBlank(apiPackageName) && !apiPackageName.equals(packageName)) {
            imports.add(apiPackageName);
        }
        String genericPackageName = mappingConfig.getPackageName();
        if (!Utils.isBlank(genericPackageName) && !genericPackageName.equals(packageName)) {
            imports.add(genericPackageName);
        }
        imports.add("java.util");
        return imports;
    }

    /**
     * Returns imports required for the fields resolvers class
     *
     * @param mappingConfig Global mapping configuration
     * @param packageName   Package name of the generated class which will be ignored
     * @return all imports required for a generated class
     */
    static Set<String> getImportsForFieldResolvers(MappingConfig mappingConfig, String packageName, String objectTypeName) {
        Set<String> imports = getImports(mappingConfig, packageName);
        if (mappingConfig.getGenerateDataFetchingEnvironmentArgumentInApis()) {
            imports.add("graphql.schema");
        }
        if (shouldUseAsyncMethods(mappingConfig, objectTypeName)) {
            imports.add("java.util.concurrent");
        }
        return imports;
    }

    /**
     * Returns imports required for the request class.
     *
     * @param mappingConfig Global mapping configuration
     * @param packageName   Package name of the generated class which will be ignored
     * @return all imports required for a generated request class
     */
    static Set<String> getImportsForRequests(MappingConfig mappingConfig, String packageName) {
        Set<String> imports = getImports(mappingConfig, packageName);
        imports.add(GraphQLOperation.class.getPackage().getName());
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
