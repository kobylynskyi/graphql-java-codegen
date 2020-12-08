package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.GraphQLTypeMapper;
import com.kobylynskyi.graphql.codegen.mapper.ValueFormatter;
import com.kobylynskyi.graphql.codegen.mapper.ValueMapper;

/**
 * Factory for creating JVM-language-specific mappers
 *
 * @author kobylynskyi
 */
public interface MapperFactory {

    DataModelMapper createDataModelMapper();

    GraphQLTypeMapper createGraphQLTypeMapper(ValueMapper valueMapper);

    ValueFormatter createValueFormatter();

}
