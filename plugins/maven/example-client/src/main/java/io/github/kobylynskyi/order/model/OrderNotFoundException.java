package io.github.kobylynskyi.order.model;

public class OrderNotFoundException extends Exception {

    public OrderNotFoundException(String id) {
        super(String.format("Order with id '%s' does not exist", id));
    }

}
