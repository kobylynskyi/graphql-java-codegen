package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult;
import java.util.Map;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-17T10:15:09+0800"
)
public class DroidQueryResponse extends GraphQLResult<Map<String, DroidEntity>> {

    private static final String OPERATION_NAME = "droid";

    public DroidQueryResponse() {
    }

    public DroidEntity droid() {
        Map<String, DroidEntity> data = getData();
        return data != null ? data.get(OPERATION_NAME) : null;
    }

}
