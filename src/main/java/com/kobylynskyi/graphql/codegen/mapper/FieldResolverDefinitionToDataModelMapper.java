package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.FieldResolverDefinition;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.FieldDefinition;
import graphql.language.InputValueDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;
import static java.util.Collections.emptyList;

/**
 * Map field definition resolver to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class FieldResolverDefinitionToDataModelMapper {

    /**
     * Map field definition to a Freemarker data model
     *
     * @param mappingConfig Global mapping configuration
     * @param fieldDefs     GraphQL field definitions that require resolvers
     * @param typeName      Name of the type for which Resolver will be generated
     * @return Freemarker data model of the GraphQL parametrized field
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, List<FieldDefinition> fieldDefs,
                                          String typeName) {
        Map<String, Object> dataModel = new HashMap<>();
        String packageName = MapperUtils.getApiPackageName(mappingConfig);
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImportsForFieldResolvers(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, getClassName(typeName));
        dataModel.put(FIELDS, fieldDefs.stream()
                .map(fieldDef -> mapFieldDefinition(mappingConfig, fieldDef, typeName))
                .collect(Collectors.toList()));
        return dataModel;
    }


    /**
     * Map GraphQL's FieldDefinition to a Freemarker-understandable format of operation
     *
     * @param mappingConfig Global mapping configuration
     * @param typeName      Name of the type for which Resolver will be generated
     * @param fieldDef      GraphQL definition of the field which should have resolver
     * @return Freemarker-understandable format of Parametrized Field
     */
    private static FieldResolverDefinition mapFieldDefinition(MappingConfig mappingConfig,
                                                              FieldDefinition fieldDef,
                                                              String typeName) {
        FieldResolverDefinition parametrizedFieldDef = new FieldResolverDefinition();
        parametrizedFieldDef.setName(fieldDef.getName());
        parametrizedFieldDef.setType(GraphqlTypeToJavaTypeMapper.getJavaType(mappingConfig, fieldDef.getType(), fieldDef.getName(), typeName));

        List<ParameterDefinition> parameters = new ArrayList<>();
        // 1. Add type as a first method argument
        String typeNameParameterName = MapperUtils.capitalizeIfRestricted(Utils.uncapitalize(typeName)); // Commit -> commit
        parameters.add(new ParameterDefinition(typeName, typeNameParameterName, null, emptyList()));

        // 2. Add each field parameter as a method argument
        for (InputValueDefinition parameterDef : fieldDef.getInputValueDefinitions()) {
            String parameterDefType = GraphqlTypeToJavaTypeMapper.getJavaType(mappingConfig, parameterDef.getType(), parameterDef.getName(), fieldDef.getName());
            String defaultValue = DefaultValueMapper.map(parameterDef.getDefaultValue(), parameterDef.getType());
            parameters.add(new ParameterDefinition(parameterDefType, parameterDef.getName(), defaultValue, emptyList()));
        }
        // 3. Add DataFetchingEnvironment as a last method argument
        parameters.add(new ParameterDefinition("DataFetchingEnvironment", "env", null, emptyList()));

        parametrizedFieldDef.setParameters(parameters);
        return parametrizedFieldDef;
    }

    /**
     * Examples:
     * - PersonResolver
     */
    private static String getClassName(String typeName) {
        return Utils.capitalize(typeName) + "Resolver";
    }

}
