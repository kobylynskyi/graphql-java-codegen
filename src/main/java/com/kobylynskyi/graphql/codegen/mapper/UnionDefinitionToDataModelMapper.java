package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedUnionTypeDefinition;

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
     * @param mappingConfig Global mapping configuration
     * @param definition    Definition of union type including base definition and its extensions
     * @return Freemarker data model of the GraphQL union
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, ExtendedUnionTypeDefinition definition) {
        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingConfig));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, definition));
        dataModel.put(JAVA_DOC, definition.getJavaDoc());
        return dataModel;
    }

}
