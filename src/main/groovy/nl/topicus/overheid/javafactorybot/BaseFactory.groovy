package nl.topicus.overheid.javafactorybot

import com.github.javafaker.Faker
import nl.topicus.overheid.javafactorybot.definition.Definition
import nl.topicus.overheid.javafactorybot.exception.TraitNotFoundException

import java.lang.reflect.ParameterizedType

/**
 * A factory is a special class which is able to generate new valid objects, for testing purposes.
 * These objects can be randomized by using a faker.
 *
 * @param < M >                      The type of the generated object
 * @param < F >                      The type of the faker of this factory. This allows to override the faker with a custom implementation.
 */
abstract class BaseFactory<M, F extends Faker> extends Definition<M> {
    /**
     * A Faker instance which can be used to generate random attribute values.
     *
     * @link https://github.com/DiUS/java-faker
     */
    abstract F getFaker()

    /**
     * Returns the type of the object which is created by this factory. By default, this method returns the type
     * specified as generic type of this factory.
     *
     * @return The type of the object which is created by this factory.
     */
    Class<M> getObjectType() {
        // Reflection magic to determine the type of the generated object
        // Based on https://stackoverflow.com/questions/1901164/get-type-of-a-generic-parameter-in-java-with-reflection
        try {
            ((ParameterizedType) this.class.genericSuperclass).actualTypeArguments[0] as Class<M>
        } catch (ClassCastException e) {
            // This exception explains better what you did wrong
            throw new IllegalStateException("Object type of ${this.class.simpleName} could not be determined. Did you forget to add a type parameter to the factory?", e)
        }
    }

    /**
     * Returns the passed object.
     * <p>
     * This method exists so it is possible to completely override a relation by passing your own instance, or null.
     *
     * @param object The custom instance of the object.
     * @return The passed object
     */
    M build(M object) {
        // This check is required. When creating an empty map in groovy, it does not conform to the type Map<String, Object>. Due to this, the
        // map is passed to this method. To handle this situation, the correct method is used when a map is given.
        if (object instanceof Map) {
            build(object as Map, [])
        } else {
            persist(object, FactoryManager.instance.currentContext)
        }
    }

    /**
     * Returns a new instance that is not saved.
     * <p>
     * In normal usage, this method should not be overriden. If you want to change how the object is built, use
     * {@link #onAfterBuild} or {@link #construct}.
     *
     * @return The new instance.
     */
    M build() {
        build([:])
    }

    /**
     * Returns a new instance that is not saved.
     * <p>
     * In normal usage, this method should not be overriden. If you want to change how the object is built, use
     * {@link #onAfterBuild} or {@link #construct}.
     *
     * @param overrides Additional overrides to use when building a new object.
     * Build parameters allow to define custom values for attributes and relations.
     * @param traits A list of traits to apply to new object. A trait is basically a collection of attribute/relation
     * updates, meant to create an object representing a certain state. The possible traits are specified in the factory.
     * @return The new instance.
     */
    M build(Map<String, Object> overrides, List<String> traits = null) {
        def evaluator = new Evaluator(this, traits, overrides)
        combine(
                persist(
                        finalize(
                                construct(
                                        initialize(evaluator)
                                ),
                                evaluator
                        ),
                        FactoryManager.instance.currentContext
                ),
                evaluator
        )
    }

    /**
     * Returns a new instance that is not saved.
     * <p>
     * In normal usage, this method should not be overriden. If you want to change how the object is built, use
     * {@link #onAfterBuild} or {@link #construct}.
     *
     * @param traits A list of traits to apply to new object. A trait is basically a collection of attribute/relation
     * updates, meant to create an object representing a certain state. The possible traits are specified in the factory.
     * @return The new instance.
     */
    M build(List<String> traits) {
        build([:], traits)
    }

