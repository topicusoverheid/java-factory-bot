package nl.topicus.overheid.javafactorybot

import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.exception.EvaluationException

/**
 * An evaluator takes a factory and the user specified overrides and yields the evaluated values of the attributes.
 */
class Evaluator {
    private Map<String, Attribute> attributes
    private Map<String, Object> overrides
    private List<String> traits
    private Map<String, Object> cache
    private BaseFactory factory

    Evaluator(BaseFactory factory, List<String> traits, Map<String, Object> overrides) {
        this.factory = factory
        this.traits = traits
        this.overrides = overrides
        this.attributes = compileAttributes()
        this.cache = new HashMap<>()
    }

    Map<String, Object> evaluateForBuildPhase(FactoryPhase buildPhase) {
        def result

        Map<String, Attribute> activeAttributes = attributes.findAll { it.value.activePhase == buildPhase }

        switch (buildPhase) {
            case FactoryPhase.INIT:
                result = evaluateForKeys(overrides.keySet() + activeAttributes.keySet())
                break
            case FactoryPhase.FINALIZE:
                result = evaluateForKeys(activeAttributes.keySet())
                break
        }

        result
    }

    Map<String, Object> evaluateForKeys(Collection<String> keys) {
        keys.inject([:], { Map result, String key -> result.put(key, get(key)); result }) as Map<String, Object>
    }

    /**
     * Returns the evaluated value of a single attribute
     * @param name The name of the attribute to evaluate
     * @return The evaluated value of the attribute
     */
    def get(String name) {
        if (!cache.containsKey(name)) {
            evaluate(name)
        }

        return cache[name]
    }

    /**
     * Evaluates the attribute with the given name. Discards any value already in the cache.
     * @param name The name of the attribute to evaluate.
     * @return The evaluated value.
     */
    private def evaluate(String name) {
        def attribute = attributes.get(name)
        if (attribute != null) {
            try {
                cache[name] = overrides.containsKey(name) ? attribute.evaluate(overrides[name], this) : attribute.evaluate(this)
            } catch (Exception e) {
                throw new EvaluationException(this, name, e)
            }
        } else if (overrides.containsKey(name)) {
            cache[name] = overrides[name]
        } else {
            throw new EvaluationException(this, name)
        }
    }

    /**
     * Compile the list of traits into the base attributes
     *
     * @param traits List of traits to apply, can be null or empty.
     * @return A map of attributes including attributes from the traits.
     */
    private Map<String, Attribute> compileAttributes(List<String> traits) {
        if (traits != null && !traits.isEmpty()) {
            traits.inject(factory.attributes, { Map attributes, String traitName ->
                attributes + factory.findTrait(traitName).attributes
            }) as Map<String, Attribute>
        } else {
            factory.attributes
        }
    }
}
