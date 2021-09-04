package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.mapper.AnnotationsMapper;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.ValueMapper;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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
        StringBuilder typeNameWithPrefixAndSuffixBuilder = new StringBuilder();
        if (mappingContext.getModelNamePrefix() != null) {
            typeNameWithPrefixAndSuffixBuilder.append(mappingContext.getModelNamePrefix());
        }
        typeNameWithPrefixAndSuffixBuilder.append(typeName);
        if (mappingContext.getModelNameSuffix() != null) {
            typeNameWithPrefixAndSuffixBuilder.append(mappingContext.getModelNameSuffix());
        }
        Set<String> enumImportItSelf = mappingContext.getEnumImportItSelfInScala();
        if (enumImportItSelf == null ||
                !enumImportItSelf.contains(typeNameWithPrefixAndSuffixBuilder.toString())) {
            return Collections.emptyList();
        }
        // Inspired by the pr https://github.com/kobylynskyi/graphql-java-codegen/pull/637/files
        String modelPackageName = DataModelMapper.getModelPackageName(mappingContext);
        if (Utils.isNotBlank(modelPackageName)) {
            typeNameWithPrefixAndSuffixBuilder.insert(0, modelPackageName + ".");
        }
        String annotation = String.format(
                "com.fasterxml.jackson.module.scala.JsonScalaEnumeration(classOf[%sTypeRefer])",
                typeNameWithPrefixAndSuffixBuilder);
        return Collections.singletonList(annotation);
    }

    @Override
    public ValueMapper getValueMapper() {
        return valueMapper;
    }

}
