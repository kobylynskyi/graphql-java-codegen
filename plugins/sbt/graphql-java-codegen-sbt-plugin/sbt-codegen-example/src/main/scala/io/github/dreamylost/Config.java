package io.github.dreamylost;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * @author liguobin@growingio.com
 * @version 1.0, 2020/7/16
 */
public class Config {

    public static HttpEntity<String> httpEntity(GraphQLRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request.toHttpJsonBody(), headers);
    }

    public static RestTemplate restTemplate = new RestTemplate();
    public static String productUrl = "http://localhost:8080/graphql";
}
