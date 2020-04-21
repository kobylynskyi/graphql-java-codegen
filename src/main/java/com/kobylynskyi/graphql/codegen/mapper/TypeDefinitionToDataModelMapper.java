package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.ProjectionParameterDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Document;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.ObjectTypeExtensionDefinition;

import java.util.*;
import java.util.stream.Collectors;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;

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
        String packageName = MapperUtils.getModelPackageName(mappingConfig);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, typeDefinition));
        dataModel.put(IMPLEMENTS, getInterfaces(mappingConfig, typeDefinition, document));
        dataModel.put(FIELDS, getFields(mappingConfig, typeDefinition, document));
        dataModel.put(BUILDER, mappingConfig.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingConfig.getGenerateEqualsAndHashCode());
        dataModel.put(TO_STRING, mappingConfig.getGenerateToString());
        dataModel.put(TO_STRING_ESCAPE_JSON, mappingConfig.getGenerateRequests());
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
    private static Set<ParameterDefinition> getFields(MappingConfig mappingConfig, ObjectTypeDefinition typeDefinition,
                                                      Document document) {
        List<ParameterDefinition> typeParameters = FieldDefinitionToParameterMapper.mapFields(mappingConfig,
                typeDefinition.getFieldDefinitions(), typeDefinition.getName());
        List<ParameterDefinition> typeParametersFromInterfaces = getInterfacesOfType(mappingConfig, typeDefinition, document)
                .stream()
                .map(i -> FieldDefinitionToParameterMapper.mapFields(mappingConfig, i.getFieldDefinitions(), i.getName()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<ParameterDefinition> typeParametersFromExtensions = MapperUtils.getDefinitionsOfType(document,
                ObjectTypeExtensionDefinition.class, typeDefinition.getName())
                .stream()
                .map(i -> FieldDefinitionToParameterMapper.mapFields(mappingConfig, i.getFieldDefinitions(), i.getName()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Set<ParameterDefinition> allParameters = new LinkedHashSet<>();
        allParameters.addAll(typeParameters);
        allParameters.addAll(typeParametersFromInterfaces);
        allParameters.addAll(typeParametersFromExtensions);
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
    private static Set<ProjectionParameterDefinition> getProjectionFields(MappingConfig mappingConfig, ObjectTypeDefinition typeDefinition,
                                                                          Document document, Set<String> typeNames) {
        List<ProjectionParameterDefinition> typeParameters = FieldDefinitionToParameterMapper.mapProjectionFields(mappingConfig,
                typeDefinition.getFieldDefinitions(), typeDefinition.getName(), typeNames);
        List<ProjectionParameterDefinition> typeParametersFromInterfaces = getInterfacesOfType(mappingConfig, typeDefinition, document)
                .stream().map(i -> FieldDefinitionToParameterMapper.mapProjectionFields(mappingConfig,
                        i.getFieldDefinitions(), i.getName(), typeNames))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<ProjectionParameterDefinition> typeParametersFromExtensions = MapperUtils.getDefinitionsOfType(document, ObjectTypeExtensionDefinition.class, typeDefinition.getName())
                .stream()
                .map(ext -> FieldDefinitionToParameterMapper.mapProjectionFields(mappingConfig,
                        ext.getFieldDefinitions(), ext.getName(), typeNames))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Set<ProjectionParameterDefinition> allParameters = new LinkedHashSet<>();
        allParameters.addAll(typeParameters);
        allParameters.addAll(typeParametersFromInterfaces);
        allParameters.addAll(typeParametersFromExtensions);
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

    private static Set<String> getInterfaces(MappingConfig mappingConfig, ObjectTypeDefinition typeDefinition, Document document) {
        Set<String> allInterfaces = new LinkedHashSet<>(
                MapperUtils.getUnionNamesHavingType(mappingConfig, typeDefinition, document));
        typeDefinition.getImplements().stream()
                .map(anImplement -> GraphqlTypeToJavaTypeMapper.getJavaType(mappingConfig, anImplement))
                .forEach(allInterfaces::add);
        return allInterfaces;
    }

}
