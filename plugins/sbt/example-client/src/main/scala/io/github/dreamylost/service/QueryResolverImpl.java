package io.github.dreamylost.service;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import io.github.dreamylost.Config;
import io.github.dreamylost.api.QueryResolver;
import io.github.dreamylost.model.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.util.List;

/**
 * @author liguobin@growingio.com
 * @version 1.0, 2020/7/16
 */
public class QueryResolverImpl implements QueryResolver {

    @Override
    public List<HumanEntity> humans() throws Exception {
        HumansQueryRequest humanQueryRequest = new HumansQueryRequest();
        HumanResponseProjection humanResponseProjection = new HumanResponseProjection();
        humanResponseProjection.id().name();

        GraphQLRequest graphQLRequest = new GraphQLRequest(humanQueryRequest, humanResponseProjection);
        HumansQueryResponse result = Config.restTemplate.exchange(URI.create(Config.productUrl), HttpMethod.POST, Config.httpEntity(graphQLRequest), HumansQueryResponse.class).getBody();

        assert result != null;
        return result.humans();
    }

    @Override
    public DroidEntity droid(String id) {
        DroidQueryRequest productByIdQueryRequest = new DroidQueryRequest();
        productByIdQueryRequest.setId(id);

        DroidResponseProjection droidResponseProjection = new DroidResponseProjection();
        droidResponseProjection.id().name();

        GraphQLRequest graphQLRequest = new GraphQLRequest(productByIdQueryRequest, droidResponseProjection);
        DroidQueryResponse result = Config.restTemplate.exchange(URI.create(Config.productUrl), HttpMethod.POST, Config.httpEntity(graphQLRequest), DroidQueryResponse.class).getBody();

        return result.droid();
    }


    @Override
    public CharacterEntity hero(EpisodeEntity episode) throws Exception {
        HeroQueryRequest heroQueryRequest = new HeroQueryRequest();
        heroQueryRequest.setEpisode(episode);


        CharacterResponseProjection characterResponseProjection = new CharacterResponseProjection();
        characterResponseProjection.id().name().friends(new CharacterResponseProjection().id()).appearsIn();

        GraphQLRequest graphQLRequest = new GraphQLRequest(heroQueryRequest, characterResponseProjection);
        ParameterizedTypeReference<HeroQueryResponse> p = new ParameterizedTypeReference<HeroQueryResponse>() {
        };
        HeroQueryResponse result = Config.restTemplate.exchange(URI.create(Config.productUrl), HttpMethod.POST, Config.httpEntity(graphQLRequest), p).getBody();

        assert result != null;
        return result.hero();

    }

    @Override
    public HumanEntity human(String id) throws Exception {
        HumanQueryRequest humanQueryRequest = new HumanQueryRequest();
        humanQueryRequest.setId(id);

        HumanResponseProjection humanResponseProjection = new HumanResponseProjection();
        //not support email, because start by main instance of spring
        humanResponseProjection.id().name();

        GraphQLRequest graphQLRequest = new GraphQLRequest(humanQueryRequest, humanResponseProjection);
        HumanQueryResponse result = Config.restTemplate.exchange(URI.create(Config.productUrl), HttpMethod.POST, Config.httpEntity(graphQLRequest), HumanQueryResponse.class).getBody();

        assert result != null;
        return result.human();
    }

}