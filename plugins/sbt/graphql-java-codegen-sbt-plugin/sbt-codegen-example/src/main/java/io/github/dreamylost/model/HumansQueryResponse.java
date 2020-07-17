package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult;
import java.util.Map;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-17T10:15:09+0800"
)
public class HumansQueryResponse extends GraphQLResult<Map<String, java.util.List<HumanEntity>>> {

    private static final String OPERATION_NAME = "humans";

    public HumansQueryResponse() {
    }

    public java.util.List<HumanEntity> humans() {
        Map<String, java.util.List<HumanEntity>> data = getData();
        return data != null ? data.get(OPERATION_NAME) : null;
    }

}
