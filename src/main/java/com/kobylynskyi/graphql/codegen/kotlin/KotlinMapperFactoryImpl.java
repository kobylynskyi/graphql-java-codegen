package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.MapperFactory;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper;
import com.kobylynskyi.graphql.codegen.mapper.ValueFormatter;
import com.kobylynskyi.graphql.codegen.mapper.ValueMapper;

/**
 * @author 梦境迷离
 * @since 2020/12/09
 */
public class KotlinMapperFactoryImpl implements MapperFactory {

    @Override
    public DataModelMapper createDataModelMapper() {
        return new KotlinDataModelMapper();
    }

    @Override
    public GraphQLTypeMapper createGraphQLTypeMapper(ValueMapper valueMapper) {
        return new KotlinGraphQLTypeMapper(valueMapper);
    }

    @Override
    public ValueFormatter createValueFormatter() {
        return new KotlinValueFormatter();
    }

}
