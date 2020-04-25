package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.EnumValueDefinition;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedEnumTypeDefinition;
import graphql.language.Comment;
import graphql.language.Directive;
import graphql.language.DirectivesContainer;

import java.util.*;
import java.util.stream.Collectors;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;

/**
 * Map enum definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class EnumDefinitionToDataModelMapper {

    /**
     * Map field definition to a Freemarker data model
     *
     * @param mappingConfig Global mapping configuration
     * @param definition    Definition of enum type including base definition and its extensions
     * @return Freemarker data model of the GraphQL enum
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, ExtendedEnumTypeDefinition definition) {
        Map<String, Object> dataModel = new HashMap<>();
        // type/enum/input/interface/union classes do not require any imports
        dataModel.put(PACKAGE, MapperUtils.getModelPackageName(mappingConfig));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, definition));
        dataModel.put(JAVA_DOC, definition.getJavaDoc());
        dataModel.put(FIELDS, map(definition.getValueDefinitions()));
        return dataModel;
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
