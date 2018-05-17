package nl.topicus.overheid.javafactorybot.test.factory

import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.test.model.Order

import java.util.concurrent.TimeUnit

@Singleton
class OrderFactory extends Factory<Order> {
    Map<String, Attribute> attributes = [
            reference    : attribute { faker.lorem().characters(16) },
            date         : attribute { faker.date().past(10, TimeUnit.DAYS) },
            productOrders: hasMany(ProductOrderFactory, 5, [order: null]/*, composition: true*/) // Future feature
    ]

    @Override
    void onAfterBuild(Order order) {
        order?.productOrders?.each { it.order = order }
    }
}
