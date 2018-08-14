package nl.topicus.overheid.javafactorybot.factory

import com.github.javafaker.Faker
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import nl.topicus.overheid.javafactorybot.BaseFactory
import nl.topicus.overheid.javafactorybot.Evaluator
import nl.topicus.overheid.javafactorybot.FactoryPhase
import nl.topicus.overheid.javafactorybot.definition.Association
import nl.topicus.overheid.javafactorybot.definition.ManyAssociation
import nl.topicus.overheid.javafactorybot.definition.ValueAttribute

trait FactoryDSL {
    /**
     * Creates a new attribute which either uses the user specified override or the result of the closure.
     *
     * @param defaultValueGenerator The closure which generates a value when no override is given.
     * @return An attribute which resolves to the override or the result of the closure.
     */
    ValueAttribute value(@DelegatesTo(Evaluator) @ClosureParams(value = SimpleType, options = "Evaluator") Closure defaultValueGenerator) {
        new ValueAttribute(defaultValueGenerator)
    }

    def <T> Association<T> hasOne(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass) {
        new Association<>(factoryClass)
    }

    def <T> Association<T> hasOne(BaseFactory<T, ? extends Faker> factory) {
        new Association<>(factory)
    }

//    def <T> Association<T> hasOne(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass,
//                                  Closure<T> defaultObjectProducer) {
//        new Association<>(factoryClass, defaultObjectProducer)
//    }
//
//    def <T> Association<T> hasOne(BaseFactory<T, ? extends Faker> factory,
//                                  Closure<T> defaultObjectProducer) {
//        new Association<>(factory, defaultObjectProducer)
//    }

//
//    def <T> Association<T> hasOne(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass,
//                                  Map<String, ? extends Object> overrides,
//                                  List<String> traits = null) {
//        new Association<>(factoryClass, overrides, traits)
//    }
//
//    def <T> Association<T> hasOne(BaseFactory<T, ? extends Faker> factory,
//                                  Map<String, ? extends Object> overrides,
//                                  List<String> traits = null) {
//        new Association<>(factory, overrides, traits)
//    }
//
//    def <T> Association<T> hasOne(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass,
//                                  List<String> traits) {
//        hasOne(factoryClass, [:], traits)
//    }
//
//    def <T> Association<T> hasOne(BaseFactory<T, ? extends Faker> factory,
//                                  List<String> traits) {
//        hasOne(factory, [:], traits)
//    }

    // Special version for groovy syntax
    def <T> Association<T> hasOne(Map<String, ? extends Object> args,
                                  Class<? extends BaseFactory<T, ? extends Faker>> factoryClass) {
        Association<T> association = new Association(factoryClass)
        parseArgs(association, args)
        association
    }

    // Special version for groovy syntax
    def <T> Association<T> hasOne(Map<String, ? extends Object> args,
                                  BaseFactory<T, ? extends Faker> factory) {
        Association<T> association = new Association(factory)
        parseArgs(association, args)
        association
    }

    def parseArgs(Association association, Map<String, ? extends Object> args) {
        association.defaultObjectProducer = args['default'] as Closure
        def defaultOverrides = args['overrides']
        if (defaultOverrides instanceof Closure) {
            association.defaultOverridesProducer = defaultOverrides
        } else {
            association.defaultOverridesProducer = { defaultOverrides as Map<String, Object> }
        }
        association.traits = args['traits'] as List<String>
        association.activePhase = (args['phase'] ?: FactoryPhase.INIT) as FactoryPhase
        association.inverse = args['inverse'] as String
    }

    def <T> ManyAssociation<T> hasMany(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass,
                                       int amount = 0,
                                       Map<String, ? extends Object> overrides = null,
                                       List<String> traits = null) {
        new ManyAssociation<>(factoryClass, amount, overrides, traits)
    }

    def <T> ManyAssociation<T> hasMany(BaseFactory<T, ? extends Faker> factory,
                                       int amount = 0,
                                       Map<String, ? extends Object> overrides = null,
                                       List<String> traits = null) {
        new ManyAssociation<>(factory, amount, overrides, traits)
    }

    // Special version for groovy syntax
    def <T> ManyAssociation<T> hasMany(Map<String, ? extends Object> overrides,
                                       Class<? extends BaseFactory<T, ? extends Faker>> factoryClass,
                                       int amount = 0,
                                       List<String> traits = null) {
        hasMany(factoryClass, amount, overrides, traits)
    }

    // Special version for groovy syntax
    def <T> ManyAssociation<T> hasMany(Map<String, ? extends Object> overrides,
                                       BaseFactory<T, ? extends Faker> factory,
                                       int amount = 0,
                                       List<String> traits = null) {
        hasMany(factory, amount, overrides, traits)
    }

    def <T> ManyAssociation<T> hasMany(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass,
                                       List<Object> defaultOverrides,
                                       List<String> traits = null) {
        new ManyAssociation<>(factoryClass, defaultOverrides, traits)
    }

    def <T> ManyAssociation<T> hasMany(BaseFactory<T, ? extends Faker> factory,
                                       List<Object> defaultOverrides,
                                       List<String> traits = null) {
        new ManyAssociation<>(factory, defaultOverrides, traits)
    }
}
