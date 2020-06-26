package com.kobylynskyi.graphql.codegen.model;

import java.util.List;

/**
 * Freemarker-understandable format of enum value definition
 *
 * @author kobylynskyi
 */
public class EnumValueDefinition {

    private final String value;
    private final List<String> javaDoc;
    private final boolean deprecated;

    public EnumValueDefinition(String value, List<String> javaDoc, boolean deprecated) {
        this.value = value;
        this.javaDoc = javaDoc;
        this.deprecated = deprecated;
    }

    public String getValue() {
        return value;
    }

    public List<String> getJavaDoc() {
        return javaDoc;
    }

    public boolean isDeprecated() {
        return deprecated;
    }
}
