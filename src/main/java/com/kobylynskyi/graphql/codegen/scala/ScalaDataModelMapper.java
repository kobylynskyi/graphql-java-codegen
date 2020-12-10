package com.kobylynskyi.graphql.codegen.scala;

import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.definitions.ExtendedDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ScalaDataModelMapper implements DataModelMapper {

    private static final Set<String> SCALA_RESTRICTED_KEYWORDS = new HashSet<>(Arrays.asList(
            "package", "import", "class", "object", "trait", "extends", "with", "type", "forSome",
            "private", "protected", "abstract", "sealed", "final", "implicit", "lazy", "override", "try",
            "catch", "finally", "throw", "if", "else", "match", "case", "do", "while", "for", "return", "yield",
            "def", "val", "var", "this", "super", "new", "true", "false", "null"
    ));

    //TODO maybe have others
    private static final Set<String> SCALA_RESTRICTED_METHOD_NAMES = new HashSet<>(Arrays.asList(
            "getClass", "notify", "notifyAll", "wait", "clone", "finalize"));


    @Override
    public String capitalizeIfRestricted(MappingContext mappingContext, String fieldName) {

        if (SCALA_RESTRICTED_KEYWORDS.contains(fieldName)) {
            return Utils.capitalize(fieldName);
        }
        return fieldName;
    }

    @Override
    public String capitalizeMethodNameIfRestricted(MappingContext mappingContext, String methodName) {
        if (SCALA_RESTRICTED_KEYWORDS.contains(methodName)) {
            return Utils.capitalize(methodName);
        }
        if (SCALA_RESTRICTED_METHOD_NAMES.contains(methodName)) {
            return Utils.capitalize(methodName);
        }
        return methodName;
    }

    @Override
    public String getModelClassNameWithPrefixAndSuffix(MappingContext mappingContext,
                                                       ExtendedDefinition<?, ?> extendedDefinition) {
        String classNameWithPrefixAndSuffix = DataModelMapper.getModelClassNameWithPrefixAndSuffix(mappingContext, extendedDefinition.getName());
        mappingContext.getEnumImportItSelfInScala().add(classNameWithPrefixAndSuffix);
        return classNameWithPrefixAndSuffix;
    }

}
