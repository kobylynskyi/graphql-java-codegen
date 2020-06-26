package com.kobylynskyi.graphql.codegen.model;

public class NamedDefinition {

    private String name;
    private boolean isInterface;

    public NamedDefinition() {
    }

    public NamedDefinition(String name, boolean isInterface) {
        this.name = name;
        this.isInterface = isInterface;
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
}
