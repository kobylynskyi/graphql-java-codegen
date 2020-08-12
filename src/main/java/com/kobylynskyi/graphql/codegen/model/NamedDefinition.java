package com.kobylynskyi.graphql.codegen.model;

public class NamedDefinition {

    private String name;
    private boolean isInterface;
    private boolean mandatory;

    public NamedDefinition(String name, boolean isInterface, boolean mandatory) {
        this.name = name;
        this.isInterface = isInterface;
        this.mandatory = mandatory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setInterface(boolean anInterface) {
        isInterface = anInterface;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
