package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult;
import java.util.Map;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-16T14:18:15+0800"
)
public class HumansQueryResponse extends GraphQLResult<Map<String, java.util.List<Human>>> {

    private static final String OPERATION_NAME = "humans";

    public HumansQueryResponse() {
    }

    public java.util.List<Human> humans() {
        Map<String, java.util.List<Human>> data = getData();
        return data != null ? data.get(OPERATION_NAME) : null;
    }

}
