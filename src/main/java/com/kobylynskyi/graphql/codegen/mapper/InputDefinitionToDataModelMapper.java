package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import graphql.language.Document;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputObjectTypeExtensionDefinition;
import graphql.language.InputValueDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;

/**
 * Map input type definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class InputDefinitionToDataModelMapper {

    /**
     * Map input type definition to a Freemarker data model
     *
     * @param mappingConfig  Global mapping configuration
     * @param typeDefinition GraphQL type definition
     * @param document       GraphQL document
     * @return Freemarker data model of the GraphQL type
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, InputObjectTypeDefinition typeDefinition,
                                          Document document) {
        String packageName = MapperUtils.getModelPackageName(mappingConfig);
        List<InputValueDefinition> inputValueDefinitions = getInputValueDefinitions(typeDefinition, document);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, typeDefinition));
        dataModel.put(NAME, typeDefinition.getName());
        dataModel.put(FIELDS, InputValueDefinitionToParameterMapper.map(mappingConfig, inputValueDefinitions, typeDefinition.getName()));
        dataModel.put(BUILDER, mappingConfig.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingConfig.getGenerateEqualsAndHashCode());
        dataModel.put(TO_STRING, mappingConfig.getGenerateToString());
        dataModel.put(TO_STRING_ESCAPE_JSON, mappingConfig.getGenerateRequests());
        return dataModel;
    }

    /**
     * Merge input value definitions from the definition and its extensions
     *
     * @param typeDefinition InputObjectTypeDefinition definition
     * @param document       GraphQL document. Used to fetch all extensions of the same definition
     * @return list of all input value definitions
     */
    private static List<InputValueDefinition> getInputValueDefinitions(InputObjectTypeDefinition typeDefinition,
                                                                       Document document) {
        List<InputValueDefinition> definitions = typeDefinition.getInputValueDefinitions();
        MapperUtils.getDefinitionsOfType(document, InputObjectTypeExtensionDefinition.class, typeDefinition.getName())
                .stream()
                .map(InputObjectTypeDefinition::getInputValueDefinitions)
                .forEach(definitions::addAll);
        return definitions;
    }

}
