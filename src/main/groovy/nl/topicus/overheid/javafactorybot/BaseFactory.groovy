package nl.topicus.overheid.javafactorybot

import com.github.javafaker.Faker
import nl.topicus.overheid.javafactorybot.definition.Attribute
import nl.topicus.overheid.javafactorybot.definition.Definition
import nl.topicus.overheid.javafactorybot.exception.TraitNotFoundException

import java.lang.reflect.ParameterizedType

/**
 * A factory is a special class which is able to generate new valid objects, for testing purposes.
 * These objects can be randomized by using a faker.
 *
 * @param < M >          The type of the generated object
 * @param < F >          The type of the faker of this factory. This allows to override the faker with a custom implementation.
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
     * Returns a map of attributes and relations based on the specified default attribute, default relations and build parameters.
     *
     * @param overrides The overrides specified to override default attributes and/or relations.
     * @param traits A list of traits to apply.
     * @return A map of attributes which can be used to create a new instance.
     */
    Map<String, Object> buildAttributes(Map<String, Object> overrides, List<String> traits = null) {
        Evaluator evaluator = new Evaluator(this, compileAttributes(traits), overrides)
        applyAfterAttributesHooks(evaluator.attributes())
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
        createIfInContext(applyAfterBuildHooks(object))
    }

    /**
     * Returns a new instance that is not saved.
     * <p>
     * In normal usage, this method should not be overriden. If you want to change how the object is built, use
     * {@link #onAfterBuild} or {@link #internalBuild}.
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
     * {@link #onAfterBuild} or {@link #internalBuild}.
     *
     * @param overrides Additional overrides to use when building a new object.
     * Build parameters allow to define custom values for attributes and relations.
     * @param traits A list of traits to apply to new object. A trait is basically a collection of attribute/relation
     * updates, meant to create an object representing a certain state. The possible traits are specified in the factory.
     * @return The new instance.
     */
    M build(Map<String, Object> overrides, List<String> traits = null) {
        M object = internalBuild(buildAttributes(overrides, traits))
        createIfInContext(applyAfterBuildHooks(object, traits))
    }

    /**
     * Returns a new instance that is not saved.
     * <p>
     * In normal usage, this method should not be overriden. If you want to change how the object is built, use
     * {@link #onAfterBuild} or {@link #internalBuild}.
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
     * {@link #onAfterBuild} or {@link #internalBuild}.
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

    /**
     * Actual builder of the object, which creates the instance using the computed attributes.
     * This method is not used when {@link #build(M)} is called.
     *
     * @param attributes The computed attributes of the object
     * @return A object with the values from the given attributes
     */
    protected M internalBuild(Map attributes) {
        getObjectType().newInstance(attributes)
    }

    /**
     * Callback which will persist the object, given the current context specifies the persist strategy. This method
     * is called after the object is completely build, just after {@link BaseFactory#onAfterBuild(java.lang.Object)}.
     *
     * @param object The built object. Can be null
     * @param context The context which should be used to persist the object.
     * @return The persisted object.
     */
    protected M internalCreate(M object, FactoryContext context) {
        if (object) context.persist(object) else object
    }

    // Private methods down here

    /**
     * If create context is active, persist object. Otherwise, use unpersisted object.
     */
    private M createIfInContext(M object) {
        def context = FactoryManager.instance.currentContext
        context == null ? object : applyAfterCreateHooks(internalCreate(object, context))
    }

    /**
     * Compile the list of traits into the base attributes
     *
     * @param traits List of traits to apply, can be null or empty.
     * @return A map of attributes including attributes from the traits.
     */
    private Map<String, Attribute> compileAttributes(List<String> traits) {
        if (traits != null && !traits.isEmpty()) {
            traits.inject(attributes, { Map attributes, String traitName ->
                attributes + findTrait(traitName).attributes
            }) as Map<String, Attribute>
        } else {
            attributes
        }
    }

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
     * Apply all 'afterCreate' hooks from factory and possible traits.
     */
    private M applyAfterCreateHooks(M object, List<String> traits = null) {
        onAfterCreate(object)
        if (traits) traits.each { findTrait(it).onAfterCreate(object) }
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