package nl.topicus.overheid.javafactorybot.test.model;

import lombok.Data;

@Data
public class Address {
    private Long id;
    private String street;
    private String houseNumber;
    private String zipCode;
    private String city;
    private String country;
}
