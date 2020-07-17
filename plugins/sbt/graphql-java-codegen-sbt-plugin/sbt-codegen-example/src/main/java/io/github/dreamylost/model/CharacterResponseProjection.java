package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

/**
 * Response projection for Character
 */
@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-17T10:15:09+0800"
)
public class CharacterResponseProjection extends GraphQLResponseProjection {

    public CharacterResponseProjection() {
    }

    public CharacterResponseProjection id() {
        return id(null);
    }

    public CharacterResponseProjection id(String alias) {
        fields.add(new GraphQLResponseField("id").alias(alias));
        return this;
    }

    public CharacterResponseProjection name() {
        return name(null);
    }

    public CharacterResponseProjection name(String alias) {
        fields.add(new GraphQLResponseField("name").alias(alias));
        return this;
    }

    public CharacterResponseProjection friends(CharacterResponseProjection subProjection) {
        return friends(null, subProjection);
    }

    public CharacterResponseProjection friends(String alias, CharacterResponseProjection subProjection) {
        fields.add(new GraphQLResponseField("friends").alias(alias).projection(subProjection));
        return this;
    }

    public CharacterResponseProjection appearsIn() {
        return appearsIn(null);
    }

    public CharacterResponseProjection appearsIn(String alias) {
        fields.add(new GraphQLResponseField("appearsIn").alias(alias));
        return this;
    }

    @Deprecated
    public CharacterResponseProjection secretBackstory() {
        return secretBackstory(null);
    }

    public CharacterResponseProjection secretBackstory(String alias) {
        fields.add(new GraphQLResponseField("secretBackstory").alias(alias));
        return this;
    }

    public CharacterResponseProjection onHuman(HumanResponseProjection subProjection) {
        return onHuman(null, subProjection);
    }

    public CharacterResponseProjection onHuman(String alias, HumanResponseProjection subProjection) {
        fields.add(new GraphQLResponseField("...on Human").alias(alias).projection(subProjection));
        return this;
    }

    public CharacterResponseProjection onDroid(DroidResponseProjection subProjection) {
        return onDroid(null, subProjection);
    }

    public CharacterResponseProjection onDroid(String alias, DroidResponseProjection subProjection) {
        fields.add(new GraphQLResponseField("...on Droid").alias(alias).projection(subProjection));
        return this;
    }


}
