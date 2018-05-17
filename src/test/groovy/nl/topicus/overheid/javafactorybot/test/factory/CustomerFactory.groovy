package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.test.model.Customer

import java.security.MessageDigest
import java.util.concurrent.TimeUnit

@Singleton
class CustomerFactory extends Factory<Customer> {
    Map<String, Attribute> attributes = [
            firstName   : attribute { faker.name().firstName() },
            lastName    : attribute { faker.name().lastName() },
            emailAddress: attribute { "${get("firstName")}.${get("lastName")}@${faker.internet().domainName()}.com" },
            // Future feature: afterprocessor like attribute { faker.internet().password }, process: { MessageDigest.getInstance("SHA-1").digest(it)}
            password    : attribute { MessageDigest.getInstance("SHA-1").digest(faker.internet().password().bytes) },
            creationDate: attribute { faker.date().past(100, TimeUnit.DAYS) },
            address     : hasOne(AddressFactory),
            orders      : hasMany(OrderFactory, faker.number().numberBetween(1, 5)),
            wishList    : hasOne(WishListFactory)
    ]
}
