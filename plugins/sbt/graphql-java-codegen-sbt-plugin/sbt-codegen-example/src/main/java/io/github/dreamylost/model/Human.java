package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer;
import java.util.StringJoiner;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-16T17:07:53+0800"
)
public class Human implements java.io.Serializable, Character {

    @javax.validation.constraints.NotNull
    private String id;
    @javax.validation.constraints.NotNull
    private String name;
    private java.util.List<Character> friends;
    @javax.validation.constraints.NotNull
    private java.util.List<Episode> appearsIn;
    private String homePlanet;
    @Deprecated
    private String secretBackstory;
    @javax.validation.constraints.NotNull
    private io.github.dreamylost.scalar.EmailScalar email;

    public Human() {
    }

    public Human(String id, String name, java.util.List<Character> friends, java.util.List<Episode> appearsIn, String homePlanet, String secretBackstory, io.github.dreamylost.scalar.EmailScalar email) {
        this.id = id;
        this.name = name;
        this.friends = friends;
        this.appearsIn = appearsIn;
        this.homePlanet = homePlanet;
        this.secretBackstory = secretBackstory;
        this.email = email;
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

    public java.util.List<Character> getFriends() {
        return friends;
    }
    public void setFriends(java.util.List<Character> friends) {
        this.friends = friends;
    }

    public java.util.List<Episode> getAppearsIn() {
        return appearsIn;
    }
    public void setAppearsIn(java.util.List<Episode> appearsIn) {
        this.appearsIn = appearsIn;
    }

    public String getHomePlanet() {
        return homePlanet;
    }
    public void setHomePlanet(String homePlanet) {
        this.homePlanet = homePlanet;
    }

    @Deprecated
    public String getSecretBackstory() {
        return secretBackstory;
    }
    @Deprecated
    public void setSecretBackstory(String secretBackstory) {
        this.secretBackstory = secretBackstory;
    }

    public io.github.dreamylost.scalar.EmailScalar getEmail() {
        return email;
    }
    public void setEmail(io.github.dreamylost.scalar.EmailScalar email) {
        this.email = email;
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
        if (homePlanet != null) {
            joiner.add("homePlanet: " + GraphQLRequestSerializer.getEntry(homePlanet));
        }
        if (secretBackstory != null) {
            joiner.add("secretBackstory: " + GraphQLRequestSerializer.getEntry(secretBackstory));
        }
        if (email != null) {
            joiner.add("email: " + GraphQLRequestSerializer.getEntry(email));
        }
        return joiner.toString();
    }

    public static Human.Builder builder() {
        return new Human.Builder();
    }

    public static class Builder {

        private String id;
        private String name;
        private java.util.List<Character> friends;
        private java.util.List<Episode> appearsIn;
        private String homePlanet;
        private String secretBackstory;
        private io.github.dreamylost.scalar.EmailScalar email;

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

        public Builder setFriends(java.util.List<Character> friends) {
            this.friends = friends;
            return this;
        }

        public Builder setAppearsIn(java.util.List<Episode> appearsIn) {
            this.appearsIn = appearsIn;
            return this;
        }

        public Builder setHomePlanet(String homePlanet) {
            this.homePlanet = homePlanet;
            return this;
        }

        @Deprecated
        public Builder setSecretBackstory(String secretBackstory) {
            this.secretBackstory = secretBackstory;
            return this;
        }

        public Builder setEmail(io.github.dreamylost.scalar.EmailScalar email) {
            this.email = email;
            return this;
        }


        public Human build() {
            return new Human(id, name, friends, appearsIn, homePlanet, secretBackstory, email);
        }

    }
}
