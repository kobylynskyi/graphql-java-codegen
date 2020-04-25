package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedInputObjectTypeDefinition;

import java.util.HashMap;
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
     * @param mappingConfig Global mapping configuration
     * @param definition    Definition of input type including base definition and its extensions
     * @return Freemarker data model of the GraphQL type
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, ExtendedInputObjectTypeDefinition definition) {
        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingConfig));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, definition));
        dataModel.put(JAVA_DOC, definition.getJavaDoc());
        dataModel.put(NAME, definition.getName());
        dataModel.put(FIELDS, InputValueDefinitionToParameterMapper.map(mappingConfig, definition.getValueDefinitions(), definition.getName()));
        dataModel.put(BUILDER, mappingConfig.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingConfig.getGenerateEqualsAndHashCode());
        dataModel.put(TO_STRING, mappingConfig.getGenerateToString());
        dataModel.put(TO_STRING_ESCAPE_JSON, mappingConfig.getGenerateRequests());
        return dataModel;
    }

}
