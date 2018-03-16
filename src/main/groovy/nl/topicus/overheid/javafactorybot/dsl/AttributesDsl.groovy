package nl.topicus.overheid.javafactorybot.dsl

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import nl.topicus.overheid.javafactorybot.Evaluator
import nl.topicus.overheid.javafactorybot.Factory
import nl.topicus.overheid.javafactorybot.definition.Association
import nl.topicus.overheid.javafactorybot.definition.ManyAssociation
import nl.topicus.overheid.javafactorybot.definition.ValueAttribute

trait AttributesDsl {
    static ValueAttribute attribute(
            @DelegatesTo(Evaluator) @ClosureParams(value = SimpleType, options = "Evaluator") Closure defaultValueGenerator) {
        new ValueAttribute(defaultValueGenerator)
    }

    static <T> Association<T> hasOne(Factory<T> factory) {
        new Association<T>(factory)
    }

    static <T> Association<T> hasOne(Factory<T> factory, T defaultObject) {
        new Association<T>(factory, defaultObject)
    }

    static <T> Association<T> hasOne(Factory<T> factory, Map<String, Object> overrides, List<String> traits = null) {
        new Association<T>(factory, overrides, traits)
    }

    // Special version for groovy syntax
    static <T> Association<T> hasOne(Map<String, Object> overrides, Factory<T> factory, List<String> traits = null) {
        new Association<T>(factory, overrides, traits)
    }

    static <T> ManyAssociation<T> hasMany(Factory<T> factory, int amount, Map<String, Object> overrides = null, List<String> traits = null) {
        new ManyAssociation<T>(factory, amount, overrides, traits)
    }

    // Special version for groovy syntax
    static <T> ManyAssociation<T> hasMany(Map<String, Object> overrides, Factory<T> factory, int amount, List<String> traits = null) {
        new ManyAssociation<T>(factory, amount, overrides, traits)
    }

    static <T> ManyAssociation<T> hasMany(Factory<T> factory, List<Object> defaultOverrides, List<String> traits = null) {
        new ManyAssociation<T>(factory, defaultOverrides, traits)
    }
}
