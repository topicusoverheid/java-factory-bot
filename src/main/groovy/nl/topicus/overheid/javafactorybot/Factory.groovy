package nl.topicus.overheid.javafactorybot

import com.github.javafaker.Faker

/**
 * A factory is a special class which is able to generate new valid objects, for testing purposes.
 * These objects can be randomized by using a faker. This factory class uses the default faker from https://github.com/DiUS/java-faker.
 *
 * @param < M >   The type of the generated object
 */
abstract class Factory<M> extends BaseFactory<M, Faker> {
    protected final Faker faker = new Faker()

    Faker getFaker() {
        faker
    }
}
