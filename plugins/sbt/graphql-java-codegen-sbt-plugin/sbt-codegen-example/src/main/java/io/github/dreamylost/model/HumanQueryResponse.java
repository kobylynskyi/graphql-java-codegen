package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult;
import java.util.Map;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-17T11:17:19+0800"
)
public class HumanQueryResponse extends GraphQLResult<Map<String, HumanEntity>> {

    private static final String OPERATION_NAME = "human";

    public HumanQueryResponse() {
    }

    public HumanEntity human() {
        Map<String, HumanEntity> data = getData();
        return data != null ? data.get(OPERATION_NAME) : null;
    }

}
