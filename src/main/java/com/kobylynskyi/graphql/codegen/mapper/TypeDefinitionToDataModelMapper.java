package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.ProjectionParameterDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Document;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.ObjectTypeDefinition;

import java.util.*;
import java.util.stream.Collectors;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;
import static java.util.Collections.emptySet;

/**
 * Map type definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class TypeDefinitionToDataModelMapper {

    /**
     * Map type definition to a Freemarker data model
     *
     * @param mappingConfig  Global mapping configuration
     * @param typeDefinition GraphQL type definition
     * @param document       Parent GraphQL document
     * @return Freemarker data model of the GraphQL type
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, ObjectTypeDefinition typeDefinition,
                                          Document document) {
        Map<String, Object> dataModel = new HashMap<>();
        String packageName = MapperUtils.getModelPackageName(mappingConfig);
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, typeDefinition));
        Set<String> allInterfaces = new LinkedHashSet<>(
                MapperUtils.getUnionsHavingType(mappingConfig, typeDefinition, document));
        typeDefinition.getImplements().stream()
                .map(anImplement -> GraphqlTypeToJavaTypeMapper.getJavaType(mappingConfig, anImplement))
                .forEach(allInterfaces::add);
        dataModel.put(IMPLEMENTS, allInterfaces);
        dataModel.put(FIELDS, getFields(mappingConfig, typeDefinition, document));
        dataModel.put(BUILDER, mappingConfig.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingConfig.getGenerateEqualsAndHashCode());
        dataModel.put(TO_STRING, mappingConfig.getGenerateToString());
        return dataModel;
    }


    /**
     * Map type definition to a Freemarker data model of Response Projection.
     *
     * @param mappingConfig  Global mapping configuration
     * @param typeDefinition GraphQL type definition
     * @param document       Parent GraphQL document
     * @param typeNames      Names of all GraphQL types
     * @return Freemarker data model of the GraphQL Response Projection
     */
    public static Map<String, Object> mapResponseProjection(MappingConfig mappingConfig,
                                                            ObjectTypeDefinition typeDefinition, Document document,
                                                            Set<String> typeNames) {
        Map<String, Object> dataModel = new HashMap<>();
        String packageName = MapperUtils.getModelPackageName(mappingConfig);
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImportsForRequests(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, Utils.capitalize(typeDefinition.getName()) + mappingConfig.getResponseProjectionSuffix());
        dataModel.put(FIELDS, getProjectionFields(mappingConfig, typeDefinition, document, typeNames));
        dataModel.put(BUILDER, mappingConfig.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingConfig.getGenerateEqualsAndHashCode());
        // dataModel.put(TO_STRING, mappingConfig.getGenerateToString()); always generated for serialization purposes
        return dataModel;
    }

    /**
     * Get merged attributes from the type and attributes from the interface.
     *
     * @param mappingConfig  Global mapping configuration
     * @param typeDefinition GraphQL type definition
     * @param document       Parent GraphQL document
     * @return Freemarker data model of the GraphQL type
     */
    @SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
    private static Set<ParameterDefinition> getFields(MappingConfig mappingConfig, ObjectTypeDefinition typeDefinition,
                                                      Document document) {
        Set<ParameterDefinition> allParameters = new LinkedHashSet<>();
        allParameters.addAll(FieldDefinitionToParameterMapper.mapFields(mappingConfig,
                typeDefinition.getFieldDefinitions(), typeDefinition.getName()));
        List<InterfaceTypeDefinition> interfaces = getInterfacesOfType(mappingConfig, typeDefinition, document);
        interfaces.stream().map(i -> FieldDefinitionToParameterMapper.mapFields(mappingConfig,
                i.getFieldDefinitions(), i.getName()))
                .forEach(allParameters::addAll);
        return allParameters;
    }

    /**
     * Get merged attributes from the type and attributes from the interface.
     *
     * @param mappingConfig  Global mapping configuration
     * @param typeDefinition GraphQL type definition
     * @param document       Parent GraphQL document
     * @param typeNames      Names of all GraphQL types
     * @return Freemarker data model of the GraphQL type
     */
    @SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
    private static Set<ProjectionParameterDefinition> getProjectionFields(MappingConfig mappingConfig, ObjectTypeDefinition typeDefinition,
                                                                          Document document, Set<String> typeNames) {
        Set<ProjectionParameterDefinition> allParameters = new LinkedHashSet<>();
        allParameters.addAll(FieldDefinitionToParameterMapper.mapProjectionFields(mappingConfig,
                typeDefinition.getFieldDefinitions(), typeDefinition.getName(), typeNames));
        List<InterfaceTypeDefinition> interfaces = getInterfacesOfType(mappingConfig, typeDefinition, document);
        interfaces.stream().map(i -> FieldDefinitionToParameterMapper.mapProjectionFields(mappingConfig,
                i.getFieldDefinitions(), i.getName(), typeNames))
                .forEach(allParameters::addAll);
        return allParameters;
    }

    /**
     * Scan document and return all interfaces that given type implements.
     *
     * @param mappingConfig Global mapping configuration
     * @param definition    GraphQL type definition
     * @param document      Parent GraphQL document
     * @return all interfaces that given type implements.
     */
    private static List<InterfaceTypeDefinition> getInterfacesOfType(MappingConfig mappingConfig,
                                                                     ObjectTypeDefinition definition,
                                                                     Document document) {
        if (definition.getImplements().isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> typeImplements = definition.getImplements().stream()
                .map(type -> GraphqlTypeToJavaTypeMapper.getJavaType(mappingConfig, type))
                .collect(Collectors.toSet());
        return document.getDefinitions().stream()
                .filter(def -> def instanceof InterfaceTypeDefinition)
                .map(def -> (InterfaceTypeDefinition) def)
                .filter(def -> typeImplements.contains(def.getName()))
                .collect(Collectors.toList());
    }

}
