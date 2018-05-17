package nl.topicus.overheid.javafactorybot.test.model;

import lombok.Data;

@Data
public class ProductOrder {
    // Attributes
    private Long id;
    private int amount;

    // Relations
    private Order order;
    private Product product;
}
