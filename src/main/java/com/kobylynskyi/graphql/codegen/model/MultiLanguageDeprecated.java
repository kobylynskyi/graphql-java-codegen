package com.kobylynskyi.graphql.codegen.model;

import graphql.language.Directive;
import graphql.language.StringValue;

/**
 * @author liguobin@growingio.com
 * @version 1.0, 2020/12/28
 */
public class MultiLanguageDeprecated {

    public static DeprecatedDefinition getLanguageDeprecated(GeneratedLanguage generatedLanguage, Directive directive) {
        String JAVA = "Deprecated";
        String SCALA = "deprecated";
        String KOTLIN = "Deprecated";
        String msg = null;
        if (directive.getArguments().parallelStream().anyMatch(argument -> argument.getName().equals("reason"))) {
            msg = ((StringValue) directive.getArgument("reason").getValue()).getValue();
        }
        switch (generatedLanguage) {
            case KOTLIN:
                return new DeprecatedDefinition(KOTLIN, msg);
            case SCALA:
                return new DeprecatedDefinition(SCALA, msg);
            //ignore msg
            case JAVA:
            default:
                return new DeprecatedDefinition(JAVA);
        }
    }
}
