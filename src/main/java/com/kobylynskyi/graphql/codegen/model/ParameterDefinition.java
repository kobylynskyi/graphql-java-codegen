package com.kobylynskyi.graphql.codegen.model;

import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import graphql.schema.DataFetchingEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * Freemarker-understandable format of method parameter and field definition
 *
 * @author kobylynskyi
 */
public class ParameterDefinition {

    public static final ParameterDefinition DATA_FETCHING_ENVIRONMENT = new ParameterDefinition(
            DataFetchingEnvironment.class.getName(), "env");

    private String type;
    /**
     * Normalized name using {@link DataModelMapper#capitalizeIfRestricted(MappingContext, String)} }
     */
    private String name;
    /**
     * Original name that appears in GraphQL schema
     */
    private String originalName;
    private String defaultValue;
    private boolean isMandatory;
    private List<String> annotations = new ArrayList<>();
    private List<String> javaDoc = new ArrayList<>();
    private DeprecatedDefinition deprecated;
    private boolean serializeUsingObjectMapper;
    /**
     * Definition of the same type, but defined in the parent
     */
    private ParameterDefinition definitionInParentType;

    public ParameterDefinition() {
    }

    private ParameterDefinition(String type, String name) {
        this.type = type;
        this.name = name;
        this.originalName = name;
    }

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

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
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

    public boolean isSerializeUsingObjectMapper() {
        return serializeUsingObjectMapper;
    }

    public void setSerializeUsingObjectMapper(boolean serializeUsingObjectMapper) {
        this.serializeUsingObjectMapper = serializeUsingObjectMapper;
    }

    public void setDefinitionInParentType(ParameterDefinition definitionInParentType) {
        this.definitionInParentType = definitionInParentType;
    }

    public ParameterDefinition getDefinitionInParentType() {
        return definitionInParentType;
    }
}
