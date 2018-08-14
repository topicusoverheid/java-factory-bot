package nl.topicus.overheid.javafactorybot.factory

import com.github.javafaker.Faker
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import nl.topicus.overheid.javafactorybot.BaseFactory
import nl.topicus.overheid.javafactorybot.Evaluator
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

    def <T> Association<T> hasOne(Map<String, ? extends Object> args, Class<? extends BaseFactory<T, ? extends Faker>> factoryClass) {
        Association<T> association = new Association(factoryClass)
        parseHasOneArgs(association, args)
        association
    }

    def <T> Association<T> hasOne(Map<String, ? extends Object> args, BaseFactory<T, ? extends Faker> factory) {
        Association<T> association = new Association(factory)
        parseHasOneArgs(association, args)
        association
    }

    def <T> ManyAssociation<T> hasMany(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass) {
        new ManyAssociation(factoryClass)
    }

    def <T> ManyAssociation<T> hasMany(BaseFactory<T, ? extends Faker> factory) {
        new ManyAssociation(factory)
    }

    // Special version for groovy syntax
    def <T> ManyAssociation<T> hasMany(Map<String, ? extends Object> args, Class<? extends BaseFactory<T, ? extends Faker>> factoryClass) {
        def association = new ManyAssociation(factoryClass)
        parseHasManyArgs(association, args)
        association
    }

    // Special version for groovy syntax
    def <T> ManyAssociation<T> hasMany(Map<String, ? extends Object> args, BaseFactory<T, ? extends Faker> factory) {
        def association = new ManyAssociation(factory)
        parseHasManyArgs(association, args)
        association
    }

    private def parseHasOneArgs(Association association, Map<String, ? extends Object> args) {
        association.defaultObjectProducer = args['default'] as Closure
        def defaultOverrides = args['overrides']
        if (defaultOverrides instanceof Closure) {
            association.defaultOverridesProducer = defaultOverrides
        } else {
            association.defaultOverridesProducer = defaultOverrides ? { defaultOverrides as Map<String, Object> } : null
        }
        association.traits = args['traits'] as List<String>
        association.afterBuild = (args['afterBuild'] ?: false) as boolean
    }

    private def parseHasManyArgs(ManyAssociation association, Map<String, ? extends Object> args) {
        association.overrides = args['overrides'] as List
        def generalOverrides = args['generalOverrides']
        if (generalOverrides instanceof Closure) {
            association.generalOverridesProvider = generalOverrides
        } else {
            association.generalOverridesProvider = generalOverrides ? { generalOverrides as Map<String, Object> } : null
        }
        association.traits = args['traits'] as List<String>
        association.afterBuild = (args['afterBuild'] ?: false) as boolean
        association.amount = (args['amount'] ?: 0) as int
    }
}
