package com.kobylynskyi.graphql.codegen.model;

import java.util.List;

/**
 * Freemarker-understandable format of enum value definition
 *
 * @author kobylynskyi
 */
public class EnumValueDefinition {

    private final String javaName;
    private final String graphqlName;
    private final List<String> javaDoc;
    private final boolean deprecated;

    public EnumValueDefinition(String javaName, String graphqlName, List<String> javaDoc, boolean deprecated) {
        this.javaName = javaName;
        this.graphqlName = graphqlName;
        this.javaDoc = javaDoc;
        this.deprecated = deprecated;
    }

    public String getJavaName() {
        return javaName;
    }

    public String getGraphqlName() {
        return graphqlName;
    }

    public List<String> getJavaDoc() {
        return javaDoc;
    }

    public boolean isDeprecated() {
        return deprecated;
    }
}
