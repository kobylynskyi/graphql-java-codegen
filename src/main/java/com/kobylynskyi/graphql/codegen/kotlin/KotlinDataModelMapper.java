package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 梦境迷离
 * @since 2020/12/09
 */
public class KotlinDataModelMapper implements DataModelMapper {

    private static final Set<String> KOTLIN_RESTRICTED_KEYWORDS = new HashSet<>(Arrays.asList("package", "interface", "class",
            "object", "super", "null", "this", "typealias", "as", "as?", "if", "else", "true", "false", "while", "do",
            "for", "when", "break", "continue", "return", "fun", "in", "!in", "is", "!is", "throw", "try", "val", "var",
            "typeof"));

    //TODO maybe have others
    private static final Set<String> KOTLIN_RESTRICTED_METHOD_NAMES = new HashSet<>(Arrays.asList("notify", "notifyAll", "wait"));

    /**
     * {@inheritDoc}
     */
    @Override
    public String capitalizeIfRestricted(MappingContext mappingContext, String fieldName) {

        if (KOTLIN_RESTRICTED_KEYWORDS.contains(fieldName)) {
            return Utils.capitalize(fieldName);
        }
        return fieldName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String capitalizeMethodNameIfRestricted(MappingContext mappingContext, String methodName) {
        if (KOTLIN_RESTRICTED_KEYWORDS.contains(methodName)) {
            return Utils.capitalize(methodName);
        }
        if (KOTLIN_RESTRICTED_METHOD_NAMES.contains(methodName)) {
            return Utils.capitalize(methodName);
        }
        return methodName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModelClassNameWithPrefixAndSuffix(MappingContext mappingContext,
                                                       ExtendedDefinition<?, ?> extendedDefinition) {
        String classNameWithPrefixAndSuffix = DataModelMapper.getModelClassNameWithPrefixAndSuffix(mappingContext, extendedDefinition.getName());
        mappingContext.getEnumImportItSelfInScala().add(classNameWithPrefixAndSuffix);
        return classNameWithPrefixAndSuffix;
    }

}