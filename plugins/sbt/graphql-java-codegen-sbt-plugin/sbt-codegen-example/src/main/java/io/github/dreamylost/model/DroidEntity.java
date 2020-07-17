package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer;
import java.util.StringJoiner;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-17T10:15:09+0800"
)
public class DroidEntity implements java.io.Serializable, CharacterEntity {

    @javax.validation.constraints.NotNull
    private String id;
    @javax.validation.constraints.NotNull
    private String name;
    private java.util.List<CharacterEntity> friends;
    @javax.validation.constraints.NotNull
    private java.util.List<EpisodeEntity> appearsIn;
    private String primaryFunction;
    @Deprecated
    private String secretBackstory;

    public DroidEntity() {
    }

    public DroidEntity(String id, String name, java.util.List<CharacterEntity> friends, java.util.List<EpisodeEntity> appearsIn, String primaryFunction, String secretBackstory) {
        this.id = id;
        this.name = name;
        this.friends = friends;
        this.appearsIn = appearsIn;
        this.primaryFunction = primaryFunction;
        this.secretBackstory = secretBackstory;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public java.util.List<CharacterEntity> getFriends() {
        return friends;
    }
    public void setFriends(java.util.List<CharacterEntity> friends) {
        this.friends = friends;
    }

    public java.util.List<EpisodeEntity> getAppearsIn() {
        return appearsIn;
    }
    public void setAppearsIn(java.util.List<EpisodeEntity> appearsIn) {
        this.appearsIn = appearsIn;
    }

    public String getPrimaryFunction() {
        return primaryFunction;
    }
    public void setPrimaryFunction(String primaryFunction) {
        this.primaryFunction = primaryFunction;
    }

    @Deprecated
    public String getSecretBackstory() {
        return secretBackstory;
    }
    @Deprecated
    public void setSecretBackstory(String secretBackstory) {
        this.secretBackstory = secretBackstory;
    }


    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
        if (id != null) {
            joiner.add("id: " + GraphQLRequestSerializer.getEntry(id));
        }
        if (name != null) {
            joiner.add("name: " + GraphQLRequestSerializer.getEntry(name));
        }
        if (friends != null) {
            joiner.add("friends: " + GraphQLRequestSerializer.getEntry(friends));
        }
        if (appearsIn != null) {
            joiner.add("appearsIn: " + GraphQLRequestSerializer.getEntry(appearsIn));
        }
        if (primaryFunction != null) {
            joiner.add("primaryFunction: " + GraphQLRequestSerializer.getEntry(primaryFunction));
        }
        if (secretBackstory != null) {
            joiner.add("secretBackstory: " + GraphQLRequestSerializer.getEntry(secretBackstory));
        }
        return joiner.toString();
    }

    public static DroidEntity.Builder builder() {
        return new DroidEntity.Builder();
    }

    public static class Builder {

        private String id;
        private String name;
        private java.util.List<CharacterEntity> friends;
        private java.util.List<EpisodeEntity> appearsIn;
        private String primaryFunction;
        private String secretBackstory;

        public Builder() {
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setFriends(java.util.List<CharacterEntity> friends) {
            this.friends = friends;
            return this;
        }

        public Builder setAppearsIn(java.util.List<EpisodeEntity> appearsIn) {
            this.appearsIn = appearsIn;
            return this;
        }

        public Builder setPrimaryFunction(String primaryFunction) {
            this.primaryFunction = primaryFunction;
            return this;
        }

        @Deprecated
        public Builder setSecretBackstory(String secretBackstory) {
            this.secretBackstory = secretBackstory;
            return this;
        }


        public DroidEntity build() {
            return new DroidEntity(id, name, friends, appearsIn, primaryFunction, secretBackstory);
        }

    }
}
