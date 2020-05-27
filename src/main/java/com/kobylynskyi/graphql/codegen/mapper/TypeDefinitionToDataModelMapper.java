package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.ProjectionParameterDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDocument;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
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
     * @param mappingContext Global mapping context
     * @param definition     Definition of object type including base definition and its extensions
     * @return Freemarker data model of the GraphQL type
     */
    public static Map<String, Object> map(MappingContext mappingContext,
                                          ExtendedObjectTypeDefinition definition) {
        ExtendedDocument document = mappingContext.getDocument();

        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingContext, definition));
        dataModel.put(JAVA_DOC, definition.getJavaDoc());
        dataModel.put(IMPLEMENTS, getInterfaces(mappingContext, definition));
        dataModel.put(FIELDS, getFields(mappingContext, definition, document));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingContext.getGenerateEqualsAndHashCode());
        dataModel.put(TO_STRING, mappingContext.getGenerateToString());
        dataModel.put(TO_STRING_FOR_REQUEST, mappingContext.getGenerateRequests());
        return dataModel;
    }

    /**
     * Map type definition to a Freemarker data model of Response Projection.
     *
     * @param mappingContext Global mapping context
     * @param typeDefinition GraphQL type definition
     * @return Freemarker data model of the GraphQL Response Projection
     */
    public static Map<String, Object> mapResponseProjection(MappingContext mappingContext,
                                                            ExtendedObjectTypeDefinition typeDefinition) {
        Map<String, Object> dataModel = new HashMap<>();
        // ResponseProjection classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, Utils.capitalize(typeDefinition.getName()) + mappingContext.getResponseProjectionSuffix());
        dataModel.put(JAVA_DOC, Collections.singletonList("Response projection for " + typeDefinition.getName()));
        dataModel.put(FIELDS, getProjectionFields(mappingContext, typeDefinition));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingContext.getGenerateEqualsAndHashCode());
        // dataModel.put(TO_STRING, mappingConfig.getGenerateToString()); always generated for serialization purposes
        return dataModel;
    }

    /**
     * Map field definition to a Freemarker data model of Parametrized Input.
     *
     * @param mappingContext       Global mapping context
     * @param fieldDefinition      GraphQL field definition
     * @param parentTypeDefinition GraphQL parent type definition
     * @return Freemarker data model of the GraphQL Parametrized Input
     */
    public static Map<String, Object> mapParametrizedInput(MappingContext mappingContext,
                                                           ExtendedFieldDefinition fieldDefinition,
                                                           ExtendedObjectTypeDefinition parentTypeDefinition) {
        Map<String, Object> dataModel = new HashMap<>();
        // ParametrizedInput classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, MapperUtils.getParametrizedInputClassName(mappingContext, fieldDefinition, parentTypeDefinition));
        dataModel.put(JAVA_DOC, Collections.singletonList(String.format("Parametrized input for field %s in type %s",
                fieldDefinition.getName(), parentTypeDefinition.getName())));
        dataModel.put(FIELDS, InputValueDefinitionToParameterMapper.map(
                mappingContext, fieldDefinition.getInputValueDefinitions(), parentTypeDefinition.getName()));
        dataModel.put(BUILDER, mappingContext.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingContext.getGenerateEqualsAndHashCode());
        // dataModel.put(TO_STRING, mappingConfig.getGenerateToString()); always generated for serialization purposes
        return dataModel;
    }

    /**
     * Get merged attributes from the type and attributes from the interface.
     *
     * @param mappingContext Global mapping context
     * @param typeDefinition GraphQL type definition
     * @param document       Parent GraphQL document
     * @return Freemarker data model of the GraphQL type
     */
    private static Collection<ParameterDefinition> getFields(MappingContext mappingContext,
                                                             ExtendedObjectTypeDefinition typeDefinition,
                                                             ExtendedDocument document) {
        // using the map to exclude duplicate fields from the type and interfaces
        Map<String, ParameterDefinition> allParameters = new LinkedHashMap<>();

        // includes parameters from the base definition and extensions
        FieldDefinitionToParameterMapper.mapFields(mappingContext, typeDefinition.getFieldDefinitions(), typeDefinition.getName())
                .forEach(p -> allParameters.put(p.getName(), p));
        // includes parameters from the interface
        getInterfacesOfType(typeDefinition, document).stream()
                .map(i -> FieldDefinitionToParameterMapper.mapFields(mappingContext, i.getFieldDefinitions(), i.getName()))
                .flatMap(Collection::stream)
                .forEach(paramDef -> allParameters.merge(paramDef.getName(), paramDef, TypeDefinitionToDataModelMapper::merge));
        return allParameters.values();
    }

    /**
     * Merge parameter definition data from the type and interface
     * Annotations from the type have higher precedence
     *
     * @param typeDef      Definition of the same parameter from the type
     * @param interfaceDef Definition of the same parameter from the interface
     * @return merged parameter definition
     */
    private static ParameterDefinition merge(ParameterDefinition typeDef, ParameterDefinition interfaceDef) {
        if (Utils.isEmpty(typeDef.getAnnotations())) {
            typeDef.setAnnotations(interfaceDef.getAnnotations());
        }
        if (Utils.isEmpty(typeDef.getJavaDoc())) {
            typeDef.setJavaDoc(interfaceDef.getJavaDoc());
        }
        return typeDef;
    }

    /**
     * Get merged attributes from the type and attributes from the interface.
     *
     * @param mappingContext Global mapping context
     * @param typeDefinition GraphQL type definition
     * @return Freemarker data model of the GraphQL type
     */
    private static Collection<ProjectionParameterDefinition> getProjectionFields(MappingContext mappingContext,
                                                                                 ExtendedObjectTypeDefinition typeDefinition) {
        // using the map to exclude duplicate fields from the type and interfaces
        Map<String, ProjectionParameterDefinition> allParameters = new LinkedHashMap<>();

        // includes parameters from the base definition and extensions
        FieldDefinitionToParameterMapper.mapProjectionFields(mappingContext, typeDefinition.getFieldDefinitions(), typeDefinition)
                .forEach(p -> allParameters.put(p.getName(), p));
        // includes parameters from the interface
        getInterfacesOfType(typeDefinition, mappingContext.getDocument()).stream()
                .map(i -> FieldDefinitionToParameterMapper.mapProjectionFields(mappingContext, i.getFieldDefinitions(), i))
                .flatMap(Collection::stream)
                .filter(paramDef -> !allParameters.containsKey(paramDef.getName()))
                .forEach(paramDef -> allParameters.put(paramDef.getName(), paramDef));
        return allParameters.values();
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

    private static Set<String> getInterfaces(MappingContext mappingContext,
                                             ExtendedObjectTypeDefinition definition) {
        List<String> unionsNames = mappingContext.getDocument().getUnionDefinitions()
                .stream()
                .filter(union -> union.isDefinitionPartOfUnion(definition))
                .map(ExtendedUnionTypeDefinition::getName)
                .map(unionName -> MapperUtils.getClassNameWithPrefixAndSuffix(mappingContext, unionName))
                .collect(Collectors.toList());
        Set<String> interfaceNames = definition.getImplements()
                .stream()
                .map(anImplement -> GraphqlTypeToJavaTypeMapper.getJavaType(mappingContext, anImplement))
                .collect(Collectors.toSet());

        Set<String> allInterfaces = new LinkedHashSet<>();
        allInterfaces.addAll(unionsNames);
        allInterfaces.addAll(interfaceNames);
        return allInterfaces;
    }

}
