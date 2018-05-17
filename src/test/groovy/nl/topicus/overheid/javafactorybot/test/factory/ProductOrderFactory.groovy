package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.test.model.ProductOrder

@Singleton
class ProductOrderFactory extends Factory<ProductOrder> {
    Map<String, Attribute> attributes = [
            amount : attribute { faker.number().numberBetween(1, 10) },
            order  : hasOne(OrderFactory),
            product: hasOne(ProductFactory),
    ]
}