    /**
     * Returns a new instance that is not saved.
     * <p>
     * In normal usage, this method should not be overriden. If you want to change how the object is built, use
     * {@link #onAfterBuild} or {@link #construct}.
     *
     * @param traits An array of traits to apply to new object. A trait is basically a collection of attribute/relation
     * updates, meant to create an object representing a certain state. The possible traits are specified in the factory.
     * @return The new instance.
     */
    M build(String... traits) {
        build([:], traits.toList())
    }

    /**
     * Returns the passed object, after it is saved.
     * <p>
     * This method exists so it is possible to completely override a relation by passing your own instance, or null.
     * <p>
     * To persist the object, the current context (@link FactoryManager#currentContext} is temporarily set to {@link FactoryManager#createContext}.
     * This context specifies the strategy to use to persist the object.
     *
     * @param object The custom instance of the object.
     * @return The passed object, after is saved.
     */
    M create(M object) {
        doInCreateContext { build(object) }
    }

    /**
     * Returns a new instance that is saved.
     * <p>
     * To persist the object, the current context (@link FactoryManager#currentContext} is temporarily set to {@link FactoryManager#createContext}.
     * This context specifies the strategy to use to persist the object.
     *
     * @return The new saved instance.
     */
    M create() {
        doInCreateContext { build() }
    }

    /**
     * Returns a new instance that is saved.
     * <p>
     * To persist the object, the current context (@link FactoryManager#currentContext} is temporarily set to {@link FactoryManager#createContext}.
     * This context specifies the strategy to use to persist the object..
     *
     * @param overrides Additional overrides to use when building a new object.
     * Build parameters allow to define custom values for attributes and relations.
     * @param traits A list of traits to apply to new object. A trait is basically a collection of attribute/relation
     * updates, meant to create an object representing a certain state. The possible traits are specified in the factory.
     * @return The new saved instance.
     */
    M create(Map overrides, List<String> traits = null) {
        doInCreateContext { build(overrides, traits) }
    }

    /**
     * Returns a new instance that is saved.
     * <p>
     * To persist the object, the current context (@link FactoryManager#currentContext} is temporarily set to {@link FactoryManager#createContext}.
     * This context specifies the strategy to use to persist the object..
     *
     * @param traits A list of traits to apply to new object. A trait is basically a collection of attribute/relation
     * updates, meant to create an object representing a certain state. The possible traits are specified in the factory.
     * @return The new saved instance.
     */
    M create(List<String> traits) {
        doInCreateContext { build(traits) }
    }

    /**
     * Returns a new instance that is saved.
     * <p>
     * To persist the object, the current context (@link FactoryManager#currentContext} is temporarily set to {@link FactoryManager#createContext}.
     * This context specifies the strategy to use to persist the object..
     *
     * @param traits An array of traits to apply to new object. A trait is basically a collection of attribute/relation
     * updates, meant to create an object representing a certain state. The possible traits are specified in the factory.
     * @return The new saved instance.
     */
    M create(String... traits) {
        doInCreateContext { build(traits) }
    }

    /**
     * Returns a list of new instances that are not saved.
     *
     * @param amount The amount of instances to build.
     * @param overrides Additional overrides to use when building a new object.
     * @return As list of new instances.
     */
    List<M> buildList(int amount, Map overrides = null) {
        List<M> result = new ArrayList<>(amount)
        amount.times { result.add(overrides == null ? build() : build(overrides)) }
        result
    }

    /**
     * Returns a list of new instances that are not saved.
     *
     * @param overrides A list of additional overrides.
     * Each element in the list is applied to {@link #build}.
     * So the size of the result is the same as the amount of elements in this list.
     * @return As list of new instances.
     */
    List<M> buildList(List overrides) {
        overrides.collect({ (M) this.build(it) })
    }

    /**
     * Returns a list of new instances that are saved.
     *
     * @param amount The amount of instances to build.
     * @param overrides Additional overrides to use when building a new object.
     * @return As list of new saved instances.
     */
    List<M> createList(int amount, Map overrides) {
        doInCreateContext { buildList(amount, overrides) }
    }

