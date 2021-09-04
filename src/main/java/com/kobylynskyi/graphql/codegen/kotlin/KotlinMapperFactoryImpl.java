package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.mapper.AnnotationsMapper;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper;
import com.kobylynskyi.graphql.codegen.mapper.MapperFactory;
import com.kobylynskyi.graphql.codegen.mapper.ValueMapper;

/**
 * A factory of various mappers for Kotlin language
 *
 * @author 梦境迷离
 * @since 2020/12/09
 */
public class KotlinMapperFactoryImpl implements MapperFactory {

    private final DataModelMapper dataModelMapper;
    private final ValueMapper valueMapper;
    private final GraphQLTypeMapper graphQLTypeMapper;
    private final AnnotationsMapper annotationsMapper;

    public KotlinMapperFactoryImpl() {
        dataModelMapper = new KotlinDataModelMapper();
        valueMapper = new ValueMapper(new KotlinValueFormatter(), dataModelMapper);
        graphQLTypeMapper = new KotlinGraphQLTypeMapper();
        annotationsMapper = new KotlinAnnotationsMapper(valueMapper);
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
