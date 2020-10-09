package io.github.kobylynskyi.order.external.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInput {

    private String title;
    private String sku;
    private BigDecimal price;

}
