package com.kobylynskyi.graphql.codegen.model;

import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Freemarker-understandable format of operation (Query/Mutation/Subscription)
 *
 * @author kobylynskyi
 */
public class OperationDefinition {

    /**
     * Normalized name using {@link DataModelMapper#capitalizeIfRestricted(MappingContext, String)} }
     */
    private String name;
    /**
     * Original name that appears in GraphQL schema
     */
    private String originalName;
    private String type;
    private List<String> annotations = new ArrayList<>();
    private List<ParameterDefinition> parameters = new ArrayList<>();
    private List<String> javaDoc = new ArrayList<>();
    private DeprecatedDefinition deprecated;
    private boolean throwsException;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
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

    public DeprecatedDefinition getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(DeprecatedDefinition deprecated) {
        this.deprecated = deprecated;
    }

    public boolean isThrowsException() {
        return throwsException;
    }

    public void setThrowsException(boolean throwsException) {
        this.throwsException = throwsException;
    }
}
