package io.github.kobylynskyi.order.external.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequests;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponse;
import io.github.kobylynskyi.order.model.Product;
import io.github.kobylynskyi.order.model.UnableToCreateProductException;
import io.github.kobylynskyi.order.model.UnableToRetrieveProductException;
import io.github.kobylynskyi.order.model.UnableToRetrieveProductsException;
import io.github.kobylynskyi.product.graphql.model.CreateMutationRequest;
import io.github.kobylynskyi.product.graphql.model.CreateMutationResponse;
import io.github.kobylynskyi.product.graphql.model.ProductByIdQueryRequest;
import io.github.kobylynskyi.product.graphql.model.ProductByIdQueryResponse;
import io.github.kobylynskyi.product.graphql.model.ProductInputTO;
import io.github.kobylynskyi.product.graphql.model.ProductResponseProjection;
import io.github.kobylynskyi.product.graphql.model.ProductTO;
import io.github.kobylynskyi.product.graphql.model.ProductsByIdsQueryRequest;
import io.github.kobylynskyi.product.graphql.model.ProductsByIdsQueryResponse;
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
import java.util.stream.Collectors;

@Service
public class ProductServiceGraphQLClient {

    @Autowired
    private ExternalProductMapper productMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${external.service.product.url}")
    private String productUrl;

    public Product getProduct(String productId) throws UnableToRetrieveProductException {
        ProductByIdQueryRequest productByIdQueryRequest = new ProductByIdQueryRequest();
        productByIdQueryRequest.setId(productId);
        ProductResponseProjection responseProjection = new ProductResponseProjection()
                .id()
                .title()
                .price();
        GraphQLRequest graphQLRequest = new GraphQLRequest(productByIdQueryRequest, responseProjection);

        ProductByIdQueryResponse result = restTemplate.exchange(URI.create(productUrl),
                HttpMethod.POST,
                httpEntity(graphQLRequest),
                ProductByIdQueryResponse.class).getBody();
        if (result.hasErrors()) {
            throw new UnableToRetrieveProductException(productId, result.getErrors().get(0).getMessage());
        }
        return productMapper.map(result.productById());
    }

    public List<Product> getProducts(List<String> productIds) throws UnableToRetrieveProductsException {
        ProductsByIdsQueryRequest getProductRequest = new ProductsByIdsQueryRequest();
        getProductRequest.setIds(productIds);
        ProductResponseProjection responseProjection = new ProductResponseProjection()
                .id()
                .title()
                .price();
        GraphQLRequest request = new GraphQLRequest(getProductRequest, responseProjection);

        ProductsByIdsQueryResponse result = restTemplate.exchange(URI.create(productUrl),
                HttpMethod.POST,
                httpEntity(request),
                ProductsByIdsQueryResponse.class).getBody();
        if (result.hasErrors()) {
            throw new UnableToRetrieveProductsException(productIds, result.getErrors().get(0).getMessage());
        }
        return result.productsByIds().stream().map(productMapper::map).collect(Collectors.toList());
    }

    public Product createProduct(ProductInput input) throws UnableToCreateProductException {
        CreateMutationRequest createProductRequest = new CreateMutationRequest();
        createProductRequest.setProductInput(mapInputTO(input));
        GraphQLRequest request = new GraphQLRequest(createProductRequest,
                new ProductResponseProjection().id().title().price().sku());

        CreateMutationResponse result = restTemplate.exchange(URI.create(productUrl),
                HttpMethod.POST,
                httpEntity(request),
                CreateMutationResponse.class).getBody();
        if (result.hasErrors()) {
            throw new UnableToCreateProductException(result.getErrors().get(0).getMessage());
        }
        ProductTO productTO = result.create();
        return productMapper.map(productTO);
    }

    public List<Product> createMultipleProduct(List<ProductInput> inputs) throws UnableToCreateProductException {
        GraphQLRequests requests = new GraphQLRequests();
        for (ProductInput input : inputs) {
            CreateMutationRequest createProductRequest = new CreateMutationRequest("create" + input.getSku());
            createProductRequest.setProductInput(mapInputTO(input));
            requests.addRequest(new GraphQLRequest(createProductRequest,
                    new ProductResponseProjection().id().title().price().sku()));
        }

        GraphQLResponse result = restTemplate.exchange(URI.create(productUrl),
                HttpMethod.POST,
                httpEntity(requests),
                GraphQLResponse.class).getBody();
        if (result.hasErrors()) {
            throw new UnableToCreateProductException(result.getErrors().get(0).getMessage());
        }
        return result.getData().values().stream()
                .map(o -> objectMapper.convertValue(o, ProductTO.class))
                .map(productMapper::map)
                .collect(Collectors.toList());
    }

    public ProductInputTO mapInputTO(ProductInput input) {
        return new ProductInputTO.Builder()
                .setTitle(input.getTitle())
                .setPrice(input.getPrice().toString())
                .setSku(input.getSku())
                .build();
    }

    private static HttpEntity<String> httpEntity(GraphQLRequest request) {
        return new HttpEntity<>(request.toHttpJsonBody(), getHttpHeaders());
    }

    private static HttpEntity<String> httpEntity(GraphQLRequests request) {
        return new HttpEntity<>(request.toHttpJsonBody(), getHttpHeaders());
    }

    private static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
