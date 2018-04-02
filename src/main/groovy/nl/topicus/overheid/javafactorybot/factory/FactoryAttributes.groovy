package nl.topicus.overheid.javafactorybot.factory

import com.github.javafaker.Faker
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import nl.topicus.overheid.javafactorybot.BaseFactory
import nl.topicus.overheid.javafactorybot.Evaluator
import nl.topicus.overheid.javafactorybot.FactoryManager
import nl.topicus.overheid.javafactorybot.definition.Association
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.definition.ManyAssociation
import nl.topicus.overheid.javafactorybot.definition.ValueAttribute

trait FactoryAttributes {
    /**
     * Map containing {@link nl.topicus.overheid.javafactorybot.definition.Attribute}s of this factory. An {@link nl.topicus.overheid.javafactorybot.definition.Attribute} is a definition of an attribute in the
     * (generated) object, and minimally implements a function which, given an instance of {@link nl.topicus.overheid.javafactorybot.Evaluator}, yields the
     * value this attribute should have in the generated object.
     *
     * @return A map containing the {@link nl.topicus.overheid.javafactorybot.definition.Attribute}s of this factory.
     */
    Map<String, Attribute> attributes = [:]

    /**
     * Creates a new attribute which either uses the user specified override or the result of the closure.
     *
     * @param defaultValueGenerator The closure which generates a value when no override is given.
     * @return An attribute which resolves to the override or the result of the closure.
     */
    ValueAttribute attribute(@DelegatesTo(Evaluator) @ClosureParams(value = SimpleType, options = "Evaluator") Closure defaultValueGenerator) {
        new ValueAttribute(defaultValueGenerator)
    }

    def <T> Association<T> hasOne(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass) {
        hasOne(FactoryManager.instance.getFactoryInstance(factoryClass))
    }

    def <T> Association<T> hasOne(BaseFactory<T, ? extends Faker> factory) {
        new Association<>(factory)
    }

    def <T> Association<T> hasOne(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass, T defaultObject) {
        hasOne(FactoryManager.instance.getFactoryInstance(factoryClass), defaultObject)
    }

    def <T> Association<T> hasOne(BaseFactory<T, ? extends Faker> factory, T defaultObject) {
        new Association<>(factory, defaultObject)
    }

    def <T> Association<T> hasOne(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass, Map<String, Object> overrides, List<String> traits = null) {
        hasOne(FactoryManager.instance.getFactoryInstance(factoryClass), overrides, traits)
    }

    def <T> Association<T> hasOne(BaseFactory<T, ? extends Faker> factory, Map<String, Object> overrides, List<String> traits = null) {
        new Association<>(factory, overrides, traits)
    }

    // Special version for groovy syntax
    def <T> Association<T> hasOne(Map<String, Object> overrides, Class<? extends BaseFactory<T, ? extends Faker>> factoryClass, List<String> traits = null) {
        hasOne(factoryClass, overrides, traits)
    }

    // Special version for groovy syntax
    def <T> Association<T> hasOne(Map<String, Object> overrides, BaseFactory<T, ? extends Faker> factory, List<String> traits = null) {
        hasOne(factory, overrides, traits)
    }

    def <T> ManyAssociation<T> hasMany(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass, int amount = 0, Map<String, Object> overrides = null, List<String> traits = null) {
        hasMany(FactoryManager.instance.getFactoryInstance(factoryClass), amount, overrides, traits)
    }

    def <T> ManyAssociation<T> hasMany(BaseFactory<T, ? extends Faker> factory, int amount = 0, Map<String, Object> overrides = null, List<String> traits = null) {
        new ManyAssociation<>(factory, amount, overrides, traits)
    }

    // Special version for groovy syntax
    def <T> ManyAssociation<T> hasMany(Map<String, Object> overrides, Class<? extends BaseFactory<T, ? extends Faker>> factoryClass, int amount = 0, List<String> traits = null) {
        hasMany(factoryClass, amount, overrides, traits)
    }

    // Special version for groovy syntax
    def <T> ManyAssociation<T> hasMany(Map<String, Object> overrides, BaseFactory<T, ? extends Faker> factory, int amount = 0, List<String> traits = null) {
        hasMany(factory, amount, overrides, traits)
    }

    def <T> ManyAssociation<T> hasMany(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass, List<Object> defaultOverrides, List<String> traits = null) {
        hasMany(FactoryManager.instance.getFactoryInstance(factoryClass), defaultOverrides, traits)
    }

    def <T> ManyAssociation<T> hasMany(BaseFactory<T, ? extends Faker> factory, List<Object> defaultOverrides, List<String> traits = null) {
        new ManyAssociation<>(factory, defaultOverrides, traits)
    }
}
