package com.kobylynskyi.graphql.codegen.model;

/**
 * Freemarker-understandable format of parameter user in ResponseProjection
 *
 * @author kobylynskyi
 */
public class ProjectionParameterDefinition {

    private String type;
    private String name;
    private String methodName;
    private boolean deprecated;
    private String parametrizedInputClassName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getParametrizedInputClassName() {
        return parametrizedInputClassName;
    }

    public void setParametrizedInputClassName(String parametrizedInputClassName) {
        this.parametrizedInputClassName = parametrizedInputClassName;
    }
}
