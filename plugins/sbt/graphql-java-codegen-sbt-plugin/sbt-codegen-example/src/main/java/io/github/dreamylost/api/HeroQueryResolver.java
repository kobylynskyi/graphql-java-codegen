package io.github.dreamylost.api;

import io.github.dreamylost.model.*;
import io.github.dreamylost.model.Character;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-16T14:18:15+0800"
)
public interface HeroQueryResolver {

    Character hero(Episode episode) throws Exception;

}