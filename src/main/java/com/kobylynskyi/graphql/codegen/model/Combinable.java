package com.kobylynskyi.graphql.codegen.model;

/**
 * The interface Combinable.
 *
 * @param <T> the type parameter
 * @author valinha
 */
@FunctionalInterface
public interface Combinable<T> {
    /**
     * Combine with source.
     *
     * @param source the source
     */
    void combine(T source);
}
