package io.github.kobylynskyi.product.service;

import io.github.kobylynskyi.product.model.Product;
import io.github.kobylynskyi.product.model.ProductNotFoundException;
import io.github.kobylynskyi.product.repository.ProductRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public Collection<Product> findAll() {
        return repository.findAll();
    }

    public Collection<Product> findByIds(Collection<String> ids) {
        return repository.findAllById(ids);
    }

    public Product findById(String id) throws ProductNotFoundException {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product create(@NonNull Product input) {
        input.setAddedDateTime(new Date());
        Product saved = repository.save(input);
        log.info("Created new product: {}", saved);
        return saved;
    }

}
