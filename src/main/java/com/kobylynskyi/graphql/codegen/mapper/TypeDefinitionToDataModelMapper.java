package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.Parameter;
import graphql.language.Document;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.ObjectTypeDefinition;

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
        Map<String, Object> dataModel = new HashMap<>();
        String packageName = MapperUtils.getModelPackageName(mappingConfig);
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, typeDefinition));
        Set<String> allInterfaces = new LinkedHashSet<>();
        allInterfaces.addAll(MapperUtils.getUnionsHavingType(mappingConfig, typeDefinition, document));
        typeDefinition.getImplements().stream()
                .map(anImplement -> GraphqlTypeToJavaTypeMapper.mapToJavaType(mappingConfig, anImplement))
                .forEach(allInterfaces::add);
        dataModel.put(IMPLEMENTS, allInterfaces);

        Set<Parameter> allParameters = new LinkedHashSet<>();
        // Merge attributes from the type and attributes from the interface
        allParameters.addAll(FieldDefinitionToParameterMapper.map(mappingConfig, typeDefinition.getFieldDefinitions()));
        List<InterfaceTypeDefinition> interfaces = getInterfacesOfType(mappingConfig, typeDefinition, document);
        interfaces.stream()
                .map(i -> FieldDefinitionToParameterMapper.map(mappingConfig, i.getFieldDefinitions()))
                .forEach(allParameters::addAll);
        dataModel.put(FIELDS, allParameters);

        return dataModel;
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
                .map(type -> GraphqlTypeToJavaTypeMapper.mapToJavaType(mappingConfig, type))
                .collect(Collectors.toSet());
        return document.getDefinitions().stream()
                .filter(def -> def instanceof InterfaceTypeDefinition)
                .map(def -> (InterfaceTypeDefinition) def)
                .filter(def -> typeImplements.contains(def.getName()))
                .collect(Collectors.toList());
    }

}
