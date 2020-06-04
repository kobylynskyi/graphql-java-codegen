package io.github.kobylynskyi.order.graphql.mappers;

import io.github.kobylynskyi.order.graphql.model.ItemTO;
import io.github.kobylynskyi.order.graphql.model.OrderTO;
import io.github.kobylynskyi.order.model.Item;
import io.github.kobylynskyi.order.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderTO map(Order from);

    ItemTO map(Item from);

}
