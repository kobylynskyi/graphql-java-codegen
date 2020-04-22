package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInterfaceTypeDefinition;
import graphql.language.FieldDefinition;

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
     * @param definition    Definition of interface type including base definition and its extensions
     * @return Freemarker data model of the GraphQL interface
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, ExtendedInterfaceTypeDefinition definition) {
        String packageName = MapperUtils.getModelPackageName(mappingConfig);
        List<FieldDefinition> fieldDefinitions = definition.getFieldDefinitions();

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, definition));
        dataModel.put(FIELDS, FieldDefinitionToParameterMapper.mapFields(mappingConfig, fieldDefinitions, definition.getName()));
        return dataModel;
    }

}
