package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import graphql.language.InterfaceTypeDefinition;

import java.util.HashMap;
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
     * @return Freemarker data model of the GraphQL interface
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, InterfaceTypeDefinition typeDef) {
        Map<String, Object> dataModel = new HashMap<>();
        String packageName = MapperUtils.getModelPackageName(mappingConfig);
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, typeDef));
        dataModel.put(FIELDS, FieldDefinitionToParameterMapper.map(mappingConfig, typeDef.getFieldDefinitions(), typeDef.getName()));
        return dataModel;
    }

}
