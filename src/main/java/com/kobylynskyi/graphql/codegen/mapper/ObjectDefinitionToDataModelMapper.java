package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.ObjectTypeDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;

/**
 * Map object definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class ObjectDefinitionToDataModelMapper {

    /**
     * Map object definition to a Freemarker data model
     *
     * @param mappingConfig  Global mapping configuration
     * @param typeDefinition GraphQL object definition
     * @return Freemarker data model of the GraphQL object
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, ObjectTypeDefinition typeDefinition) {
        Map<String, Object> dataModel = new HashMap<>();
        String packageName = MapperUtils.getApiPackageName(mappingConfig);
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, Utils.capitalize(typeDefinition.getName()));
        List<Object> operations = typeDefinition.getFieldDefinitions().stream()
                .map(fieldDef ->
                        FieldDefinitionToDataModelMapper.mapFieldDefinition(mappingConfig, fieldDef, typeDefinition.getName()))
                .collect(Collectors.toList());
        dataModel.put(OPERATIONS, operations);
        return dataModel;
    }

}
