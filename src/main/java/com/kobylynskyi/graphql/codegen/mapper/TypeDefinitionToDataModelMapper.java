package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.ProjectionParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDocument;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInterfaceTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedObjectTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedUnionTypeDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.TypeName;

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
     * @param mappingConfig Global mapping configuration
     * @param definition    Definition of object type including base definition and its extensions
     * @param document      GraphQL Document
     * @return Freemarker data model of the GraphQL type
     */
    public static Map<String, Object> map(MappingConfig mappingConfig,
                                          ExtendedObjectTypeDefinition definition,
                                          ExtendedDocument document) {
        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingConfig));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, definition));
        dataModel.put(JAVA_DOC, definition.getJavaDoc());
        dataModel.put(IMPLEMENTS, getInterfaces(mappingConfig, definition, document));
        dataModel.put(FIELDS, getFields(mappingConfig, definition, document));
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
                                                            ExtendedObjectTypeDefinition typeDefinition,
                                                            ExtendedDocument document,
                                                            Set<String> typeNames) {
        Map<String, Object> dataModel = new HashMap<>();
        // ResponseProjection classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingConfig));
        dataModel.put(CLASS_NAME, Utils.capitalize(typeDefinition.getName()) + mappingConfig.getResponseProjectionSuffix());
        dataModel.put(JAVA_DOC, Collections.singletonList("Response projection for " + typeDefinition.getName()));
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
    private static Set<ParameterDefinition> getFields(MappingConfig mappingConfig,
                                                      ExtendedObjectTypeDefinition typeDefinition,
                                                      ExtendedDocument document) {
        // this includes parameters from base definition and extensions
        List<ParameterDefinition> typeParameters = FieldDefinitionToParameterMapper.mapFields(mappingConfig,
                typeDefinition.getFieldDefinitions(), typeDefinition.getName());
        List<ParameterDefinition> typeParametersFromInterfaces = getInterfacesOfType(typeDefinition, document)
                .stream()
                .map(i -> FieldDefinitionToParameterMapper.mapFields(mappingConfig, i.getFieldDefinitions(), i.getName()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Set<ParameterDefinition> allParameters = new LinkedHashSet<>();
        allParameters.addAll(typeParameters);
        allParameters.addAll(typeParametersFromInterfaces);
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
    private static Set<ProjectionParameterDefinition> getProjectionFields(MappingConfig mappingConfig,
                                                                          ExtendedObjectTypeDefinition typeDefinition,
                                                                          ExtendedDocument document,
                                                                          Set<String> typeNames) {
        // this includes parameters from base definition and extensions
        List<ProjectionParameterDefinition> typeParameters = FieldDefinitionToParameterMapper.mapProjectionFields(
                mappingConfig, typeDefinition.getFieldDefinitions(), typeNames);
        List<ProjectionParameterDefinition> typeParametersFromInterfaces = getInterfacesOfType(typeDefinition, document)
                .stream()
                .map(i -> FieldDefinitionToParameterMapper.mapProjectionFields(mappingConfig, i.getFieldDefinitions(), typeNames))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Set<ProjectionParameterDefinition> allParameters = new LinkedHashSet<>();
        allParameters.addAll(typeParameters);
        allParameters.addAll(typeParametersFromInterfaces);
        return allParameters;
    }

    /**
     * Scan document and return all interfaces that given type implements.
     *
     * @param definition GraphQL type definition
     * @param document   GraphQL document
     * @return all interfaces that given type implements.
     */
    private static List<ExtendedInterfaceTypeDefinition> getInterfacesOfType(ExtendedObjectTypeDefinition definition,
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

    private static Set<String> getInterfaces(MappingConfig mappingConfig,
                                             ExtendedObjectTypeDefinition definition,
                                             ExtendedDocument document) {
        List<String> unionsNames = document.getUnionDefinitions()
                .stream()
                .filter(union -> union.isDefinitionPartOfUnion(definition))
                .map(ExtendedUnionTypeDefinition::getName)
                .map(unionName -> MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, unionName))
                .collect(Collectors.toList());
        Set<String> interfaceNames = definition.getImplements()
                .stream()
                .map(anImplement -> GraphqlTypeToJavaTypeMapper.getJavaType(mappingConfig, anImplement))
                .collect(Collectors.toSet());

        Set<String> allInterfaces = new LinkedHashSet<>();
        allInterfaces.addAll(unionsNames);
        allInterfaces.addAll(interfaceNames);
        return allInterfaces;
    }

}
