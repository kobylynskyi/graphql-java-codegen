package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.Objects;

public class GraphQLErrorSourceLocation {

    private int line;
    private int column;
    private String sourceName;

    public GraphQLErrorSourceLocation() {
    }

    public GraphQLErrorSourceLocation(int line, int column, String sourceName) {
        this.line = line;
        this.column = column;
        this.sourceName = sourceName;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphQLErrorSourceLocation that = (GraphQLErrorSourceLocation) o;
        return line == that.line &&
                column == that.column &&
                Objects.equals(sourceName, that.sourceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, column, sourceName);
    }

}
