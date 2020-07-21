package io.github.kobylynskyi.order.external.product;

import io.github.kobylynskyi.order.model.Product;
import io.github.kobylynskyi.product.graphql.model.ProductTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExternalProductMapper {

    Product map(ProductTO from);

}
