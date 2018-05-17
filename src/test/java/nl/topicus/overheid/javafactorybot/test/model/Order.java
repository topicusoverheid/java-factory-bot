package nl.topicus.overheid.javafactorybot.test.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Order {
    // Attributes
    private Long id;
    private String reference;
    private Date date;

    // Relations
    private Customer customer;
    private List<ProductOrder> productOrders;
}
