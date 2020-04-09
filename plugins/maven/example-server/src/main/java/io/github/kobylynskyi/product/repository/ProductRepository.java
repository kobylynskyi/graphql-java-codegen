package io.github.kobylynskyi.product.repository;

import io.github.kobylynskyi.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

public interface ProductRepository extends MongoRepository<Product, String> {

    @Override
    Collection<Product> findAllById(Iterable<String> ids);
}
