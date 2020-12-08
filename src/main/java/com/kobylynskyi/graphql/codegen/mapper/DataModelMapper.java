package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.ApiRootInterfaceStrategy;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDocument;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedImplementingTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInterfaceTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.InputValueDefinition;
import graphql.language.SourceLocation;
import graphql.language.TypeName;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface DataModelMapper {

    /**
     * Generates a model class name including prefix and suffix (if any)
     *
     * @param mappingContext Global mapping context
     * @param definitionName GraphQL node name
     * @return Class name of GraphQL model node
     */
    static String getModelClassNameWithPrefixAndSuffix(MappingContext mappingContext, String definitionName) {
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
     * Generates a type resolver class name including prefix and suffix (if any)
     *
     * @param mappingContext Global mapping context
     * @param typeName       GraphQL type name
     * @return Class name of GraphQL type resolver
     */
    static String getTypeResolverClassNameWithPrefixAndSuffix(MappingContext mappingContext, String typeName) {
        StringBuilder classNameBuilder = new StringBuilder();
        if (Utils.isNotBlank(mappingContext.getTypeResolverPrefix())) {
            classNameBuilder.append(mappingContext.getTypeResolverPrefix());
        }
        classNameBuilder.append(Utils.capitalize(typeName));
        if (Utils.isNotBlank(mappingContext.getTypeResolverSuffix())) {
            classNameBuilder.append(mappingContext.getTypeResolverSuffix());
        }
        return classNameBuilder.toString();
    }

    /**
     * Generates an api class name including prefix and suffix (if any)
     * Examples: CreateEventMutationResolver, EventsQueryResolver, EventsByIdsQueryResolver (rootTypeName is "Query" or the likes)
     *
     * @param mappingContext  Global mapping context
     * @param fieldDefinition GraphQL field definition
     * @param rootTypeName    Object type (e.g.: "Query", "Mutation" or "Subscription")
     * @param fieldNames      Names of all fields inside the rootType. Used to detect duplicate
     * @return Class name of GraphQL api node
     */
    static String getApiClassNameWithPrefixAndSuffix(MappingContext mappingContext,
                                                     ExtendedFieldDefinition fieldDefinition,
                                                     String rootTypeName,
                                                     List<String> fieldNames) {
        StringBuilder classNameBuilder = new StringBuilder();
        classNameBuilder.append(getApiPrefix(mappingContext, fieldDefinition.getSourceLocation()));
        classNameBuilder.append(Utils.capitalize(fieldDefinition.getName()));
        if (Collections.frequency(fieldNames, fieldDefinition.getName()) > 1) {
            // Examples: EventsByIdsQuery, EventsByCategoryAndStatusQuery
            classNameBuilder.append(DataModelMapper.getClassNameSuffixWithInputValues(fieldDefinition));
        }
        if (Utils.isNotBlank(rootTypeName)) {
            classNameBuilder.append(rootTypeName);
        }
        if (Utils.isNotBlank(mappingContext.getApiNameSuffix())) {
            classNameBuilder.append(mappingContext.getApiNameSuffix());
        }
        return classNameBuilder.toString();
    }

    /**
     * Generates an api class name including prefix and suffix (if any)
     * Examples: MutationResolver, QueryResolver, etc
     *
     * @param mappingContext Global mapping context
     * @param definition     GraphQL object definition of a root type like Query
     * @return Class name of GraphQL api node
     */
    static String getApiClassNameWithPrefixAndSuffix(MappingContext mappingContext,
                                                     ExtendedObjectTypeDefinition definition) {
        StringBuilder classNameBuilder = new StringBuilder();
        if (mappingContext.getApiRootInterfaceStrategy() == ApiRootInterfaceStrategy.SINGLE_INTERFACE) {
            // we don't need to consider apiNamePrefixStrategy in case we are generating a single root interface
            if (Utils.isNotBlank(mappingContext.getApiNamePrefix())) {
                classNameBuilder.append(mappingContext.getApiNamePrefix());
            }
        } else {
            classNameBuilder.append(getApiPrefix(mappingContext, definition.getSourceLocation()));
        }
        classNameBuilder.append(Utils.capitalize(definition.getName()));
        if (Utils.isNotBlank(mappingContext.getApiNameSuffix())) {
            classNameBuilder.append(mappingContext.getApiNameSuffix());
        }
        return classNameBuilder.toString();
    }

    /**
     * Get the prefix for api class name based on the defined strategy and GraphQL node source location.
     *
     * @param mappingContext Global mapping context
     * @param sourceLocation GraphQL node SourceLocation
     * @return prefix for the api class
     */
    static String getApiPrefix(MappingContext mappingContext,
                               SourceLocation sourceLocation) {
        switch (mappingContext.getApiNamePrefixStrategy()) {
            case FILE_NAME_AS_PREFIX:
                return getPrefixFromSourceLocation(sourceLocation, File::getName);
            case FOLDER_NAME_AS_PREFIX:
                return getPrefixFromSourceLocation(sourceLocation, getParentFileNameFunction());
            case CONSTANT:
            default:
                if (Utils.isNotBlank(mappingContext.getApiNamePrefix())) {
                    return mappingContext.getApiNamePrefix();
                }
        }
        return "";
    }

    /**
     * Get the prefix (used as a prefix for class names) from GraphQL source location (file) using the supplied
     * function <b>fileStringFunction</b>.
     * Examples:
     * ("src/test/resources/order-service/schema.graphql", File::getParent) becomes "OrderService"
     * ("src/test/resources/product-service/ProductApis.graphql", File::getName) becomes "ProductApis"
     *
     * @param sourceLocation     source location of GraphQL definition
     * @param fileStringFunction function to fetch File's attribute (name, parent, etc)
     * @return prefix of the file.
     */
    static String getPrefixFromSourceLocation(SourceLocation sourceLocation,
                                              Function<File, String> fileStringFunction) {
        if (sourceLocation == null || sourceLocation.getSourceName() == null) {
            return "";
        }
        String fileName = fileStringFunction.apply(new File(sourceLocation.getSourceName()));
        // remove prefix
        fileName = fileName.replaceFirst("[.][^.]+$", "");
        // capitalize
        fileName = Utils.capitalizeString(fileName);
        // leave only alphanumeric
        fileName = fileName.replaceAll("[^A-Za-z0-9]", "");
        return fileName;
    }

    /**
     * Generates a class name for ParametrizedInput
     *
     * @param mappingContext       Global mapping context
     * @param fieldDefinition      GraphQL field definition for a field that has parametrized input
     * @param parentTypeDefinition GraphQL definition which is a parent for fieldDefinition
     * @return Class name of parametrized input
     */
    static String getParametrizedInputClassName(MappingContext mappingContext,
                                                ExtendedFieldDefinition fieldDefinition,
                                                ExtendedDefinition<?, ?> parentTypeDefinition) {
        return Utils.capitalize(parentTypeDefinition.getName()) +
                Utils.capitalize(fieldDefinition.getName()) +
                mappingContext.getParametrizedInputSuffix();
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
     * Builds a className suffix based on the input values.
     * Examples:
     * 1. fieldDefinition has some input values:
     * "ids" becomes "ByIds"
     * "category", "status" becomes "ByCategoryAndStatus"
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

    /**
     * Scan document and return all interfaces that given type implements.
     *
     * @param definition GraphQL definition that might implement some interfaces
     * @param document   GraphQL document
     * @return all interfaces that given type implements.
     */
    static List<ExtendedInterfaceTypeDefinition> getInterfacesOfType(ExtendedImplementingTypeDefinition<?, ?> definition,
                                                                     ExtendedDocument document) {
        if (definition.getImplements().isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> typeImplements = definition.getImplements()
                .stream()
                .filter(type -> TypeName.class.isAssignableFrom(type.getClass()))
                .map(TypeName.class::cast)
                .map(TypeName::getName)
                .collect(Collectors.toSet());
        return document.getInterfaceDefinitions()
                .stream()
                .filter(def -> typeImplements.contains(def.getName()))
                .collect(Collectors.toList());
    }

    static Function<File, String> getParentFileNameFunction() {
        return file -> file != null && file.getParentFile() != null ? file.getParentFile().getName() : null;
    }

    /**
     * Capitalize field name if it is language-restricted.
     * Examples:
     * * 'class' becomes 'Class'
     * * 'int' becomes 'Int'
     *
     * @param fieldName      any string
     * @param mappingContext Global mapping context
     * @return capitalized value if it is restricted in java/scala/kotlin/etc, same value as parameter otherwise
     */
    String capitalizeIfRestricted(MappingContext mappingContext, String fieldName);

    /**
     * Capitalize method name if it is language-restricted.
     * Examples:
     * * 'getClass' becomes 'GetClass'
     * * 'wait' becomes 'Wait'
     * * 'this' becomes 'This'
     *
     * @param methodName     any string
     * @param mappingContext Global mapping context
     * @return capitalized value if it is restricted in java/scala/kotlin/etc, same value as parameter otherwise
     */
    String capitalizeMethodNameIfRestricted(MappingContext mappingContext, String methodName);

    /**
     * Generates a model class name including prefix and suffix (if any)
     *
     * @param mappingContext     Global mapping context, record enum type
     * @param extendedDefinition GraphQL extended definition
     * @return Class name of GraphQL model node
     */
    default String getModelClassNameWithPrefixAndSuffix(MappingContext mappingContext,
                                                        ExtendedDefinition<?, ?> extendedDefinition) {
        return getModelClassNameWithPrefixAndSuffix(mappingContext, extendedDefinition.getName());
    }

}
