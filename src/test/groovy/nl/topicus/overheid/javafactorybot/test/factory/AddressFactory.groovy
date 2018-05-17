package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.test.model.Address

@Singleton
class AddressFactory extends Factory<Address> {
    Map<String, Attribute> attributes = [
            street     : attribute { faker.address().streetName() },
            houseNumber: attribute { faker.address().buildingNumber() },
            zipCode    : attribute { faker.address().zipCode() },
            city       : attribute { faker.address().city() },
            country    : attribute { faker.address().country() }
    ]
}
