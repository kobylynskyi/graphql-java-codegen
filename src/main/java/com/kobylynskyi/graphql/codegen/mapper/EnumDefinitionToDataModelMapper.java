package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import graphql.language.Document;
import graphql.language.EnumTypeDefinition;
import graphql.language.EnumTypeExtensionDefinition;
import graphql.language.EnumValueDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;

/**
 * Map enum definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class EnumDefinitionToDataModelMapper {

    /**
     * Map field definition to a Freemarker data model
     *
     * @param mappingConfig  Global mapping configuration
     * @param enumDefinition GraphQL enum definition
     * @param document       GraphQL document
     * @return Freemarker data model of the GraphQL enum
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, EnumTypeDefinition enumDefinition,
                                          Document document) {
        String packageName = MapperUtils.getModelPackageName(mappingConfig);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, enumDefinition));
        dataModel.put(FIELDS, map(getEnumValueDefinitions(enumDefinition, document)));
        return dataModel;
    }

    /**
     * Mapper from GraphQL's EnumValueDefinition to a Freemarker-understandable format
     *
     * @param enumValueDefinitions list of GraphQL EnumValueDefinition types
     * @return list of strings
     */
    private static List<String> map(List<EnumValueDefinition> enumValueDefinitions) {
        return enumValueDefinitions.stream()
                .map(EnumValueDefinition::getName)
                .map(MapperUtils::capitalizeIfRestricted)
                .collect(Collectors.toList());
    }

    /**
     * Merge enum value definitions from the definition and its extensions
     *
     * @param typeDefinition EnumTypeDefinition definition
     * @param document       GraphQL document. Used to fetch all extensions of the same definition
     * @return list of all enum value definitions
     */
    private static List<EnumValueDefinition> getEnumValueDefinitions(EnumTypeDefinition typeDefinition,
                                                                     Document document) {
        List<EnumValueDefinition> definitions = typeDefinition.getEnumValueDefinitions();
        MapperUtils.getDefinitionsOfType(document, EnumTypeExtensionDefinition.class, typeDefinition.getName())
                .stream()
                .map(EnumTypeExtensionDefinition::getEnumValueDefinitions)
                .forEach(definitions::addAll);
        return definitions;
    }

}
