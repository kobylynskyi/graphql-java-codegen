package com.kobylynskyi.graphql.codegen.model.graphql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphQLErrorSourceLocation {

    private int line;
    private int column;
    private String sourceName;

}
