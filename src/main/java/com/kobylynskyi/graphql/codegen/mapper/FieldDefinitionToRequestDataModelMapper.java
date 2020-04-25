package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedFieldDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
     * @param fieldNames     Names of all fields inside the rootType. Used to detect duplicate
     * @return Freemarker data model of the GraphQL request
     */
    public static Map<String, Object> map(MappingConfig mappingConfig,
                                          ExtendedFieldDefinition operationDef,
                                          String objectTypeName,
                                          List<String> fieldNames) {
        Map<String, Object> dataModel = new HashMap<>();
        // Request classes are sharing the package with the model classes, so no imports are needed
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingConfig));
        dataModel.put(CLASS_NAME, getClassName(operationDef, fieldNames, objectTypeName, mappingConfig.getRequestSuffix()));
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
     * - EventsByIdsQueryRequest
     * - EventsByCategoryQueryRequest
     * - CreateEventMutationRequest
     */
    private static String getClassName(ExtendedFieldDefinition operationDef,
                                       List<String> fieldNames,
                                       String objectType,
                                       String requestSuffix) {
        StringBuilder classNameBuilder = new StringBuilder();
        classNameBuilder.append(Utils.capitalize(operationDef.getName()));
        if (Collections.frequency(fieldNames, operationDef.getName()) > 1) {
            classNameBuilder.append(MapperUtils.getClassNameSuffixWithInputValues(operationDef));
        }
        classNameBuilder.append(objectType);
        if (Utils.isNotBlank(requestSuffix)) {
            classNameBuilder.append(requestSuffix);
        }
        return classNameBuilder.toString();
    }
}
