package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.mapper.AnnotationsMapper;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper;
import com.kobylynskyi.graphql.codegen.mapper.MapperFactory;
import com.kobylynskyi.graphql.codegen.mapper.ValueMapper;

/**
 * A factory of various mappers for Scala language
 */
public class ScalaMapperFactoryImpl implements MapperFactory {

    private final DataModelMapper dataModelMapper;
    private final ValueMapper valueMapper;
    private final GraphQLTypeMapper graphQLTypeMapper;
    private final AnnotationsMapper annotationsMapper;

    public ScalaMapperFactoryImpl() {
        dataModelMapper = new ScalaDataModelMapper();
        valueMapper = new ValueMapper(new ScalaValueFormatter(), dataModelMapper);
        graphQLTypeMapper = new ScalaGraphQLTypeMapper();
        annotationsMapper = new ScalaAnnotationsMapper(valueMapper);
    }

    @Override
    public DataModelMapper getDataModelMapper() {
        return dataModelMapper;
    }

    @Override
    public GraphQLTypeMapper getGraphQLTypeMapper() {
        return graphQLTypeMapper;
    }

    @Override
    public AnnotationsMapper getAnnotationsMapper() {
        return annotationsMapper;
    }

    @Override
    public ValueMapper getValueMapper() {
        return valueMapper;
    }

}
