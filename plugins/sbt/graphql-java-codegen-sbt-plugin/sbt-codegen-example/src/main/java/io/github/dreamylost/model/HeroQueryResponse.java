package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult;
import java.util.Map;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-17T10:15:09+0800"
)
public class HeroQueryResponse extends GraphQLResult<Map<String, CharacterEntity>> {

    private static final String OPERATION_NAME = "hero";

    public HeroQueryResponse() {
    }

    public CharacterEntity hero() {
        Map<String, CharacterEntity> data = getData();
        return data != null ? data.get(OPERATION_NAME) : null;
    }

}
