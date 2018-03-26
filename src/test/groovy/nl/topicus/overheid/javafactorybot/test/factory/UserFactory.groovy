package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.test.model.User

class UserFactory extends Factory<User> {
    def init() {
        attributes([
                username : attribute { faker.name().username() },
                firstName: attribute { faker.name().firstName() },
                lastName : attribute { faker.name().lastName() },
                email    : attribute { "${get("firstName")}.${get("lastName")}@example.com" }
        ])
    }
}
