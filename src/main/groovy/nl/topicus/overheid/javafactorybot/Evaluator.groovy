package nl.topicus.overheid.javafactorybot

import nl.topicus.overheid.javafactorybot.exception.EvaluationException

/**
 * An evaluator takes a factory and the user specified overrides and yields the evaluated values of the attributes.
 */
class Evaluator {
    private Factory factory
    private Map<String, Object> overrides
    private Map<String, Object> cache

    Evaluator(Factory factory, Map<String, Object> overrides) {
        this.factory = factory
        this.overrides = overrides
        this.cache = new HashMap<>()
    }

    /**
     * Returns a map of all evaluated attributes. These values are based on the defined fields in the factory and
     * possible user specified overrides.
     * @return The evaluated values of of all attributes and associations.
     */
    def attributes() {
        // Make sure cache is filled with values
        (overrides.keySet() + factory.attributes.keySet()).each {
            // For each unevaluated value, evaluate the value
            if (!cache.containsKey(it)) {
                evaluate(it)
            }
        }
        cache
    }

    /**
     * Evaluates the attribute with the given name. Discards any value already in the cache.
     * @param name The name of the attribute to evaluate.
     * @return The evaluated value.
     */
    private def evaluate(String name) {
        def attribute = factory.attributes.get(name)
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
}
