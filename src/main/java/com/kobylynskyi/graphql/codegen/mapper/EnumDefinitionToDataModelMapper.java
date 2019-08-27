package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import graphql.language.EnumTypeDefinition;

import java.util.HashMap;
import java.util.Map;

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
     * @param mappingConfig Global mapping configuration
     * @param enumDef       GraphQL enum definition
     * @return Freemarker data model of the GraphQL enum
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, EnumTypeDefinition enumDef) {
        Map<String, Object> dataModel = new HashMap<>();
        String packageName = MapperUtils.getModelPackageName(mappingConfig);
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, enumDef));
        dataModel.put(FIELDS, EnumValueDefinitionToStringMapper.map(enumDef.getEnumValueDefinitions()));
        return dataModel;
    }

}
