package nl.topicus.overheid.javafactorybot.dsl

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import nl.topicus.overheid.javafactorybot.Evaluator
import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Association
import nl.topicus.overheid.javafactorybot.definition.ManyAssociation
import nl.topicus.overheid.javafactorybot.definition.ValueAttribute

trait AttributesDsl {
    /**
     * Creates a new attribute which either uses the user specified override or the result of the closure.
     *
     * @param defaultValueGenerator The closure which generates a value when no override is given.
     * @return An attribute which resolves to the override or the result of the closure.
     */
    ValueAttribute attribute(@DelegatesTo(Evaluator) @ClosureParams(value = SimpleType, options = "Evaluator") Closure defaultValueGenerator) {
        new ValueAttribute(defaultValueGenerator)
    }

    def <T> Association<T> hasOne(Factory<T> factory) {
        new Association<>(factory)
    }

    def <T> Association<T> hasOne(Factory<T> factory, T defaultObject) {
        new Association<>(factory, defaultObject)
    }

    def <T> Association<T> hasOne(Factory<T> factory, Map<String, Object> overrides, List<String> traits = null) {
        new Association<>(factory, overrides, traits)
    }

    // Special version for groovy syntax
    def <T> Association<T> hasOne(Map<String, Object> overrides, Factory<T> factory, List<String> traits = null) {
        new Association<>(factory, overrides, traits)
    }

    def <T> ManyAssociation<T> hasMany(Factory<T> factory, int amount, Map<String, Object> overrides = null, List<String> traits = null) {
        new ManyAssociation<>(factory, amount, overrides, traits)
    }

    // Special version for groovy syntax
    def <T> ManyAssociation<T> hasMany(Map<String, Object> overrides, Factory<T> factory, int amount, List<String> traits = null) {
        new ManyAssociation<>(factory, amount, overrides, traits)
    }

    def <T> ManyAssociation<T> hasMany(Factory<T> factory, List<Object> defaultOverrides, List<String> traits = null) {
        new ManyAssociation<>(factory, defaultOverrides, traits)
    }
}
