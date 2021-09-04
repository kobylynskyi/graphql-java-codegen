package com.kobylynskyi.graphql.codegen.java;

import com.kobylynskyi.graphql.codegen.mapper.AnnotationsMapper;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper;
import com.kobylynskyi.graphql.codegen.mapper.MapperFactory;
import com.kobylynskyi.graphql.codegen.mapper.ValueMapper;

/**
 * A factory of various mappers for Java language
 */
public class JavaMapperFactoryImpl implements MapperFactory {

    private final DataModelMapper dataModelMapper;
    private final ValueMapper valueMapper;
    private final GraphQLTypeMapper graphQLTypeMapper;
    private final AnnotationsMapper annotationsMapper;

    public JavaMapperFactoryImpl() {
        dataModelMapper = new JavaDataModelMapper();
        valueMapper = new ValueMapper(new JavaValueFormatter(), dataModelMapper);
        graphQLTypeMapper = new JavaGraphQLTypeMapper();
        annotationsMapper = new JavaAnnotationsMapper(valueMapper);
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
