package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import graphql.language.UnionTypeDefinition;

import java.util.HashMap;
import java.util.Map;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;

/**
 * Map union definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class UnionDefinitionToDataModelMapper {

    /**
     * Map union definition to a Freemarker data model
     *
     * @param mappingConfig  Global mapping configuration
     * @param typeDefinition GraphQL union definition
     * @return Freemarker data model of the GraphQL union
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, UnionTypeDefinition typeDefinition) {
        Map<String, Object> dataModel = new HashMap<>();
        String packageName = MapperUtils.getModelPackageName(mappingConfig);
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, typeDefinition));
        return dataModel;
    }

}
