package io.github.kobylynskyi.order.model;

public class UnableToRetrieveProductException extends Exception {

    public UnableToRetrieveProductException(String id, String message) {
        super(String.format("Unable to retrieve product with id '%s': %s", id, message));
    }

}
