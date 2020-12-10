package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.MapperFactory;

public class DataModelMapperFactory {

    private final EnumDefinitionToDataModelMapper enumDefinitionToDataModelMapper;
    private final FieldDefinitionsToResolverDataModelMapper fieldDefinitionsToResolverDataModelMapper;
    private final InputDefinitionToDataModelMapper inputDefinitionToDataModelMapper;
    private final InterfaceDefinitionToDataModelMapper interfaceDefinitionToDataModelMapper;
    private final RequestResponseDefinitionToDataModelMapper requestResponseDefinitionToDataModelMapper;
    private final TypeDefinitionToDataModelMapper typeDefinitionToDataModelMapper;
    private final UnionDefinitionToDataModelMapper unionDefinitionToDataModelMapper;

    private final DataModelMapper dataModelMapper;
    private final FieldDefinitionToParameterMapper fieldDefinitionToParameterMapper;

    public DataModelMapperFactory(MapperFactory generatedLanguageMapperFactory) {
        ValueFormatter valueFormatter = generatedLanguageMapperFactory.createValueFormatter();
        dataModelMapper = generatedLanguageMapperFactory.createDataModelMapper();
        ValueMapper valueMapper = new ValueMapper(valueFormatter, dataModelMapper);
        GraphQLTypeMapper graphQLTypeMapper = generatedLanguageMapperFactory.createGraphQLTypeMapper(valueMapper);
        fieldDefinitionToParameterMapper = new FieldDefinitionToParameterMapper(graphQLTypeMapper, dataModelMapper);
        InputValueDefinitionToParameterMapper inputValueDefinitionToParameterMapper = new InputValueDefinitionToParameterMapper(valueMapper, graphQLTypeMapper, dataModelMapper);
        enumDefinitionToDataModelMapper = new EnumDefinitionToDataModelMapper(graphQLTypeMapper, dataModelMapper);
        unionDefinitionToDataModelMapper = new UnionDefinitionToDataModelMapper(graphQLTypeMapper, dataModelMapper);
        typeDefinitionToDataModelMapper = new TypeDefinitionToDataModelMapper(graphQLTypeMapper, dataModelMapper, fieldDefinitionToParameterMapper);
        interfaceDefinitionToDataModelMapper = new InterfaceDefinitionToDataModelMapper(graphQLTypeMapper, dataModelMapper, fieldDefinitionToParameterMapper);
        inputDefinitionToDataModelMapper = new InputDefinitionToDataModelMapper(graphQLTypeMapper, dataModelMapper, inputValueDefinitionToParameterMapper);
        fieldDefinitionsToResolverDataModelMapper = new FieldDefinitionsToResolverDataModelMapper(graphQLTypeMapper, dataModelMapper, inputValueDefinitionToParameterMapper);
        requestResponseDefinitionToDataModelMapper = new RequestResponseDefinitionToDataModelMapper(graphQLTypeMapper, dataModelMapper, fieldDefinitionToParameterMapper, inputValueDefinitionToParameterMapper);
    }

    public EnumDefinitionToDataModelMapper getEnumDefinitionMapper() {
        return enumDefinitionToDataModelMapper;
    }

    public FieldDefinitionsToResolverDataModelMapper getFieldDefinitionsToResolverMapper() {
        return fieldDefinitionsToResolverDataModelMapper;
    }

    public InputDefinitionToDataModelMapper getInputDefinitionMapper() {
        return inputDefinitionToDataModelMapper;
    }

    public InterfaceDefinitionToDataModelMapper getInterfaceDefinitionMapper() {
        return interfaceDefinitionToDataModelMapper;
    }

    public UnionDefinitionToDataModelMapper getUnionDefinitionMapper() {
        return unionDefinitionToDataModelMapper;
    }

    public RequestResponseDefinitionToDataModelMapper getRequestResponseDefinitionMapper() {
        return requestResponseDefinitionToDataModelMapper;
    }

    public TypeDefinitionToDataModelMapper getTypeDefinitionMapper() {
        return typeDefinitionToDataModelMapper;
    }

    public DataModelMapper getDataModelMapper() {
        return dataModelMapper;
    }

    public FieldDefinitionToParameterMapper getFieldDefinitionToParameterMapper() {
        return fieldDefinitionToParameterMapper;
    }
}
