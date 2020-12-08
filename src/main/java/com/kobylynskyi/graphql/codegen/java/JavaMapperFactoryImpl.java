package com.kobylynskyi.graphql.codegen.java;

import com.kobylynskyi.graphql.codegen.MapperFactory;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper;
import com.kobylynskyi.graphql.codegen.mapper.ValueFormatter;
import com.kobylynskyi.graphql.codegen.mapper.ValueMapper;

public class JavaMapperFactoryImpl implements MapperFactory {

    @Override
    public DataModelMapper createDataModelMapper() {
        return new JavaDataModelMapper();
    }

    @Override
    public GraphQLTypeMapper createGraphQLTypeMapper(ValueMapper valueMapper) {
        return new JavaGraphQLTypeMapper(valueMapper);
    }

    @Override
    public ValueFormatter createValueFormatter() {
        return new JavaValueFormatter();
    }

}
