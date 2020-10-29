package com.kobylynskyi.graphql.codegen.model.graphql;

import java.util.Map;

public class GraphQLResponse extends GraphQLResult<Map<String, Object>> {

    public GraphQLResponse() {
        super();
    }

    public Object getData(String name) {
        Map<String, Object> data = getData();
        return data != null ? data.get(name) : null;
    }

}
