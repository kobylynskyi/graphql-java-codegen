package io.github.kobylynskyi.order.repository;

import io.github.kobylynskyi.order.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {

}
