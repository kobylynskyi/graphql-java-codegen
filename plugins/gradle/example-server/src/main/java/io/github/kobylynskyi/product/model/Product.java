package io.github.kobylynskyi.product.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Product {

    @Id
    private String id;
    private String title;
    private String description;
    private BigDecimal price;
    private String sku;
    private StockStatus stockStatus;
    private Date addedDateTime;

}
