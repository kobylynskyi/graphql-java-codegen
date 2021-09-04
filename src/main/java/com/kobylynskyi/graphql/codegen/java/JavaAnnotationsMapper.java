package com.kobylynskyi.graphql.codegen.java;

import com.kobylynskyi.graphql.codegen.mapper.AnnotationsMapper;
import com.kobylynskyi.graphql.codegen.mapper.ValueMapper;
import com.kobylynskyi.graphql.codegen.model.MappingContext;

import java.util.Collections;
import java.util.List;

/**
 * Mapper class for converting GraphQL types to Java types
 */
public class JavaAnnotationsMapper extends AnnotationsMapper {

    private final ValueMapper valueMapper;

    public JavaAnnotationsMapper(ValueMapper valueMapper) {
        this.valueMapper = valueMapper;
    }

    @Override
    public boolean addModelValidationAnnotationForType(String type) {
        return !JavaGraphQLTypeMapper.isJavaPrimitive(type);
    }

    @Override
    public ValueMapper getValueMapper() {
        return valueMapper;
    }

    @Override
    public String getJacksonResolverTypeIdAnnotation(String modelPackageName) {
        return "com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(" + modelPackageName +
                "GraphqlJacksonTypeIdResolver.class)";
    }

    @Override
    protected List<String> getAdditionalAnnotations(MappingContext mappingContext, String typeName) {
        return Collections.emptyList();
    }
}