package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.test.model.Product

@Singleton
class ProductFactory extends Factory<Product> {
    Map<String, Attribute> attributes = [
            name       : attribute { faker.commerce().productName() },
            description: attribute { faker.harryPotter().quote() },
            brand      : attribute { faker.company().name() },
            price      : attribute { faker.number().randomDouble(2, 1, 100) }
    ]
}
