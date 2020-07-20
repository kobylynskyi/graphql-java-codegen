package io.github.kobylynskyi.order.external.starwars;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import io.github.kobylynskyi.starwars.graphql.Character;
import io.github.kobylynskyi.starwars.graphql.CharacterResponseProjection;
import io.github.kobylynskyi.starwars.graphql.DroidResponseProjection;
import io.github.kobylynskyi.starwars.graphql.Episode;
import io.github.kobylynskyi.starwars.graphql.HeroesQueryRequest;
import io.github.kobylynskyi.starwars.graphql.HeroesQueryResponse;
import io.github.kobylynskyi.starwars.graphql.HumanResponseProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Service
public class StarwarsGraphQLClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.service.starwars.url}")
    private String starwarsUrl;

    private static HttpEntity<String> httpEntity(GraphQLRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request.toHttpJsonBody(), headers);
    }

    public List<Character> getCharacters(Episode episode) {
        HeroesQueryRequest heroQueryRequest = new HeroesQueryRequest();
        heroQueryRequest.setEpisode(episode);

        CharacterResponseProjection responseProjection = new CharacterResponseProjection()
                .id()
                .name()
                .onDroid(new DroidResponseProjection()
                        .primaryFunction())
                .onHuman(new HumanResponseProjection()
                        .homePlanet())
                .typename();
        GraphQLRequest graphQLRequest = new GraphQLRequest(heroQueryRequest, responseProjection);

        HeroesQueryResponse result = restTemplate.exchange(URI.create(starwarsUrl),
                HttpMethod.POST,
                httpEntity(graphQLRequest),
                HeroesQueryResponse.class).getBody();
        if (result.hasErrors()) {
            throw new RuntimeException(result.getErrors().get(0).getMessage());
        }
        return result.heroes();
    }
}
