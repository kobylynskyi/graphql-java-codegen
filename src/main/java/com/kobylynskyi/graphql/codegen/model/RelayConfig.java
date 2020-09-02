package com.kobylynskyi.graphql.codegen.model;

import java.io.Serializable;

public class RelayConfig implements Serializable {

    // Increment this when the serialization output changes
    private static final long serialVersionUID = 937465092341L;

    private String directiveName = "connection";
    private String directiveArgumentName = "for";
    private String connectionType = "graphql.relay.Connection";

    public String getDirectiveName() {
        return directiveName;
    }

    public void setDirectiveName(String directiveName) {
        this.directiveName = directiveName;
    }

    public String getDirectiveArgumentName() {
        return directiveArgumentName;
    }

    public void setDirectiveArgumentName(String directiveArgumentName) {
        this.directiveArgumentName = directiveArgumentName;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
}
