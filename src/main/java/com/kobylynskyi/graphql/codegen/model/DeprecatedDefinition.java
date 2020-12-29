package com.kobylynskyi.graphql.codegen.model;

/**
 * @author 梦境迷离
 * @version 1.0, 2020/12/28
 */
public class DeprecatedDefinition {

    private static final String DEFAULT_MSG = "No longer supported";

    // For different languages, real implementation.
    private final String annotation;
    private final String reason;

    public DeprecatedDefinition(String annotation) {
        this.annotation = annotation;
        this.reason = DEFAULT_MSG;
    }

    /**
     * field reason Only support in Scala/Kotlin.
     *
     * @param annotation Definitions in different languages
     * @param reason     Description in graphql schema Directive
     */
    public DeprecatedDefinition(String annotation, String reason) {
        this.annotation = annotation;
        this.reason = reason == null ? DEFAULT_MSG : reason;
    }

    public String getAnnotation() {
        return annotation;
    }

    public String getReason() {
        return reason;
    }

}
