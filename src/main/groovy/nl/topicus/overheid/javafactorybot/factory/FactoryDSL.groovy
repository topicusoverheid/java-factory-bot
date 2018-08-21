package nl.topicus.overheid.javafactorybot.factory

import com.github.javafaker.Faker
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import nl.topicus.overheid.javafactorybot.BaseFactory
import nl.topicus.overheid.javafactorybot.Evaluator
import nl.topicus.overheid.javafactorybot.definition.Association
import nl.topicus.overheid.javafactorybot.definition.ManyAssociation
import nl.topicus.overheid.javafactorybot.definition.ValueAttribute

/**
 * Trait containing methods which aid in defining a factory.
 */
trait FactoryDSL {
    /**
     * Defines an attribute which either uses the user specified override or the result of the closure.
     *
     * @param defaultValueGenerator The closure which generates a value when no override is given.
     * @return An attribute which resolves to the override or the result of the closure.
     */
    ValueAttribute value(@DelegatesTo(Evaluator) @ClosureParams(value = SimpleType, options = "Evaluator") Closure defaultValueGenerator) {
        new ValueAttribute(defaultValueGenerator)
    }

    /**
     * Defines a relationship with a single object.
     *
     * @param factoryClass The type of the factory which should be used to generate the related object.
     * @return An association capable of generating the related object.
     */
    def <T> Association<T> hasOne(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass) {
        new Association<>(factoryClass)
    }

    /**
     * Defines a relationship with a single object.
     *
     * @param factory The factory which should be used to generate the related object.
     * @return An association capable of generating the related object.
     */
    def <T> Association<T> hasOne(BaseFactory<T, ? extends Faker> factory) {
        new Association<>(factory)
    }

    /**
     * Defines a relationship with a single object.
     *
     * @param options A map containing options for the relationship. Possible options:
     * <ul>
     *     <li>default : {@link Closure} - A closure which yields the default object to be used.</li>
     *     <li>overrides : {@link Map}|{@link Closure} - A map of default overrides, or a closure yielding the default overrides.
     *     Combined with the user specified overrides, they form the overrides given to the factory. If this relationship is evaluated after the main
     *     object, the closure is called with the created owner.</li>
     *     <li>traits : {@link List} - A list of names of traits to apply by default</li>
     *     <li>afterBuild : {@link Boolean} - Determines if this relationship is evaluated before the object is constructed, or after
     *     it is created. Defaults to false.</li>
     * </ul>
     * @param factoryClass The type of the factory which should be used to generate the related object.
     * @return An association capable of generating the related object.
     */
    def <T> Association<T> hasOne(Map<String, ? extends Object> options, Class<? extends BaseFactory<T, ? extends Faker>> factoryClass) {
        Association<T> association = new Association(factoryClass)
        parseHasOneOptions(association, options)
        association
    }

    /**
     * Defines a relationship with a single object.
     *
     * @param options A map containing options for the relationship. Possible options:
     * <ul>
     *     <li>default : {@link Closure} - A closure which yields the default object to be used.</li>
     *     <li>overrides : {@link Map}|{@link Closure} - A map of default overrides, or a closure yielding the default overrides.
     *     Combined with the user specified overrides, they form the overrides given to the factory. If this relationship is evaluated after the main
     *     object, the closure is called with the created owner.</li>
     *     <li>traits : {@link List} - A list of names of traits to apply by default</li>
     *     <li>afterBuild : {@link Boolean} - Determines if this relationship is evaluated before the object is constructed, or after
     *     it is created. Defaults to false.</li>
     * </ul>
     * @param factory The factory which should be used to generate the related object.
     * @return An association capable of generating the related object.
     */
    def <T> Association<T> hasOne(Map<String, ? extends Object> options, BaseFactory<T, ? extends Faker> factory) {
        Association<T> association = new Association(factory)
        parseHasOneOptions(association, options)
        association
    }

    /**
     * Defines a relationship with multiple objects.
     *
     * @param factoryClass The type of the factory which should be used to generate the related objects.
     * @return An association capable of generating the related objects.
     */
    def <T> ManyAssociation<T> hasMany(Class<? extends BaseFactory<T, ? extends Faker>> factoryClass) {
        new ManyAssociation(factoryClass)
    }

    /**
     * Defines a relationship with multiple objects.
     *
     * @param factory The factory which should be used to generate the related objects.
     * @return An association capable of generating the related objects.
     */
    def <T> ManyAssociation<T> hasMany(BaseFactory<T, ? extends Faker> factory) {
        new ManyAssociation(factory)
    }

    /**
     * Defines a relationship with multiple objects.
     *
     * @param options A map containing options for the relationship. Possible options:
     * <ul>
     *     <li>overrides : {@link List} - A list containing overrides (maps) or the related objects which should be used by the factory.</li>
     *     <li>generalOverrides : {@link Map}|{@link Closure} - A map of default overrides, or a closure yielding the default overrides.
     *     Combined with the user specified overrides, they form the overrides given to the factory. If this relationship is evaluated after the main
     *     object, the closure is called with the created owner.</li>
     *     <li>traits : {@link List} - A list of names of traits to apply by default</li>
     *     <li>afterBuild : {@link Boolean} - Determines if this relationship is evaluated before the object is constructed, or after
     *     it is created. Defaults to false.</li>
     *     <li>amount : {@link Integer} - The number of related objects to generate, if no other amount or list of overrides is specified.</li>
     *     <li>transform : {@link Closure} - A closure which gets the generated list of related objects and returns a transformed collection.
     *     This can be useful if the final collection should not be a list but any other type, like a set.</li>
     * </ul>
     * @param factoryClass The type of the factory which should be used to generate the related objects.
     * @return An association capable of generating the related objects.
     */
    def <T> ManyAssociation<T> hasMany(Map<String, ? extends Object> options, Class<? extends BaseFactory<T, ? extends Faker>> factoryClass) {
        def association = new ManyAssociation(factoryClass)
        parseHasManyOptions(association, options)
        association
    }

    /**
     * Defines a relationship with multiple objects.
     *
     * @param options A map containing options for the relationship. Possible options:
     * <ul>
     *     <li>overrides : {@link List} - A list containing overrides (maps) or the related objects which should be used by the factory.</li>
     *     <li>generalOverrides : {@link Map}|{@link Closure} - A map of default overrides, or a closure yielding the default overrides.
     *     Combined with the user specified overrides, they form the overrides given to the factory. If this relationship is evaluated after the main
     *     object, the closure is called with the created owner.</li>
     *     <li>traits : {@link List} - A list of names of traits to apply by default</li>
     *     <li>afterBuild : {@link Boolean} - Determines if this relationship is evaluated before the object is constructed, or after
     *     it is created. Defaults to false.</li>
     *     <li>amount : {@link Integer} - The number of related objects to generate, if no other amount or list of overrides is specified.</li>
     *     <li>transform : {@link Closure} - A closure which gets the generated list of related objects and returns a transformed collection.
     *     This can be useful if the final collection should not be a list but any other type, like a set.</li>
     * </ul>
     * @param factory The factory which should be used to generate the related objects.
     * @return An association capable of generating the related objects.
     */
    def <T> ManyAssociation<T> hasMany(Map<String, ? extends Object> options, BaseFactory<T, ? extends Faker> factory) {
        def association = new ManyAssociation(factory)
        parseHasManyOptions(association, options)
        association
    }

    private def parseHasOneOptions(Association association, Map<String, ? extends Object> args) {
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

    private def parseHasManyOptions(ManyAssociation association, Map<String, ? extends Object> args) {
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
        association.transform = args['transform'] as Closure
    }
}