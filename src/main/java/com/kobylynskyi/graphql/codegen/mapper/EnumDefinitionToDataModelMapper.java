package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.EnumValueDefinition;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedEnumTypeDefinition;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedUnionTypeDefinition;
import graphql.language.Comment;
import graphql.language.Directive;
import graphql.language.DirectivesContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.ANNOTATIONS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.CLASS_NAME;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.FIELDS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.GENERATED_INFO;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.IMPLEMENTS;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.JAVA_DOC;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.PACKAGE;

/**
 * Map enum definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class EnumDefinitionToDataModelMapper {

    private EnumDefinitionToDataModelMapper() {
    }

    /**
     * Map field definition to a Freemarker data model
     *
     * @param mappingContext Global mapping context
     * @param definition     Definition of enum type including base definition and its extensions
     * @return Freemarker data model of the GraphQL enum
     */
    public static Map<String, Object> map(MappingContext mappingContext, ExtendedEnumTypeDefinition definition) {
        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingContext));
        dataModel.put(CLASS_NAME, MapperUtils.getModelClassNameWithPrefixAndSuffix(mappingContext, definition));
        dataModel.put(IMPLEMENTS, getUnionInterfaces(mappingContext, definition));
        dataModel.put(ANNOTATIONS, GraphqlTypeToJavaTypeMapper.getAnnotations(mappingContext, definition));
        dataModel.put(JAVA_DOC, definition.getJavaDoc());
        dataModel.put(FIELDS, map(definition.getValueDefinitions()));
        dataModel.put(GENERATED_INFO, mappingContext.getGeneratedInformation());
        return dataModel;
    }

    private static Set<String> getUnionInterfaces(MappingContext mappingContext,
                                                  ExtendedEnumTypeDefinition definition) {
        return mappingContext.getDocument().getUnionDefinitions()
                .stream()
                .filter(union -> union.isDefinitionPartOfUnion(definition))
                .map(ExtendedUnionTypeDefinition::getName)
                .map(unionName -> MapperUtils.getModelClassNameWithPrefixAndSuffix(mappingContext, unionName))
                .collect(Collectors.toSet());
    }

    /**
     * Mapper from GraphQL's EnumValueDefinition to a Freemarker-understandable format
     *
     * @param enumValueDefinitions list of GraphQL EnumValueDefinition types
     * @return list of strings
     */
    private static List<EnumValueDefinition> map(List<graphql.language.EnumValueDefinition> enumValueDefinitions) {
        return enumValueDefinitions.stream()
                .map(f -> new EnumValueDefinition(
                        MapperUtils.capitalizeIfRestricted(f.getName()),
                        f.getName(),
                        getJavaDoc(f.getComments()),
                        isDeprecated(f)))
                .collect(Collectors.toList());
    }

    private static boolean isDeprecated(DirectivesContainer<?> directivesContainer) {
        return directivesContainer.getDirectives().stream()
                .map(Directive::getName)
                .anyMatch(Deprecated.class.getSimpleName()::equalsIgnoreCase);
    }

    private static List<String> getJavaDoc(List<Comment> comments) {
        if (comments == null) {
            return Collections.emptyList();
        }
        return comments.stream()
                .map(Comment::getContent)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

}
