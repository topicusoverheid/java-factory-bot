package nl.topicus.overheid.javafactorybot.definition

import com.github.javafaker.Faker
import nl.topicus.overheid.javafactorybot.BaseFactory
import nl.topicus.overheid.javafactorybot.FactoryManager

/**
 * Abstract class for attributes which are based on using another factory.
 */
abstract class AbstractFactoryAttribute<T> {
    private Class<? extends BaseFactory<T, ? extends Faker>> factoryClass
    private BaseFactory<T, ? extends Faker> factory

    /**
     * Create a new instance.
     *
     * @param factory The factory to use for the associated object.
     */
    AbstractFactoryAttribute(BaseFactory<T, ? extends Faker> factory) {
        this.factory = factory
    }

    /**
     * Create a new instance.
     *
     * @param factoryClass The class of the factory to use for the associated object.
     *  The factory itself is lazily initialized using {@link FactoryManager#getFactoryInstance(java.lang.Class)}
     */
    AbstractFactoryAttribute(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass) {
        this.factoryClass = factoryClass
    }

    /**
     * Returns an instance of the factory.
     * This is either the specified instance, or a new instance which is created using {@link FactoryManager#getFactoryInstance(java.lang.Class)}.
     *
     * @return An instance of the factory.
     */
    BaseFactory getFactory() {
        if (factory == null) {
            if (factoryClass != null) {
                factory = FactoryManager.instance.getFactoryInstance(factoryClass)
            } else {
                throw new IllegalArgumentException("Association defined without factory of factoryclass")
            }
        }
        factory
    }
}


