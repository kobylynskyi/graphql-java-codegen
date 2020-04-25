package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.FieldDefinition;

import java.util.HashMap;
import java.util.Map;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;

/**
 * Map field definition to a Request Freemarker data model
 *
 * @author kobylynskyi
 */
public class FieldDefinitionToRequestDataModelMapper {

    /**
     * Map field definition to a Request Freemarker data model.
     *
     * @param mappingConfig  Global mapping configuration
     * @param operationDef   GraphQL operation definition
     * @param objectTypeName Object type (e.g.: "Query", "Mutation" or "Subscription")
     * @return Freemarker data model of the GraphQL request
     */
    public static Map<String, Object> map(MappingConfig mappingConfig,
                                          ExtendedFieldDefinition operationDef,
                                          String objectTypeName) {
        Map<String, Object> dataModel = new HashMap<>();
        // Request classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingConfig));
        dataModel.put(CLASS_NAME, getClassName(operationDef.getName(), objectTypeName, mappingConfig.getRequestSuffix()));
        dataModel.put(JAVA_DOC, operationDef.getJavaDoc());
        dataModel.put(OPERATION_NAME, operationDef.getName());
        dataModel.put(OPERATION_TYPE, objectTypeName.toUpperCase());
        dataModel.put(FIELDS, InputValueDefinitionToParameterMapper.map(mappingConfig, operationDef.getInputValueDefinitions(), operationDef.getName()));
        dataModel.put(BUILDER, mappingConfig.getGenerateBuilder());
        dataModel.put(EQUALS_AND_HASH_CODE, mappingConfig.getGenerateEqualsAndHashCode());
        dataModel.put(TO_STRING, mappingConfig.getGenerateToString());
        dataModel.put(TO_STRING_ESCAPE_JSON, mappingConfig.getGenerateRequests());
        return dataModel;
    }

    /**
     * Examples:
     * - EventsByCategoryQueryRequest
     * - CreateEventMutationRequest
     */
    private static String getClassName(String operationDefName, String objectType, String requestSuffix) {
        if (Utils.isBlank(requestSuffix)) {
            return Utils.capitalize(operationDefName) + objectType;
        } else {
            return Utils.capitalize(operationDefName) + objectType + requestSuffix;
        }
    }
}
