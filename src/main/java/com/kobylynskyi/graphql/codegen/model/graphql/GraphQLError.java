package com.kobylynskyi.graphql.codegen.model.graphql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphQLError {

    private String message;
    private List<GraphQLErrorSourceLocation> locations;
    private GraphQLErrorType errorType;
    private List<Object> path;
    private Map<String, Object> extensions;

}