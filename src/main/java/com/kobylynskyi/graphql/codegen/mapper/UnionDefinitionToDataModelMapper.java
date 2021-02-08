package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedUnionTypeDefinition;

import java.util.HashMap;
import java.util.Map;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.ANNOTATIONS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.ENUM_IMPORT_IT_SELF_IN_SCALA;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.GENERATED_INFO;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.GENERATED_ANNOTATION;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.JAVA_DOC;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.PACKAGE;

/**
 * Map union definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class UnionDefinitionToDataModelMapper {

    private final GraphQLTypeMapper graphQLTypeMapper;
    private final DataModelMapper dataModelMapper;

    public UnionDefinitionToDataModelMapper(GraphQLTypeMapper graphQLTypeMapper,
                                            DataModelMapper dataModelMapper) {
        this.graphQLTypeMapper = graphQLTypeMapper;
        this.dataModelMapper = dataModelMapper;
    }

    /**
     * Map union definition to a Freemarker data model
     *
     * @param mappingContext Global mapping context
     * @param definition     Definition of union type including base definition and its extensions
     * @return Freemarker data model of the GraphQL union
     */
    public Map<String, Object> map(MappingContext mappingContext, ExtendedUnionTypeDefinition definition) {
        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, dataModelMapper.getModelClassNameWithPrefixAndSuffix(mappingContext, definition));
        dataModel.put(ANNOTATIONS, graphQLTypeMapper.getAnnotations(mappingContext, definition));
        dataModel.put(JAVA_DOC, definition.getJavaDoc());
        dataModel.put(GENERATED_ANNOTATION, mappingContext.getAddGeneratedAnnotation());
        dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
        dataModel.put(ENUM_IMPORT_IT_SELF_IN_SCALA, mappingContext.getEnumImportItSelfInScala());
        return dataModel;
    }

}
