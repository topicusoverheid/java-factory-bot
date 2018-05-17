package nl.topicus.overheid.javafactorybot.test.model;

import lombok.Data;

import java.util.Date;

@Data
public class Customer {
    // Attributes
    private Long id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String password;
    private Date creationDate;

    // Relations
    private Address address;
    private WishList wishList;
}
