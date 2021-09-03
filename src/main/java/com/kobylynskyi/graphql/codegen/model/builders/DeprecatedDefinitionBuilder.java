package com.kobylynskyi.graphql.codegen.model.builders;

import com.kobylynskyi.graphql.codegen.model.DeprecatedDefinition;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import graphql.language.Argument;
import graphql.language.Directive;
import graphql.language.DirectivesContainer;
import graphql.language.StringValue;

/**
 * Builder of @deprecated annotation definition
 */
@SuppressWarnings({"java:S1133", "java:S1123"}) // sonar treats this is a deprecated method
public class DeprecatedDefinitionBuilder {

    private static final String REASON = "reason";
    private static final String SCALA_ANNOTATION = "deprecated";
    private static final String JAVA_ANNOTATION = "Deprecated";
    private static final String KOTLIN_ANNOTATION = "Deprecated";

    /**
     * Get a definition of @deprecated based on a given directive
     *
     * @param mappingContext      Global mapping context
     * @param directivesContainer GraphQL directive container
     * @return a definition of a deprecation having reason and annotation
     */
    public static DeprecatedDefinition build(MappingContext mappingContext,
                                             DirectivesContainer<?> directivesContainer) {
        for (Directive d : directivesContainer.getDirectives()) {
            if (d.getName().equalsIgnoreCase(Deprecated.class.getSimpleName())) {
                return build(mappingContext.getGeneratedLanguage(), d);
            }
        }
        return null;
    }

    /**
     * Get a definition of @deprecated annotation for a specified language.
     *
     * @param generatedLanguage Language of code generation
     * @param directive         GraphQL @deprecated directive
     * @return a definition of a deprecation having reason and annotation
     */
    public static DeprecatedDefinition build(GeneratedLanguage generatedLanguage,
                                             Directive directive) {
        switch (generatedLanguage) {
            case KOTLIN:
                return new DeprecatedDefinition(KOTLIN_ANNOTATION, getMessage(directive));
            case SCALA:
                return new DeprecatedDefinition(SCALA_ANNOTATION, getMessage(directive));
            case JAVA:
            default:
                return new DeprecatedDefinition(JAVA_ANNOTATION); // message is ignored
        }
    }

    private static String getMessage(Directive directive) {
        for (Argument argument : directive.getArguments()) {
            if (argument.getName().equals(REASON)) {
                return getReasonString(directive).getValue();
            }
        }
        return null;
    }

    private static StringValue getReasonString(Directive directive) {
        return (StringValue) directive.getArgument(REASON).getValue();
    }

}
