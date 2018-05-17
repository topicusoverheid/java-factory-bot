package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.test.model.WishList

@Singleton
class WishListFactory extends Factory<WishList> {
    Map<String, Attribute> attributes = [
            customer: hasOne(CustomerFactory),
            products: hasMany(ProductFactory, 3)
    ]
}
