package com.kobylynskyi.graphql.codegen.mapper;

/**
 * Factory for creating JVM-language-specific mappers
 *
 * @author kobylynskyi
 */
public interface MapperFactory {

    DataModelMapper getDataModelMapper();

    GraphQLTypeMapper getGraphQLTypeMapper();

    AnnotationsMapper getAnnotationsMapper();

    ValueMapper getValueMapper();

}
