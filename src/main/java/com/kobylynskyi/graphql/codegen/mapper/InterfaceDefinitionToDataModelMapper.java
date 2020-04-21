package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import graphql.language.Document;
import graphql.language.FieldDefinition;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.InterfaceTypeExtensionDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;

/**
 * Map interface definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class InterfaceDefinitionToDataModelMapper {

    /**
     * Map interface definition to a Freemarker data model
     *
     * @param mappingConfig Global mapping configuration
     * @param typeDef       GraphQL interface definition
     * @param document      GraphQL document
     * @return Freemarker data model of the GraphQL interface
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, InterfaceTypeDefinition typeDef,
                                          Document document) {
        String packageName = MapperUtils.getModelPackageName(mappingConfig);
        List<FieldDefinition> fieldDefinitions = getFieldDefinitions(typeDef, document);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, typeDef));
        dataModel.put(FIELDS, FieldDefinitionToParameterMapper.mapFields(mappingConfig, fieldDefinitions, typeDef.getName()));
        return dataModel;
    }

    /**
     * Merge interface field definitions from the definition and its extensions
     *
     * @param typeDefinition InterfaceTypeDefinition definition
     * @param document       GraphQL document. Used to fetch all extensions of the same definition
     * @return list of all interface field definitions
     */
    private static List<FieldDefinition> getFieldDefinitions(InterfaceTypeDefinition typeDefinition,
                                                             Document document) {
        List<FieldDefinition> definitions = typeDefinition.getFieldDefinitions();
        MapperUtils.getDefinitionsOfType(document, InterfaceTypeExtensionDefinition.class, typeDefinition.getName()).stream()
                .map(InterfaceTypeDefinition::getFieldDefinitions)
                .forEach(definitions::addAll);
        return definitions;
    }

}
