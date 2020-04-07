package io.github.kobylynskyi.product.model;

public class ProductNotFoundException extends Exception {

    public ProductNotFoundException(String id) {
        super(String.format("Product with id '%s' does not exist", id));
    }

}
