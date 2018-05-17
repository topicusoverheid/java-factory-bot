package nl.topicus.overheid.javafactorybot.test.model;

import lombok.Data;

import java.util.List;

@Data
public class WishList {
    private Customer customer;
    private List<Product> products;
}
