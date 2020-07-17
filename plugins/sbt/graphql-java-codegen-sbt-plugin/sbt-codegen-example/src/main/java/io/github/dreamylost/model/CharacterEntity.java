package io.github.dreamylost.model;


@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-17T10:15:09+0800"
)
public interface CharacterEntity {

    @javax.validation.constraints.NotNull
    String getId();

    @javax.validation.constraints.NotNull
    String getName();

    java.util.List<? extends CharacterEntity> getFriends();

    @javax.validation.constraints.NotNull
    java.util.List<EpisodeEntity> getAppearsIn();

    @Deprecated
    String getSecretBackstory();

}