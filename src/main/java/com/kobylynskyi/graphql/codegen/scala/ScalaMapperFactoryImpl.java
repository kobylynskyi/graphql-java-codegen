package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.MapperFactory;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper;
import com.kobylynskyi.graphql.codegen.mapper.ValueFormatter;
import com.kobylynskyi.graphql.codegen.mapper.ValueMapper;

public class ScalaMapperFactoryImpl implements MapperFactory {

    @Override
    public DataModelMapper createDataModelMapper() {
        return new ScalaDataModelMapper();
    }

    @Override
    public GraphQLTypeMapper createGraphQLTypeMapper(ValueMapper valueMapper) {
        return new ScalaGraphQLTypeMapper(valueMapper);
    }

    @Override
    public ValueFormatter createValueFormatter() {
        return new ScalaValueFormatter();
    }

}
