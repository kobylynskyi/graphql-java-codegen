package io.github.kobylynskyi.order.graphql.resolvers;

import graphql.kickstart.tools.GraphQLMutationResolver;
import io.github.kobylynskyi.order.graphql.api.AddProductToOrderMutationResolver;
import io.github.kobylynskyi.order.graphql.api.CreateMutationResolver;
import io.github.kobylynskyi.order.graphql.mappers.OrderMapper;
import io.github.kobylynskyi.order.graphql.model.OrderTO;
import io.github.kobylynskyi.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MutationsResolver implements CreateMutationResolver, AddProductToOrderMutationResolver, GraphQLMutationResolver {

    @Autowired
    private OrderService service;
    @Autowired
    private OrderMapper mapper;

    @Override
    public OrderTO create() {
        return mapper.map(service.create());
    }

    @Override
    public OrderTO addProductToOrder(String orderId, String productId, Integer quantity) throws Exception {
        return mapper.map(service.addProduct(orderId, productId, quantity));
    }
}
