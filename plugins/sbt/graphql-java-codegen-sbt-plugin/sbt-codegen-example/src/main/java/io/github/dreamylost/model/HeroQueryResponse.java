package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult;
import java.util.Map;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-16T17:07:53+0800"
)
public class HeroQueryResponse extends GraphQLResult<Map<String, Character>> {

    private static final String OPERATION_NAME = "hero";

    public HeroQueryResponse() {
    }

    public Character hero() {
        Map<String, Character> data = getData();
        return data != null ? data.get(OPERATION_NAME) : null;
    }

}
