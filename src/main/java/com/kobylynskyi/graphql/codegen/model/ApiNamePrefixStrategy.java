package com.kobylynskyi.graphql.codegen.model;

public enum ApiNamePrefixStrategy {

    /**
     * Will take GraphQL file name as a prefix for all generated API interfaces + value of apiNamePrefix config option.
     * e.g.:
     * following schemas: resources/schemas/order-service.graphql, resources/schemas/product-service.graphql
     * will result in:    OrderServiceQueryResolver.java, ProductServiceQueryResolver.java, etc
     */
    FILE_NAME_AS_PREFIX,

    /**
     * Will take parent folder name as a prefix for all generated API interfaces + value of apiNamePrefix config option.
     * e.g.:
     * following schemas: resources/order-service/schema1.graphql, resources/order-service/schema2.graphql
     * will result in:    OrderServiceQueryResolver.java, OrderServiceGetOrderByIdQueryResolver.java, etc
     */
    FOLDER_NAME_AS_PREFIX,

    /**
     * Will take only the value of apiNamePrefix config option.
     */
    CONSTANT;

}
