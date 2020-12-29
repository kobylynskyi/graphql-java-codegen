package com.kobylynskyi.graphql.codegen.model;

import graphql.language.Directive;
import graphql.language.StringValue;

/**
 * @author liguobin@growingio.com
 * @version 1.0, 2020/12/28
 */
public class MultiLanguageDeprecated {

    private final static String REASON = "reason";
    private final static String SCALA_ANNOTATION = "deprecated";
    private final static String JAVA_ANNOTATION = "Deprecated";
    private final static String KOTLIN_ANNOTATION = "Deprecated";

    public static DeprecatedDefinition getLanguageDeprecated(GeneratedLanguage generatedLanguage, Directive directive) {
        String msg = null;
        if (directive.getArguments().stream().anyMatch(argument -> argument.getName().equals(REASON))) {
            msg = ((StringValue) directive.getArgument(REASON).getValue()).getValue();
        }
        switch (generatedLanguage) {
            case KOTLIN:
                return new DeprecatedDefinition(KOTLIN_ANNOTATION, msg);
            case SCALA:
                return new DeprecatedDefinition(SCALA_ANNOTATION, msg);
            //ignore msg
            case JAVA:
            default:
                return new DeprecatedDefinition(JAVA_ANNOTATION);
        }
    }
}
