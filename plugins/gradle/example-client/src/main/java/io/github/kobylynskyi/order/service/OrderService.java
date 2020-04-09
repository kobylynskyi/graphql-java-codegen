package io.github.kobylynskyi.order.service;

import io.github.kobylynskyi.order.external.ProductServiceGraphQLClient;
import io.github.kobylynskyi.order.model.*;
import io.github.kobylynskyi.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private ProductServiceGraphQLClient productService;

    public Collection<Order> getOrders() {
        return repository.findAll();
    }

    public Order getOrderById(String id) throws OrderNotFoundException {
        return repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    public Order create() {
        Order saved = repository.save(new Order());
        log.info("Created new order: {}", saved);
        return saved;
    }

    public Order addProduct(String orderId, String productId, int quantity) throws OrderNotFoundException, UnableToRetrieveProductException {
        Order order = getOrderById(orderId);

        Product product = productService.getProduct(productId);
        Item item = order.getItems().stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst()
                .orElseGet(() -> {
                    Item newItem = new Item(productId, 0, BigDecimal.ZERO);
                    order.getItems().add(newItem);
                    return newItem;
                });
        item.setQuantity(item.getQuantity() + quantity);
        item.setTotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        repository.save(order);
        log.info("Added product [id: {}] to the order [id: {}]", product.getId(), order.getId());
        return order;
    }

}
