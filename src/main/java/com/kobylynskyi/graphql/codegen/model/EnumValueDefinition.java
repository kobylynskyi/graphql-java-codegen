package com.kobylynskyi.graphql.codegen.model;

import graphql.language.Directive;
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
    private final DeprecatedDefinition deprecated;
    private final List<Directive> directives;

    public EnumValueDefinition(String javaName, String graphqlName, List<String> javaDoc,
                               DeprecatedDefinition deprecated, 
                               List<Directive> directives) {
        this.javaName = javaName;
        this.graphqlName = graphqlName;
        this.javaDoc = javaDoc;
        this.deprecated = deprecated;
        this.directives = directives;
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

    public DeprecatedDefinition getDeprecated() {
        return deprecated;
    }

    public List<Directive> getDirectives() {
        return directives;
    }
}
