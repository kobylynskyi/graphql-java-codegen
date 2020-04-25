package io.github.kobylynskyi.order.external;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult;
import io.github.kobylynskyi.order.model.Product;
import io.github.kobylynskyi.order.model.UnableToCreateProductException;
import io.github.kobylynskyi.order.model.UnableToRetrieveProductException;
import io.github.kobylynskyi.order.model.UnableToRetrieveProductsException;
import io.github.kobylynskyi.product.graphql.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceGraphQLClient {

    @Autowired
    private ExternalProductMapper productMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.service.product.url}")
    private String productUrl;

    public Product getProduct(String productId) throws UnableToRetrieveProductException {
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
                new ParameterizedTypeReference<GraphQLResult<Map<String, ProductTO>>>() {})
                .getBody();
        if (result.hasErrors()) {
            throw new UnableToRetrieveProductException(productId, result.getErrors().get(0).getMessage());
        }
        return productMapper.map(result.getData().get(getProductRequest.getOperationName()));
    }

    public List<Product> getProducts(Collection<String> productIds) throws UnableToRetrieveProductsException {
        ProductsByIdsQueryRequest getProductRequest = new ProductsByIdsQueryRequest();
        getProductRequest.setIds(productIds);
        GraphQLRequest request = new GraphQLRequest(getProductRequest,
                new ProductResponseProjection()
                        .id()
                        .title()
                        .price());

        GraphQLResult<Map<String, List<ProductTO>>> result = restTemplate.exchange(URI.create(productUrl),
                HttpMethod.POST,
                httpEntity(request),
                new ParameterizedTypeReference<GraphQLResult<Map<String, List<ProductTO>>>>() {})
                .getBody();
        if (result.hasErrors()) {
            throw new UnableToRetrieveProductsException(productIds, result.getErrors().get(0).getMessage());
        }
        List<ProductTO> productTOs = result.getData().get(getProductRequest.getOperationName());
        return productTOs.stream().map(productMapper::map).collect(Collectors.toList());
    }

    public Product createProduct(String productTitle, String productSku, BigDecimal productPrice) throws UnableToCreateProductException {
        CreateMutationRequest createProductRequest = new CreateMutationRequest();
        createProductRequest.setProductInput(new ProductInputTO.Builder()
                .setTitle(productTitle)
                .setPrice(productPrice.toString())
                .setSku(productSku)
                .build());
        GraphQLRequest request = new GraphQLRequest(createProductRequest,
                new ProductResponseProjection()
                        .id()
                        .title()
                        .price()
                        .sku());

        GraphQLResult<Map<String, ProductTO>> result = restTemplate.exchange(URI.create(productUrl),
                HttpMethod.POST,
                httpEntity(request),
                new ParameterizedTypeReference<GraphQLResult<Map<String, ProductTO>>>() {})
                .getBody();
        if (result.hasErrors()) {
            throw new UnableToCreateProductException(result.getErrors().get(0).getMessage());
        }
        ProductTO productTO = result.getData().get(createProductRequest.getOperationName());
        return productMapper.map(productTO);
    }

    private static HttpEntity<String> httpEntity(Object request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request.toString(), headers);
    }
}
