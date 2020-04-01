package io.github.kobylynskyi.bikeshop.graphql.model;

import java.util.*;

public class BikeInputTO {

    private BikeTypeTO type;
    private String brand;
    private String size;
    private Integer year;
    private String price;

    public BikeInputTO() {
    }

    public BikeTypeTO getType() {
        return type;
    }
    public void setType(BikeTypeTO type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }

    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

}