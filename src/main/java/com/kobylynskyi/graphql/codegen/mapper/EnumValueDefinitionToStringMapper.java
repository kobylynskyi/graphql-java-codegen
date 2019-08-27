package com.kobylynskyi.graphql.codegen.mapper;

import graphql.language.EnumValueDefinition;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper from GraphQL's EnumValueDefinition to a Freemarker-understandable format
 *
 * @author kobylynskyi
 */
public class EnumValueDefinitionToStringMapper {

    public static List<String> map(List<EnumValueDefinition> enumValueDefinitions) {
        if (enumValueDefinitions == null) {
            return Collections.emptyList();
        }
        return enumValueDefinitions.stream()
                .map(EnumValueDefinitionToStringMapper::map)
                .collect(Collectors.toList());
    }

    public static String map(EnumValueDefinition enumValueDefinitions) {
        return MapperUtils.capitalizeIfRestricted(enumValueDefinitions.getName());
    }

}
