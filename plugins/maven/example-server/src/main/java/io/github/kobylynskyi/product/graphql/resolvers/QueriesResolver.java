package io.github.kobylynskyi.product.graphql.resolvers;

import io.github.kobylynskyi.product.graphql.api.Query;
import io.github.kobylynskyi.product.graphql.mappers.ProductMapper;
import io.github.kobylynskyi.product.graphql.model.ProductTO;
import io.github.kobylynskyi.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Component
public class QueriesResolver implements Query {

    @Autowired
    private ProductService service;
    @Autowired
    private ProductMapper mapper;

    @Override
    public Collection<ProductTO> products() {
        return service.findAll().stream().map(mapper::map).collect(toList());
    }

    @Override
    public ProductTO productById(String id) throws Exception {
        return mapper.map(service.findById(id));
    }

    @Override
    public Collection<ProductTO> productsByIds(Collection<String> ids) {
        return service.findByIds(ids).stream().map(mapper::map).collect(toList());
    }
}
