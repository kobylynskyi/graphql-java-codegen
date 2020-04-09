package io.github.kobylynskyi.order.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class Order {

    @Id
    private String id;

    private List<Item> items = new ArrayList<>();

    public BigDecimal getTotal() {
        return getItems().stream()
                .map(Item::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
