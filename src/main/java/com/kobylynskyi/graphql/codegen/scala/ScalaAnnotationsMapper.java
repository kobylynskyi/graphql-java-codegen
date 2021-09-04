package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.mapper.AnnotationsMapper;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.ValueMapper;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper class for converting GraphQL types to Scala types
 */
public class ScalaAnnotationsMapper extends AnnotationsMapper {

    private final ValueMapper valueMapper;

    public ScalaAnnotationsMapper(ValueMapper valueMapper) {
        this.valueMapper = valueMapper;
    }

    @Override
    public boolean addModelValidationAnnotationForType(String possiblyPrimitiveType) {
        return !ScalaGraphQLTypeMapper.isScalaPrimitive(possiblyPrimitiveType);
    }

    @Override
    public String getJacksonResolverTypeIdAnnotation(String modelPackageName) {
        return "com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(classOf[" + modelPackageName +
                "GraphqlJacksonTypeIdResolver])";
    }

    @Override
    public List<String> getAdditionalAnnotations(MappingContext mappingContext, String typeName) {
        List<String> defaults = new ArrayList<>();
        String typeNameWithPrefixAndSuffix = (mappingContext.getModelNamePrefix() == null ? ""
                : mappingContext.getModelNamePrefix())
                + typeName
                + (mappingContext.getModelNameSuffix() == null ? "" : mappingContext.getModelNameSuffix());
        boolean exists = null != mappingContext.getEnumImportItSelfInScala()
                && mappingContext.getEnumImportItSelfInScala()
                .contains(typeNameWithPrefixAndSuffix);
        // todo use switch
        // Inspired by the pr https://github.com/kobylynskyi/graphql-java-codegen/pull/637/files
        if (exists) {
            String modelPackageName = DataModelMapper.getModelPackageName(mappingContext);
            if (modelPackageName == null) {
                modelPackageName = "";
            } else if (Utils.isNotBlank(modelPackageName)) {
                modelPackageName += ".";
            }
            defaults.add("com.fasterxml.jackson.module.scala.JsonScalaEnumeration(classOf[" + modelPackageName
                    + typeNameWithPrefixAndSuffix + "TypeRefer])");
        }
        return defaults;
    }

    @Override
    public ValueMapper getValueMapper() {
        return valueMapper;
    }

}
