package nl.topicus.overheid.javafactorybot.test

import nl.topicus.overheid.javafactorybot.test.factory.AddressFactory
import nl.topicus.overheid.javafactorybot.test.model.Address
import spock.lang.Specification

class AttributesTest extends Specification {

    def "it generates attribute values"() {
        when:
        Address address = AddressFactory.instance.build()

        then:
        address.street != null
        address.houseNumber != null
        address.zipCode != null
        address.city != null
        address.country != null
        address.id == null
    }

    def "it allows to override attribute values"() {
        when:
        Address address = AddressFactory.instance.build(street: "5th Avenue", houseNumber: "123")

        then:
        address.street == "5th Avenue"
        address.houseNumber == "123"
        address.zipCode != null
        address.city != null
        address.country != null
        address.id == null

        when:
        address = AddressFactory.instance.build([street: "Main street", houseNumber: null, id: 2])

        then:
        address.street == "Main street"
        address.houseNumber == null
        address.zipCode != null
        address.city != null
        address.country != null
        address.id == 2
    }
}
