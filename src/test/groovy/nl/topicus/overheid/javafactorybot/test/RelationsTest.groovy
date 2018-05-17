package nl.topicus.overheid.javafactorybot.test

import nl.topicus.overheid.javafactorybot.test.factory.CustomerFactory
import nl.topicus.overheid.javafactorybot.test.model.*
import spock.lang.Specification

class RelationsTest extends Specification {

    def "it generates attribute and relations values"() {
        when:
        Customer customer = CustomerFactory.instance.build()

        then:
        customer.firstName != null
        customer.lastName != null
        customer.emailAddress != null
        customer.emailAddress.startsWith("${customer.firstName}.${customer.lastName}")
        customer.password != null
        customer.creationDate != null
        customer.id == null

        and:
        customer.address != null
        customer.address instanceof Address
        customer.wishList != null
        customer.wishList instanceof WishList
        customer.wishList.products != null
        customer.wishList.products.size() == 3
        customer.wishList.products.each {
            assert it instanceof Product
        }
        customer.orders.size() > 0
        customer.orders.size() <= 5
        customer.orders.each {
            assert it instanceof Order
            assert it.productOrders != null
        }
    }
}
