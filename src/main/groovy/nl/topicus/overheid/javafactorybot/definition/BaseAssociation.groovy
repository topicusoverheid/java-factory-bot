package nl.topicus.overheid.javafactorybot.definition

import com.github.javafaker.Faker
import nl.topicus.overheid.javafactorybot.BaseFactory
import nl.topicus.overheid.javafactorybot.FactoryManager

/**
 * Base association which allows to either specify a factory or a factory class to be used as generator for the related
 * object. A factory class can be used to avoid stack overflows caused by circular relations.
 * @param < T > The type of the object which is generated in this association.
 */
abstract class BaseAssociation<T> implements Attribute {
    private BaseFactory<T, ? extends Faker> factory
    private Class<BaseFactory<T, ? extends Faker>> factoryClass

    /**
     * Gets the factory to use for the association. This is either the current factory, or a new instance of the factory class.
     * @return The factory to use for the association.
     */
    BaseFactory<T, ? extends Faker> getFactory() {
        if (factory == null) {
            factory = FactoryManager.instance.getFactoryInstance(factoryClass)
        }
        factory
    }

    BaseAssociation(BaseFactory<T, ? extends Faker> factory) {
        this.factory = factory
    }

    BaseAssociation(Class<BaseFactory<T, ? extends Faker>> factoryClass) {
        this.factoryClass = factoryClass
    }
}
