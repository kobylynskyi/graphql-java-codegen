package com.kobylynskyi.graphql.codegen.mapper;

/**
 * Factory of data model mappers
 */
public class DataModelMapperFactory {

    private final MapperFactory mapperFactory;
    private final FieldDefinitionToParameterMapper fieldDefToParamMapper;
    private final EnumDefinitionToDataModelMapper enumDefToDataModelMapper;
    private final UnionDefinitionToDataModelMapper unionDefToDataModelMapper;
    private final TypeDefinitionToDataModelMapper typeDefToDataModelMapper;
    private final InterfaceDefinitionToDataModelMapper interfaceDefToDataModelMapper;
    private final InputDefinitionToDataModelMapper inputDefToDataModelMapper;
    private final FieldDefinitionsToResolverDataModelMapper fieldDefsToResolverDataModelMapper;
    private final RequestResponseDefinitionToDataModelMapper requestResponseDefToDataModelMapper;

    /**
     * Constructor for creating a new DataModelMapperFactory based on a MapperFactory
     *
     * @param mapperFactory Mapper factory of a generated language
     */
    public DataModelMapperFactory(MapperFactory mapperFactory) {
        this.mapperFactory = mapperFactory;

        InputValueDefinitionToParameterMapper inputValueDefToParamMapper = new InputValueDefinitionToParameterMapper(
                mapperFactory);
        this.fieldDefToParamMapper = new FieldDefinitionToParameterMapper(mapperFactory, inputValueDefToParamMapper);
        this.enumDefToDataModelMapper = new EnumDefinitionToDataModelMapper(mapperFactory);
        this.unionDefToDataModelMapper = new UnionDefinitionToDataModelMapper(mapperFactory);
        this.typeDefToDataModelMapper = new TypeDefinitionToDataModelMapper(mapperFactory, fieldDefToParamMapper);
        this.interfaceDefToDataModelMapper = new InterfaceDefinitionToDataModelMapper(
                mapperFactory, fieldDefToParamMapper);
        this.inputDefToDataModelMapper = new InputDefinitionToDataModelMapper(
                mapperFactory, inputValueDefToParamMapper);
        this.fieldDefsToResolverDataModelMapper = new FieldDefinitionsToResolverDataModelMapper(
                mapperFactory, inputValueDefToParamMapper);
        this.requestResponseDefToDataModelMapper = new RequestResponseDefinitionToDataModelMapper(
                mapperFactory, fieldDefToParamMapper, inputValueDefToParamMapper);
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

    public FieldDefinitionToParameterMapper getFieldDefToParamMapper() {
        return fieldDefToParamMapper;
    }

    public MapperFactory getMapperFactory() {
        return mapperFactory;
    }
}