    /**
     * Returns a list of new instances that are saved.
     *
     * @param overrides A list of additional overrides.
     * Each element in the list is applied to {@link #build}.
     * So the size of the result is the same as the amount of elements in this list.
     * @return As list of new saved instances.
     */
    List<M> createList(List overrides) {
        doInCreateContext { buildList(overrides) }
    }

    // Build process steps down here

    /**
     * First step of the build process. Takes the evaluator containing the factory and user specified overrides, and
     * outputs a map of attribute names to values which should be used to construct the object.
     *
     * In this step, all onAfterAttribute hooks are called.
     *
     * @param evaluator The evaluator to use in this step.
     * @return A map containing values of attributes to be used for constructing the boject.
     */
    protected Map<String, Object> initialize(Evaluator evaluator) {
        applyAfterAttributesHooks(evaluator.evaluateForInitialize(), evaluator.traits)
    }

    /**
     * Second step of build process. Actual builder of the object, which creates the instance using the computed attribute values
     * from the first step {@link #initialize(nl.topicus.overheid.javafactorybot.Evaluator)}.
     *
     * This method is not used when {@link #build(M)} is called.
     *
     * @param attributes The computed attribute values of the object
     * @return A object with the values from the given attributes
     */
    protected M construct(Map attributes) {
        getObjectType().newInstance(attributes)
    }

    /**
     * Third step of the build process. After the object is constructed, it is time for some fine-tuning before the object is
     * persisted (if we are creating the object) or returned as result.
     *
     * In this step, all onAfterBuild hooks are called.
     *
     * @param object The result of the second step ({@link #construct(java.util.Map)}
     * @param evaluator The evalutor to use in this step.
     * @return An object which is ready to be persisted or returned as result.
     */
    protected M finalize(M object, Evaluator evaluator) {
        applyAfterBuildHooks(object, evaluator?.traits)
    }

    /**
     * Fourth step of the build process. If the object is build within a create context, this step will persist the object
     * to the data store. Otherwise, this step does nothing.
     *
     * @param object The object which should be persisted. Can be null
     * @param context The context which should be used to persist the object.
     * @return The persisted object of the same object.
     */
    protected M persist(M object, FactoryContext context = null) {
        (context != null && object != null) ? context.persist(object) : object
    }

    /**
     * The fifth step of the build process. Some attribute values can only be created after the owner object is build. In this step,
     * these attributes are evaluated and combined with the object.
     *
     * @param object The build (and persisted) object
     * @param evaluator The evalutor to use in this step.
     * @return The object with additional attributes .
     */
    protected M combine(M object, Evaluator evaluator) {
        Map<String, Object> attrs = evaluator.evaluateForFinalize(object)
        attrs.each { key, value -> object."$key" = value }
        object
    }

    // Private methods down here

    /**
     * Find a trait by name, and throw an exception if a trait with that name does not exist.
     */
    protected Definition<M> findTrait(String traitName) {
        Definition<M> traitDefinition = getTraits()?.get(traitName)
        if (traitDefinition == null) throw new TraitNotFoundException(this, traitName)
        traitDefinition
    }

    /**
     * Apply all 'afterAttributes' hooks from factory and possible traits.
     */
    private Map<String, Object> applyAfterAttributesHooks(Map<String, Object> attributes, List<String> traits = null) {
        onAfterAttributes(attributes)
        if (traits) traits.each { findTrait(it).onAfterAttributes(attributes) }
        attributes
    }

    /**
     * Apply all 'afterBuild' hooks from factory and possible traits.
     */
    private M applyAfterBuildHooks(M object, List<String> traits = null) {
        onAfterBuild(object)
        if (traits) traits.each { findTrait(it).onAfterBuild(object) }
        object
    }

    /**
     * Execute the given closure in create context and return the result.
     */
    private static <T> T doInCreateContext(Closure<T> closure) {
        FactoryManager.instance.enableCreateContext()
        def result = closure()
        FactoryManager.instance.disableCreateContext()
        result
    }
}