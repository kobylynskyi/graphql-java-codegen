package io.github.kobylynskyi.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private String productId;
    private int quantity;
    private BigDecimal total;

}
