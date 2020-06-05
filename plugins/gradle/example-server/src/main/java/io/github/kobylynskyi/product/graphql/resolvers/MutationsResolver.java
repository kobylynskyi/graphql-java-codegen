package io.github.kobylynskyi.product.graphql.resolvers;

import io.github.kobylynskyi.product.graphql.api.CreateMutationResolver;
import io.github.kobylynskyi.product.graphql.mappers.ProductMapper;
import io.github.kobylynskyi.product.graphql.model.ProductInputTO;
import io.github.kobylynskyi.product.graphql.model.ProductTO;
import io.github.kobylynskyi.product.model.Product;
import io.github.kobylynskyi.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MutationsResolver implements CreateMutationResolver {

    @Autowired
    private ProductService service;
    @Autowired
    private ProductMapper mapper;

    @Override
    public ProductTO create(ProductInputTO ProductInputTO) {
        Product savedProduct = service.create(mapper.mapInput(ProductInputTO));
        return mapper.map(savedProduct);
    }
}
