package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Class which contains all information about a field which should
 * be returned back to the client.
 */
public class GraphQLResponseField {

    private final String name;
    private String alias;
    private GraphQLParametrizedInput parameters;
    private GraphQLResponseProjection projection;

    public GraphQLResponseField(String name) {
        this.name = name;
    }

    public GraphQLResponseField alias(String alias) {
        this.alias = alias;
        return this;
    }

    public GraphQLResponseField parameters(GraphQLParametrizedInput parameters) {
        this.parameters = parameters;
        return this;
    }

    public GraphQLResponseField projection(GraphQLResponseProjection projection) {
        this.projection = projection;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public GraphQLParametrizedInput getParameters() {
        return parameters;
    }

    public GraphQLResponseProjection getProjection() {
        return projection;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ");

        if (getAlias() != null) {
            joiner.add(getAlias()).add(":");
        }
        joiner.add(getName());
        if (getParameters() != null) {
            joiner.add(getParameters().toString());
        }
        if (getProjection() != null) {
            joiner.add(getProjection().toString());
        }

        return joiner.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final GraphQLResponseField that = (GraphQLResponseField) obj;
        return Objects.equals(name, that.name)
                && Objects.equals(alias, that.alias)
                && Objects.equals(parameters, that.parameters)
                && Objects.equals(projection, that.projection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, alias, parameters, projection);
    }
}
