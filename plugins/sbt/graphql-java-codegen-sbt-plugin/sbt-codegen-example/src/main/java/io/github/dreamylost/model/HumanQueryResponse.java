package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult;
import java.util.Map;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-16T14:18:15+0800"
)
public class HumanQueryResponse extends GraphQLResult<Map<String, Human>> {

    private static final String OPERATION_NAME = "human";

    public HumanQueryResponse() {
    }

    public Human human() {
        Map<String, Human> data = getData();
        return data != null ? data.get(OPERATION_NAME) : null;
    }

}
