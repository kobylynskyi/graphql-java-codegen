package io.github.kobylynskyi.product.graphql.mappers;

import io.github.kobylynskyi.product.graphql.model.ProductInputTO;
import io.github.kobylynskyi.product.graphql.model.ProductTO;
import io.github.kobylynskyi.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductTO map(Product from);

    @Mapping(target = "id", ignore = true) // auto-generated
    @Mapping(target = "addedDateTime", ignore = true)
        // set in the service
    Product mapInput(ProductInputTO from);

}
