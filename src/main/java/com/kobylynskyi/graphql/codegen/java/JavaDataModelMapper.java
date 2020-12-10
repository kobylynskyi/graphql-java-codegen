package com.kobylynskyi.graphql.codegen.java;

import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JavaDataModelMapper implements DataModelMapper {

    private static final Set<String> JAVA_RESTRICTED_KEYWORDS = new HashSet<>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double", "else", "enum", "extends", "false", "final", "finally", "float", "for", "goto",
            "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package",
            "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while"));

    private static final Set<String> JAVA_RESTRICTED_METHOD_NAMES = new HashSet<>(Arrays.asList(
            "getClass", "notify", "notifyAll", "wait"));

    @Override
    public String capitalizeIfRestricted(MappingContext mappingContext, String fieldName) {
        if (JAVA_RESTRICTED_KEYWORDS.contains(fieldName)) {
            return Utils.capitalize(fieldName);
        }
        return fieldName;
    }

    @Override
    public String capitalizeMethodNameIfRestricted(MappingContext mappingContext, String methodName) {
        if (JAVA_RESTRICTED_KEYWORDS.contains(methodName)) {
            return Utils.capitalize(methodName);
        }
        if (JAVA_RESTRICTED_METHOD_NAMES.contains(methodName)) {
            return Utils.capitalize(methodName);
        }
        return methodName;
    }

}
