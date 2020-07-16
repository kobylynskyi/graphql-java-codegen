package io.github.dreamylost.model;


@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-16T14:18:15+0800"
)
public interface Character {

    @javax.validation.constraints.NotNull
    String getId();

    @javax.validation.constraints.NotNull
    String getName();

    java.util.List<? extends Character> getFriends();

    @javax.validation.constraints.NotNull
    java.util.List<Episode> getAppearsIn();

    @Deprecated
    String getSecretBackstory();

}