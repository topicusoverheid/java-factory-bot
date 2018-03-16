package nl.topicus.overheid.javafactorybot.definition

import com.github.javafaker.Faker
import nl.topicus.overheid.javafactorybot.Evaluator
import nl.topicus.overheid.javafactorybot.BaseFactory

/**
 * Attribute used to define an association with another object, using a factory. A combination of the default overrides,
 * default object, traits and user specified overrides is used to create the associated object using the factory.
 * @param < T > The type of the associated object.
 */
class Association<T> implements Attribute {
    private BaseFactory factory
    private Map<String, Object> defaultOverrides
    private boolean withDefaultObject = false
    private T defaultObject
    private List<String> traits

    /**
     * Create a new Association which combines user specified overrides with optional default overrides and traits.
     * @param factory The factory to use for the associated object.
     * @param defaultOverrides Default overrides to pass to the factory. Can be overriden by user specified overrides.
     * @param traits List of traits to apply to the associated object.
     */
    Association(BaseFactory<T, ? extends Faker> factory, Map<String, Object> defaultOverrides = null, List<String> traits = null) {
        this.defaultOverrides = defaultOverrides
        this.traits = traits
        this.factory = factory
    }

    /**
     * Create a new Association which uses user specified overrides or, in absence of these, uses the given object.
     * @param factory The factory to use for the associated object.
     * @param defaultObject The default object to be used when no overrides are given.
     */
    Association(BaseFactory<T, ? extends Faker> factory, T defaultObject) {
        this.withDefaultObject = true
        this.defaultObject = defaultObject
        this.factory = factory
    }

    @Override
    def evaluate(Evaluator evaluator) {
        if (defaultOverrides != null) {
            // Build using the default overrides
            factory.build(defaultOverrides, traits)
        } else if (withDefaultObject) {
            // Build using the default object
            factory.build(defaultObject)
        } else {
            // Default build
            factory.build()
        }
    }

    @Override
    def evaluate(Object override, Evaluator evaluator) {
        if (override == null || override instanceof T) {
            factory.build((T) override)
        } else if (override instanceof Map) {
            // override given as map, use these together with default overrides to build the object
            factory.build(defaultOverrides ? defaultOverrides + override : override, traits)
        } else {
            throw new IllegalArgumentException("Override should be null, a Map or an object of the associated type")
        }
    }
}
