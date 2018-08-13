package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.test.model.User

class UserFactory extends Factory<User> {
    @Override
    Map<String, Attribute> getAttributes() {
        [
                username : value { faker.name().username() },
                firstName: value { faker.name().firstName() },
                lastName : value { faker.name().lastName() },
                email    : value { "${get("firstName")}.${get("lastName")}@example.com" }
        ]
    }
}
