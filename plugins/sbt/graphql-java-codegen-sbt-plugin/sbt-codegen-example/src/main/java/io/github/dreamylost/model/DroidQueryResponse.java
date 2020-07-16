package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult;
import java.util.Map;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-16T17:07:53+0800"
)
public class DroidQueryResponse extends GraphQLResult<Map<String, Droid>> {

    private static final String OPERATION_NAME = "droid";

    public DroidQueryResponse() {
    }

    public Droid droid() {
        Map<String, Droid> data = getData();
        return data != null ? data.get(OPERATION_NAME) : null;
    }

}
