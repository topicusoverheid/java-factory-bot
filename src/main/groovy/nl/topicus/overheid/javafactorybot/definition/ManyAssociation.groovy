package nl.topicus.overheid.javafactorybot.definition

import com.github.javafaker.Faker
import nl.topicus.overheid.javafactorybot.BaseFactory
import nl.topicus.overheid.javafactorybot.Evaluator
import nl.topicus.overheid.javafactorybot.FactoryPhase

/**
 * Attribute used to define an association with a list of objects, using a factory.
 * A combination of the default overrides, default object, traits and user specified overrides is used to create the
 * associated object using the factory.
 * @param < T >  The type of the associated object.
 */
class ManyAssociation<T> extends AbstractFactoryAttribute<T> implements Attribute {
    private Map<String, Object> defaultItemOverrides
    private List<Object> defaultOverrides
    private int amount
    private List<String> traits

    FactoryPhase activePhase = FactoryPhase.INIT

    /**
     * Create a new Association which combines user specified overrides with optional default overrides and traits.
     * @param factory The factory to use for the associated object.
     * @param defaultOverrides Default overrides to pass to the factory. Can be overriden by user specified overrides.
     * @param traits List of traits to apply to the associated object.
     */
    ManyAssociation(BaseFactory<T, ? extends Faker> factory, int amount, Map<String, Object> defaultItemOverrides = null, List<String> traits = null) {
        super(factory)
        this.amount = amount
        this.defaultItemOverrides = defaultItemOverrides
        this.traits = traits
    }

    /**
     * Create a new Association which combines user specified overrides with optional default overrides and traits.
     * @param factory The factory to use for the associated object.
     * @param defaultOverrides Default overrides to pass to the factory. Can be overriden by user specified overrides.
     * @param traits List of traits to apply to the associated object.
     */
    ManyAssociation(BaseFactory<T, ? extends Faker> factory, List<Object> defaultOverrides, List<String> traits = null) {
        super(factory)
        this.amount = defaultOverrides.size()
        this.defaultOverrides = defaultOverrides
        this.traits = traits
    }

    /**
     * Create a new Association which combines user specified overrides with optional default overrides and traits.
     * @param factoryClass The class of the factory to use for the associated object.
     * The factory itself is lazily initialized using {@link nl.topicus.overheid.javafactorybot.FactoryManager#getFactoryInstance(java.lang.Class)}.
     * @param defaultOverrides Default overrides to pass to the factory. Can be overriden by user specified overrides.
     * @param traits List of traits to apply to the associated object.
     */
    ManyAssociation(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass, int amount, Map<String, Object> defaultItemOverrides = null, List<String> traits = null) {
        super(factoryClass)
        this.amount = amount
        this.defaultItemOverrides = defaultItemOverrides
        this.traits = traits
    }

    /**
     * Create a new Association which combines user specified overrides with optional default overrides and traits.
     * @param factoryClass The class of the factory to use for the associated object.
     * The factory itself is lazily initialized using {@link nl.topicus.overheid.javafactorybot.FactoryManager#getFactoryInstance(java.lang.Class)}.
     * @param defaultOverrides Default overrides to pass to the factory. Can be overriden by user specified overrides.
     * @param traits List of traits to apply to the associated object.
     */
    ManyAssociation(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass, List<Object> defaultOverrides, List<String> traits = null) {
        super(factoryClass)
        this.amount = defaultOverrides.size()
        this.defaultOverrides = defaultOverrides
        this.traits = traits
    }

    @Override
    def evaluate(Evaluator evaluator, Object owner) {
        if (defaultOverrides != null) {
            getFactory().buildList(defaultOverrides)
        } else if (defaultItemOverrides != null) {
            getFactory().buildList(amount, defaultItemOverrides)
        } else {
            getFactory().buildList(amount)
        }
    }

    @Override
    def evaluate(Object override, Evaluator evaluator, Object owner) {
        if (override instanceof List) {
            getFactory().buildList(compileListOverride(override))
        } else if (override instanceof Integer) {
            // Build the given amount of object
            getFactory().buildList(override)
        } else {
            throw new IllegalArgumentException("Override for a toMany association should be an integer (amount) " +
                    "or a list containing individual overrides/objects. " +
                    "Instead, an instance of type ${override.class.name} was received.")
        }
    }

    List<Object> compileListOverride(List override) {
        // A list is given. Each element in the list should be either a map with overrides, or an object (or null)
        // If it is a map, we merge it with the default overrides, just as we do with single associations
        override.collect { it instanceof Map && defaultItemOverrides ? defaultItemOverrides + it : it }
    }

}
