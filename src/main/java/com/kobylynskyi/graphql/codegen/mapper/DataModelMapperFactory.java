package com.kobylynskyi.graphql.codegen.mapper;

/**
 * Factory of data model mappers
 */
public class DataModelMapperFactory {

    private final EnumDefinitionToDataModelMapper enumDefToDataModelMapper;
    private final FieldDefinitionsToResolverDataModelMapper fieldDefsToResolverDataModelMapper;
    private final InputDefinitionToDataModelMapper inputDefToDataModelMapper;
    private final InterfaceDefinitionToDataModelMapper interfaceDefToDataModelMapper;
    private final RequestResponseDefinitionToDataModelMapper requestResponseDefToDataModelMapper;
    private final TypeDefinitionToDataModelMapper typeDefToDataModelMapper;
    private final UnionDefinitionToDataModelMapper unionDefToDataModelMapper;

    private final DataModelMapper dataModelMapper;
    private final FieldDefinitionToParameterMapper fieldDefToParamMapper;

    /**
     * Constructor for creating a new DataModelMapperFactory based on a MapperFactory
     *
     * @param generatedLanguageMapperFactory Mapper factory of a generated language
     */
    public DataModelMapperFactory(MapperFactory generatedLanguageMapperFactory) {
        ValueFormatter valueFormatter = generatedLanguageMapperFactory.createValueFormatter();
        dataModelMapper = generatedLanguageMapperFactory.createDataModelMapper();
        ValueMapper valueMapper = new ValueMapper(valueFormatter, dataModelMapper);
        GraphQLTypeMapper graphQlTypeMapper = generatedLanguageMapperFactory.createGraphQLTypeMapper(valueMapper);
        fieldDefToParamMapper = new FieldDefinitionToParameterMapper(graphQlTypeMapper, dataModelMapper);
        enumDefToDataModelMapper = new EnumDefinitionToDataModelMapper(graphQlTypeMapper, dataModelMapper);
        unionDefToDataModelMapper = new UnionDefinitionToDataModelMapper(graphQlTypeMapper, dataModelMapper);
        typeDefToDataModelMapper = new TypeDefinitionToDataModelMapper(graphQlTypeMapper, dataModelMapper,
                fieldDefToParamMapper);
        interfaceDefToDataModelMapper = new InterfaceDefinitionToDataModelMapper(graphQlTypeMapper, dataModelMapper,
                fieldDefToParamMapper);
        InputValueDefinitionToParameterMapper inputValueDefToParamMapper = new InputValueDefinitionToParameterMapper(
                valueMapper, graphQlTypeMapper, dataModelMapper);
        inputDefToDataModelMapper = new InputDefinitionToDataModelMapper(graphQlTypeMapper, dataModelMapper,
                inputValueDefToParamMapper);
        fieldDefsToResolverDataModelMapper = new FieldDefinitionsToResolverDataModelMapper(
                graphQlTypeMapper, dataModelMapper, inputValueDefToParamMapper);
        requestResponseDefToDataModelMapper = new RequestResponseDefinitionToDataModelMapper(
                graphQlTypeMapper, dataModelMapper, fieldDefToParamMapper, inputValueDefToParamMapper);
    }

    public EnumDefinitionToDataModelMapper getEnumDefinitionMapper() {
        return enumDefToDataModelMapper;
    }

    public FieldDefinitionsToResolverDataModelMapper getFieldDefinitionsToResolverMapper() {
        return fieldDefsToResolverDataModelMapper;
    }

    public InputDefinitionToDataModelMapper getInputDefinitionMapper() {
        return inputDefToDataModelMapper;
    }

    public InterfaceDefinitionToDataModelMapper getInterfaceDefinitionMapper() {
        return interfaceDefToDataModelMapper;
    }

    public UnionDefinitionToDataModelMapper getUnionDefinitionMapper() {
        return unionDefToDataModelMapper;
    }

    public RequestResponseDefinitionToDataModelMapper getRequestResponseDefinitionMapper() {
        return requestResponseDefToDataModelMapper;
    }

    public TypeDefinitionToDataModelMapper getTypeDefinitionMapper() {
        return typeDefToDataModelMapper;
    }

    public DataModelMapper getDataModelMapper() {
        return dataModelMapper;
    }

    public FieldDefinitionToParameterMapper getFieldDefToParamMapper() {
        return fieldDefToParamMapper;
    }
}
