package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult;
import java.util.Map;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-17T11:17:19+0800"
)
public class HeroQueryResponse extends GraphQLResult<Map<String, io.github.dreamylost.model.CharacterEntity>> {

    private static final String OPERATION_NAME = "hero";

    public HeroQueryResponse() {
    }

    public io.github.dreamylost.model.CharacterEntity hero() {
        Map<String, io.github.dreamylost.model.CharacterEntity> data = getData();
        return data != null ? data.get(OPERATION_NAME) : null;
    }

}
