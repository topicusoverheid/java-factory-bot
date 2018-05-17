package nl.topicus.overheid.javafactorybot.test.model;

import lombok.Data;

@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private Double price;
}
