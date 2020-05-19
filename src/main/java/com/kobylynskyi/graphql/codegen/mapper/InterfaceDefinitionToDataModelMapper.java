package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInterfaceTypeDefinition;

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
     * @param mappingContext Global mapping context
     * @param definition     Definition of interface type including base definition and its extensions
     * @return Freemarker data model of the GraphQL interface
     */
    public static Map<String, Object> map(MappingContext mappingContext, ExtendedInterfaceTypeDefinition definition) {
        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingContext, definition));
        dataModel.put(JAVA_DOC, definition.getJavaDoc());
        dataModel.put(FIELDS, FieldDefinitionToParameterMapper.mapFields(
                mappingContext, definition.getFieldDefinitions(), definition.getName()));
        return dataModel;
    }

}
