package nl.topicus.overheid.javafactorybot.definition

import com.github.javafaker.Faker
import nl.topicus.overheid.javafactorybot.BaseFactory
import nl.topicus.overheid.javafactorybot.Evaluator
import nl.topicus.overheid.javafactorybot.FactoryManager
/**
 * Attribute used to define an association with another object, using a factory. A combination of the default overrides,
 * default object, traits and user specified overrides is used to create the associated object using the factory.
 * @param < T >    The type of the associated object.
 */
class Association<T> extends AbstractFactoryAttribute<T> implements Attribute{
    Closure<Map<String, Object>> defaultOverridesProducer
    Closure<T> defaultObjectProducer
    List<String> traits
    boolean afterBuild = false

    /**
     * Create a new Association which combines user specified overrides with optional default overrides and traits.
     * @param factory The factory to use for the associated object.
     * @param defaultOverrides Default overrides to pass to the factory. Can be overriden by user specified overrides.
     * @param traits List of traits to apply to the associated object.
     */
    Association(BaseFactory<T, ? extends Faker> factory) {
        super(factory)
    }

    /**
     * Create a new Association which combines user specified overrides with optional default overrides and traits.
     * @param factoryClass The class of the factory to use for the associated object.
     * The factory itself is lazily initialized using {@link FactoryManager#getFactoryInstance(java.lang.Class)}
     * @param defaultOverrides Default overrides to pass to the factory. Can be overriden by user specified overrides.
     * @param traits List of traits to apply to the associated object.
     */
    Association(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass) {
        super(factoryClass)
    }

    @Override
    def evaluate(Evaluator evaluator, Object owner) {
        if (defaultOverridesProducer != null) {
            // Build using the default overrides
            getFactory().build(defaultOverridesProducer(owner), traits)
        } else if (defaultObjectProducer != null) {
            // Build using the default object
            getFactory().build(defaultObjectProducer())
        } else {
            // Default build
            getFactory().build()
        }
    }

    @Override
    def evaluate(Object override, Evaluator evaluator, Object owner) {
        if (override == null || override instanceof T) {
            getFactory().build((T) override)
        } else if (override instanceof Map) {
            // override given as map, use these together with default overrides to build the object
            getFactory().build(defaultOverridesProducer ? defaultOverridesProducer(owner) + override : override, traits)
        } else {
            throw new IllegalArgumentException("Override should be null, a Map or an object of the associated type")
        }
    }
}
