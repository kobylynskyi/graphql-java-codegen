package io.github.kobylynskyi.order.external;

import com.kobylynskyi.graphql.codegen.model.request.GraphQLRequest;
import com.kobylynskyi.graphql.codegen.model.request.GraphQLResult;
import graphql.execution.DataFetcherResult;
import io.github.kobylynskyi.order.model.Product;
import io.github.kobylynskyi.product.graphql.model.ProductByIdQueryRequest;
import io.github.kobylynskyi.product.graphql.model.ProductResponseProjection;
import io.github.kobylynskyi.product.graphql.model.ProductTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@Service
public class ProductServiceGraphQLClient {

    @Autowired
    private ExternalProductMapper productMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.service.product.url}")
    private String productUrl;

    public Product getProduct(String productId) {
        ProductByIdQueryRequest getProductRequest = new ProductByIdQueryRequest();
        getProductRequest.setId(productId);
        GraphQLRequest request = new GraphQLRequest(getProductRequest,
                new ProductResponseProjection()
                        .id()
                        .title()
                        .price());

        GraphQLResult<Map<String, ProductTO>> result = restTemplate.exchange(URI.create(productUrl),
                HttpMethod.POST,
                httpEntity(request),
                new ParameterizedTypeReference<GraphQLResult<Map<String, ProductTO>>>() {
                }).getBody();
        return productMapper.map(result.getData().get(getProductRequest.getOperationName()));
    }

    private static HttpEntity<String> httpEntity(Object request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new HttpEntity<>(request.toString(), headers);
    }
}
