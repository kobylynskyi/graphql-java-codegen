package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

/**
 * Response projection for Human
 */
@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-17T10:15:09+0800"
)
public class HumanResponseProjection extends GraphQLResponseProjection {

    public HumanResponseProjection() {
    }

    public HumanResponseProjection id() {
        return id(null);
    }

    public HumanResponseProjection id(String alias) {
        fields.add(new GraphQLResponseField("id").alias(alias));
        return this;
    }

    public HumanResponseProjection name() {
        return name(null);
    }

    public HumanResponseProjection name(String alias) {
        fields.add(new GraphQLResponseField("name").alias(alias));
        return this;
    }

    public HumanResponseProjection friends(CharacterResponseProjection subProjection) {
        return friends(null, subProjection);
    }

    public HumanResponseProjection friends(String alias, CharacterResponseProjection subProjection) {
        fields.add(new GraphQLResponseField("friends").alias(alias).projection(subProjection));
        return this;
    }

    public HumanResponseProjection appearsIn() {
        return appearsIn(null);
    }

    public HumanResponseProjection appearsIn(String alias) {
        fields.add(new GraphQLResponseField("appearsIn").alias(alias));
        return this;
    }

    public HumanResponseProjection homePlanet() {
        return homePlanet(null);
    }

    public HumanResponseProjection homePlanet(String alias) {
        fields.add(new GraphQLResponseField("homePlanet").alias(alias));
        return this;
    }

    @Deprecated
    public HumanResponseProjection secretBackstory() {
        return secretBackstory(null);
    }

    public HumanResponseProjection secretBackstory(String alias) {
        fields.add(new GraphQLResponseField("secretBackstory").alias(alias));
        return this;
    }

    public HumanResponseProjection email() {
        return email(null);
    }

    public HumanResponseProjection email(String alias) {
        fields.add(new GraphQLResponseField("email").alias(alias));
        return this;
    }


}
