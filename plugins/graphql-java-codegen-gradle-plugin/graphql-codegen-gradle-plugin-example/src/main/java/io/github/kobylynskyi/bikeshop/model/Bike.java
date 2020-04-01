package io.github.kobylynskyi.bikeshop.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Bike {

    @Id
    private String id;
    private BikeType type;
    private String brand;
    private String size;
    private Integer year;
    private BigDecimal price;
    private Date addedDateTime;
}
