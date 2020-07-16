package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;

/**
 * Response projection for Droid
 */
@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-16T14:18:15+0800"
)
public class DroidResponseProjection extends GraphQLResponseProjection {

    public DroidResponseProjection() {
    }

    public DroidResponseProjection id() {
        return id(null);
    }

    public DroidResponseProjection id(String alias) {
        fields.add(new GraphQLResponseField("id").alias(alias));
        return this;
    }

    public DroidResponseProjection name() {
        return name(null);
    }

    public DroidResponseProjection name(String alias) {
        fields.add(new GraphQLResponseField("name").alias(alias));
        return this;
    }

    public DroidResponseProjection friends() {
        return friends(null);
    }

    public DroidResponseProjection friends(String alias) {
        fields.add(new GraphQLResponseField("friends").alias(alias));
        return this;
    }

    public DroidResponseProjection appearsIn() {
        return appearsIn(null);
    }

    public DroidResponseProjection appearsIn(String alias) {
        fields.add(new GraphQLResponseField("appearsIn").alias(alias));
        return this;
    }

    public DroidResponseProjection primaryFunction() {
        return primaryFunction(null);
    }

    public DroidResponseProjection primaryFunction(String alias) {
        fields.add(new GraphQLResponseField("primaryFunction").alias(alias));
        return this;
    }

    @Deprecated
    public DroidResponseProjection secretBackstory() {
        return secretBackstory(null);
    }

    public DroidResponseProjection secretBackstory(String alias) {
        fields.add(new GraphQLResponseField("secretBackstory").alias(alias));
        return this;
    }


}
