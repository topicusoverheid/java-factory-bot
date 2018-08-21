package nl.topicus.overheid.javafactorybot

import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.exception.EvaluationException

/**
 * An evaluator takes a factory and the user specified overrides and yields the evaluated values of the attributes.
 */
class Evaluator {
    private Map<String, Attribute> attributes
    private Map<String, Object> overrides
    List<String> traits
    private Map<String, Object> cache
    private BaseFactory factory

    Evaluator(BaseFactory factory, List<String> traits, Map<String, Object> overrides) {
        this.factory = factory
        this.traits = traits
        this.overrides = overrides
        this.attributes = compileAttributes()
        this.cache = new HashMap<>()
    }

    /**
     * Gets all values for attributes which should be evaluated during the initialize step of the build process.
     *
     * This means that all attributes which are not flagged with afterBuild and all overrides which are not overriding after build attributes
     * should be evaluated.
     */
    Map<String, Object> evaluateForInitialize() {
        Collection<String> activeAttributeKeys = attributes.findAll { !it.value.afterBuild }.keySet()
        if (overrides != null) {
            activeAttributeKeys += (overrides.keySet() - attributes.findAll { it.value.afterBuild }.keySet())
        }
        evaluateForKeys(activeAttributeKeys)
    }

    Map<String, Object> evaluateForFinalize(Object owner) {
        Collection<String> activeAttributeKeys = attributes.findAll { it.value.afterBuild }.keySet()
        evaluateForKeys(activeAttributeKeys, owner)
    }

    Map<String, Object> evaluateForKeys(Collection<String> keys, Object owner = null) {
        keys.inject([:], { Map result, String key -> result.put(key, get(key, owner)); result }) as Map<String, Object>
    }

    /**
     * Returns the evaluated value of a single attribute
     * @param name The name of the attribute to evaluate
     * @param owner The owner of the value of the attribute, if the owner is already build.
     * @return The evaluated value of the attribute
     */
    def get(String name, Object owner = null) {
        if (!cache.containsKey(name)) {
            evaluate(name, owner)
        }

        return cache[name]
    }

    /**
     * Evaluates the attribute with the given name. Discards any value already in the cache.
     * @param name The name of the attribute to evaluate.
     * @param owner The owner of the value of the attribute, if the owner is already build.
     * @return The evaluated value.
     */
    private def evaluate(String name, Object owner = null) {
        def attribute = attributes.get(name)
        if (attribute != null) {
            try {
                cache[name] = overrides != null && overrides.containsKey(name) ? attribute.evaluate(overrides[name], this, owner) : attribute.evaluate(this, owner)
            } catch (Exception e) {
                throw new EvaluationException(this, name, e)
            }
        } else if (overrides != null && overrides.containsKey(name)) {
            cache[name] = overrides[name]
        } else {
            throw new EvaluationException(this, name)
        }
    }

    /**
     * Compile the current list of traits into the base attributes
     *
     * @return A map of attributes including attributes from the traits.
     */
    private Map<String, Attribute> compileAttributes() {
        if (traits != null && !traits.isEmpty()) {
            traits.inject(factory.attributes, { Map attributes, String traitName ->
                attributes + factory.findTrait(traitName).attributes
            }) as Map<String, Attribute>
        } else {
            factory.attributes
        }
    }
}
