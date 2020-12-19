package com.kobylynskyi.graphql.codegen.kotlin;

import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.kobylynskyi.graphql.codegen.utils.Utils.wrapString;

/**
 * @author 梦境迷离
 * @since 2020/12/09
 */
public class KotlinDataModelMapper implements DataModelMapper {

    private static final String RESTRICTED_KEYWORDS_WRAP_WITH = "`";
    private static final Set<String> KOTLIN_RESTRICTED_KEYWORDS = new HashSet<>(Arrays.asList("package", "interface", "class",
            "object", "super", "null", "this", "typealias", "as", "as?", "if", "else", "true", "false", "while", "do",
            "for", "when", "break", "continue", "return", "fun", "in", "!in", "is", "!is", "throw", "try", "val", "var",
            "typeof"));

    //TODO maybe have others
    private static final Set<String> KOTLIN_RESTRICTED_METHOD_NAMES = new HashSet<>(Arrays.asList("notify", "notifyAll", "wait"));

    @Override
    public String capitalizeIfRestricted(MappingContext mappingContext, String fieldName) {
        if (KOTLIN_RESTRICTED_KEYWORDS.contains(fieldName)) {
            return wrapString(fieldName, RESTRICTED_KEYWORDS_WRAP_WITH);
        }
        return fieldName;
    }

    @Override
    public String capitalizeMethodNameIfRestricted(MappingContext mappingContext, String methodName) {
        if (KOTLIN_RESTRICTED_KEYWORDS.contains(methodName)) {
            return wrapString(methodName, RESTRICTED_KEYWORDS_WRAP_WITH);
        }
        if (KOTLIN_RESTRICTED_METHOD_NAMES.contains(methodName)) {
            return Utils.capitalize(methodName);
        }
        return methodName;
    }

    @Override
    public String getModelClassNameWithPrefixAndSuffix(MappingContext mappingContext,
                                                       ExtendedDefinition<?, ?> extendedDefinition) {
        return DataModelMapper.getModelClassNameWithPrefixAndSuffix(mappingContext, extendedDefinition.getName());
    }

}
