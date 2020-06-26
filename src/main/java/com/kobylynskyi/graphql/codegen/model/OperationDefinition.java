package com.kobylynskyi.graphql.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Freemarker-understandable format of operation (Query/Mutation/Subscription)
 *
 * @author kobylynskyi
 */
public class OperationDefinition {

    private String name;
    private String type;
    private List<String> annotations = new ArrayList<>();
    private List<ParameterDefinition> parameters = new ArrayList<>();
    private List<String> javaDoc = new ArrayList<>();
    private boolean deprecated;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }

    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterDefinition> parameters) {
        this.parameters = parameters;
    }

    public List<String> getJavaDoc() {
        return javaDoc;
    }

    public void setJavaDoc(List<String> javaDoc) {
        this.javaDoc = javaDoc;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }
}
