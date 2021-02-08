package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.DeprecatedDefinition;
import com.kobylynskyi.graphql.codegen.model.EnumValueDefinition;
import com.kobylynskyi.graphql.codegen.model.MappingConfigConstants;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedEnumTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedUnionTypeDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Comment;
import graphql.language.DirectivesContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;

/**
 * Map enum definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class EnumDefinitionToDataModelMapper {

    private final GraphQLTypeMapper graphQLTypeMapper;
    private final DataModelMapper dataModelMapper;

    public EnumDefinitionToDataModelMapper(GraphQLTypeMapper graphQLTypeMapper,
                                           DataModelMapper dataModelMapper) {
        this.graphQLTypeMapper = graphQLTypeMapper;
        this.dataModelMapper = dataModelMapper;
    }

    private static Set<String> getUnionInterfaces(MappingContext mappingContext,
                                                  ExtendedEnumTypeDefinition definition) {
        return mappingContext.getDocument().getUnionDefinitions()
                .stream()
                .filter(union -> union.isDefinitionPartOfUnion(definition))
                .map(ExtendedUnionTypeDefinition::getName)
                .map(unionName -> DataModelMapper.getModelClassNameWithPrefixAndSuffix(mappingContext, unionName))
                .collect(Collectors.toSet());
    }

    public DeprecatedDefinition getDeprecated(MappingContext mappingContext, DirectivesContainer<?> directivesContainer) {
        return graphQLTypeMapper.getDeprecated(mappingContext, directivesContainer);
    }

    private static List<String> getJavaDoc(graphql.language.EnumValueDefinition def) {
        if (def.getDescription() != null) {
            return Collections.singletonList(def.getDescription().getContent());
        }
        if (def.getComments() == null) {
            return Collections.emptyList();
        }
        return def.getComments().stream()
                .map(Comment::getContent).filter(Utils::isNotBlank)
                .map(String::trim).collect(Collectors.toList());
    }

    /**
     * Map field definition to a Freemarker data model
     *
     * @param mappingContext Global mapping context
     * @param definition     Definition of enum type including base definition and its extensions
     * @return Freemarker data model of the GraphQL enum
     */
    public Map<String, Object> map(MappingContext mappingContext, ExtendedEnumTypeDefinition definition) {
        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, DataModelMapper.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, dataModelMapper.getModelClassNameWithPrefixAndSuffix(mappingContext, definition));
        dataModel.put(IMPLEMENTS, getUnionInterfaces(mappingContext, definition));
        dataModel.put(ANNOTATIONS, graphQLTypeMapper.getAnnotations(mappingContext, definition));
        dataModel.put(JAVA_DOC, definition.getJavaDoc());
        dataModel.put(FIELDS, map(mappingContext, definition.getValueDefinitions()));
        dataModel.put(GENERATED_ANNOTATION, mappingContext.getAddGeneratedAnnotation());
        dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
        dataModel.put(SERIALIZATION_LIBRARY, MappingConfigConstants.DEFAULT_SERIALIZATION_LIBRARY);
        return dataModel;
    }

    /**
     * Mapper from GraphQL's EnumValueDefinition to a Freemarker-understandable format
     *
     * @param mappingContext       Global mapping context
     * @param enumValueDefinitions list of GraphQL EnumValueDefinition types
     * @return list of strings
     */
    private List<EnumValueDefinition> map(MappingContext mappingContext, List<graphql.language.EnumValueDefinition> enumValueDefinitions) {
        return enumValueDefinitions.stream()
                .map(f -> new EnumValueDefinition(
                        dataModelMapper.capitalizeIfRestricted(mappingContext, f.getName()),
                        f.getName(),
                        getJavaDoc(f),
                        getDeprecated(mappingContext, f)))
                .collect(Collectors.toList());
    }

}
