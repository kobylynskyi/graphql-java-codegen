package io.github.kobylynskyi.order.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
public class Product {

    @Id
    private String id;
    private String title;
    private BigDecimal price;

}
