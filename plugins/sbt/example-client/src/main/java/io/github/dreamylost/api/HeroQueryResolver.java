package io.github.dreamylost.api;

import io.github.dreamylost.model.*;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-17T11:17:19+0800"
)
public interface HeroQueryResolver {

    io.github.dreamylost.model.CharacterEntity hero(EpisodeEntity episode) throws Exception;

}