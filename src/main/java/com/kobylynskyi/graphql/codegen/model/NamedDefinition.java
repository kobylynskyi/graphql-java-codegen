package com.kobylynskyi.graphql.codegen.model;

/**
 * Class describes a specific GraphQL type converted to a specific JVM language
 */
public class NamedDefinition {

    private String javaName;
    private String graphqlTypeName;
    private boolean isInterfaceOrUnion;
    private boolean mandatory;
    private boolean primitiveCanBeUsed;
    private boolean serializeUsingObjectMapper;

    public NamedDefinition(String javaName, String graphqlTypeName,
                           boolean isInterfaceOrUnion, boolean mandatory,
                           boolean primitiveCanBeUsed, boolean serializeUsingObjectMapper) {
        this.javaName = javaName;
        this.graphqlTypeName = graphqlTypeName;
        this.isInterfaceOrUnion = isInterfaceOrUnion;
        this.mandatory = mandatory;
        this.primitiveCanBeUsed = primitiveCanBeUsed;
        this.serializeUsingObjectMapper = serializeUsingObjectMapper;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public String getGraphqlTypeName() {
        return graphqlTypeName;
    }

    public void setGraphqlTypeName(String graphqlTypeName) {
        this.graphqlTypeName = graphqlTypeName;
    }

    public boolean isInterfaceOrUnion() {
        return isInterfaceOrUnion;
    }

    public void setInterfaceOrUnion(boolean interfaceOrUnion) {
        isInterfaceOrUnion = interfaceOrUnion;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isPrimitiveCanBeUsed() {
        return primitiveCanBeUsed;
    }

    public void setPrimitiveCanBeUsed(boolean primitiveCanBeUsed) {
        this.primitiveCanBeUsed = primitiveCanBeUsed;
    }

    public boolean isSerializeUsingObjectMapper() {
        return serializeUsingObjectMapper;
    }

    public void setSerializeUsingObjectMapper(boolean serializeUsingObjectMapper) {
        this.serializeUsingObjectMapper = serializeUsingObjectMapper;
    }
}
