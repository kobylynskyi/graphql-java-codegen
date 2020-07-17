package io.github.dreamylost.api;

import io.github.dreamylost.model.*;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-17T11:17:19+0800"
)
public interface QueryResolver {

    io.github.dreamylost.model.CharacterEntity hero(EpisodeEntity episode) throws Exception;

    HumanEntity human(String id) throws Exception;

    java.util.List<HumanEntity> humans() throws Exception;

    DroidEntity droid(String id) throws Exception;

}