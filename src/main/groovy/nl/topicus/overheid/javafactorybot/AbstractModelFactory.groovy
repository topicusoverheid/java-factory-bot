package nl.topicus.overheid.javafactorybot

import com.github.javafaker.Faker

import java.lang.reflect.ParameterizedType

/**
 * A factory is a special class which is able to generate new valid models, for testing purposes.
 * These models can be randomized by using a faker.
 *
 * @param < M >
 */
abstract class AbstractModelFactory<M> {

    static class NOTHING {}

    /**
     * A Faker instance which can be used to generate random model properties.
     *
     * @link https://github.com/DiUS/java-faker
     */
    protected final Faker faker = new Faker()

    /**
     * Returns the type of the model which is created by this factory.
     * @return The type of the model  which is created by this factory.
     */
    Class<M> getModelType() {
        // Reflection magic to determine the type of the model
        // Based on https://stackoverflow.com/questions/1901164/get-type-of-a-generic-parameter-in-java-with-reflection
        try {
            ((ParameterizedType) this.class.genericSuperclass).actualTypeArguments[0] as Class<M>
        } catch (ClassCastException e) {
            // This exception explains better what you did wrong
            throw new IllegalStateException("Model type of ${this.class.simpleName} could not be determined. Did you forget to add a type parameter to the factory?", e)
        }
    }

    /**
     * Mapping containing closures (factory methods) of default attributes for the model to create.
     * To be used together with {@link #attributes}.
     * Example:
     * <pre>
     * Map<String, Closure> defaultAttributes = [bar: {faker.name.firstName()}]
     * </pre>
     * Usage:
     * <pre>
     * Foo build(Map buildParameters = [:])
     *{*     new Foo(attributes(buildParameters))
     *}* </pre>
     * This results in an instance of Foo with a property bar set to a randomized first name.
     * @return A mapping containing default attributes for the model to create.
     */
    Map<String, Closure> getDefaultAttributes() {
        [:]
    }

    /**
     * Mapping containing closures (factory methods) of default relations for the model to create.
     * Each closure receives as argument the build parameters given for this relation.
     * To be used together with {@link #attributes}.
     * @return A mapping containing default attributes for the model to create.
     */
    Map<String, Closure> getDefaultRelations() {
        [:]
    }

    Map<String, Closure> getFields() {
        [:]
    }

    static Closure attribute(Closure defaultValueGenerator) {
        // If a value is given, use that one; otherwise call the default value generator
        return { Object... arguments -> arguments.length == 0 ? defaultValueGenerator() : arguments[0] }
    }

    static <R> Closure hasOne(AbstractModelFactory<R> factory) {
        // This is the same as creating an object with no default attributes
        return hasOne(factory, [:])
    }

    static <R> Closure hasOne(AbstractModelFactory<R> factory, Map defaultAttributes) {
        return { Object... arguments ->
            def buildParameter = arguments.length == 0 ? [:] : arguments[0]
            if (buildParameter instanceof Map) {
                factory.build(defaultAttributes + buildParameter)
            } else {
                // buildParameters is either null or an instance of the object
                factory.build((R) buildParameter)
            }
        }
    }

    static <R> Closure hasOne(AbstractModelFactory<R> factory, R defaultObject) {
        return { Object... arguments ->
            if (arguments.length == 0) {
                // No build parameters are given, so we use the default object
                defaultObject
            } else {
                // Some build parameters are given, behave as if there are no default attributes
                hasOne(factory)(arguments[0])
            }
        }
    }

    static <R> Closure belongsTo(AbstractModelFactory<R> factory) {
        hasOne(factory)
    }

    static <R> Closure belongsTo(AbstractModelFactory<R> factory, Map defaultAttributes) {
        hasOne(factory, defaultAttributes)
    }

    static <R> Closure belongsTo(AbstractModelFactory<R> factory, R defaultObject) {
        hasOne(factory, defaultObject)
    }

    static Closure hasMany(AbstractModelFactory factory, int defaultAmount) {
        return { Object... arguments ->
            def buildParameter = arguments.length == 0 ? [:] : arguments[0]

            if (buildParameter instanceof List) {
                // For each element in the list, build a new object
                factory.buildList(buildParameter)
            } else if (buildParameter instanceof Integer) {
                // Build the given amount of object
                factory.buildList(buildParameter)
            } else {
                // Use arguments to build the default amount of objects
                factory.buildList(defaultAmount, buildParameter as Map)
            }
        }
    }

    /**
     * Returns the passed object.
     * <p>
     * This method exists so it is possible to completely override a relation by passing your own instance, or null.
     * @param buildParameters The custom instance of the object.
     * @param skipHooks If true, the hooks ({@link #onAfterBuild} & {@link #onBuildComplete}) are skipped.
     * This is intended for nested calls by other build methods.
     * @return The passed object
     */
    M build(M model, boolean skipHooks = false) {
        skipHooks ? model : onBuildComplete(onAfterBuild(model))
    }

    /**
     * Returns a new instance.
     * <p>
     * In normal usage, this method should not be overriden. If you want to change how the model is build, use
     * {@link #onAfterBuild} or {@link #internalBuild}.
     *
     * @param skipHooks If true, the hooks ({@link #onAfterBuild} & {@link #onBuildComplete}) are skipped.
     * This is intended for nested calls by other build methods.
     * @return The new instance.
     */
    M build(boolean skipHooks = false) {
        M model = internalBuild(attributes([:]))
        skipHooks ? model : onBuildComplete(onAfterBuild(model))
    }

    /**
     * Returns a new instance that is not saved.
     * <p>
     * In normal usage, this method should not be overriden. If you want to change how the model is build, use
     * {@link #onAfterBuild} or {@link #internalBuild}.
     *
     * @param buildParameters Additional build parameters to use when building a new model. Build parameters allow to define custom values for attributes and relations..
     * @param skipHooks If true, the hooks ({@link #onAfterBuild} & {@link #onBuildComplete}) are skipped.
     * This is intended for nested calls by other build methods.
     * @return The new instance.
     */
    M build(Map buildParameters, boolean skipHooks = false) {
        M model = internalBuild(attributes(buildParameters))
        skipHooks ? model : onBuildComplete(onAfterBuild(model))
    }

    /**
     * Returns a new instance that is not saved.
     *
     * @param buildParameters Additional build parameters to use when building a new model. Build parameters allow to define custom values for attributes and relations..
     * @param traits A list of traits to apply to new model. A trait is basically a collection of attribute/relation updates, meant to create a model
     *                        representing a certain state. The possible traits are specified in the factory.
     * @return The new instance.
     */
    M build(Map buildParameters, List<String> traits) {
        M model = applyTraits(build(buildParameters, true), traits)
        onBuildComplete(onAfterBuild(model))
    }

    /**
     * Returns a new instance that is not saved.
     *
     * @param traits A list of traits to apply to new model. A trait is basically a collection of attribute/relation updates, meant to create a model
     *                        representing a certain state. The possible traits are specified in the factory.
     * @return The new instance.
     */
    M build(List<String> traits) {
        M model = applyTraits(build(true), traits)
        onBuildComplete(onAfterBuild(model))
    }

    /**
     * Returns a list of new instances that are not saved.
     *
     * @param amount The amount of instances to build.
     * @return A list of new instances.
     */
    List<M> buildList(int amount) {
        return buildList(amount, null)
    }

    /**
     * Returns a list of new instances that are not saved.
     *
     * @param amount The amount of instances to build.
     * @param buildParameters Additional build parameters to use when building a new model.
     * @return As list of new instances.
     */
    List<M> buildList(int amount, Map buildParameters) {
        List<M> result = new ArrayList<>(amount)

        for (int i = 0; i < amount; i++) {
            result.add(buildParameters == null ? build() : build(buildParameters))
        }

        result
    }

    /**
     * Returns a list of new instances that are not saved.
     *
     * @param buildParameters A list of additional build parameters. Each element in the list is applied to {@link #build}. So the size of the result is the same as the amount of elements in this list.
     * @return As list of new instances.
     */
    List<M> buildList(List buildParameters) {
        buildParameters.collect({ (M) this.build(it) })
    }

    /**
     * Actual builder of the model, which creates the instance using the computed attributes.
     * This method is not used when {@link #build(M)} is called.
     *
     * @param attributes The computed attributes of the model
     * @return A model with the values from the given attributes
     */
    protected M internalBuild(Map attributes) {
        getModelType().newInstance(attributes)
    }

    /**
     * Callback which is called after the model is build using the calculated attributes, but before it is returned as result of a build()
     * method. This allows to tweak the model, for example to fix relationships.
     *
     * @param model The model after it was created using the calculated attributes. Can be null
     * @return The model with possible alterations
     */
    M onAfterBuild(M model) {
        model
    }

    /**
     * Callback which is called after the model is completely build, just before it is returned by the build() method.
     * By default, this methods calls {@link ModelFactoryContext#onBuildComplete(Object)} when a context is specified
     * by the {@link ModelFactoryManager}
     *
     * @param model The built model. Can be null
     * @return The built model.
     */
    M onBuildComplete(M model) {
        def context = ModelFactoryManager.instance.currentContext
        if (context != null) {
            context.onBuildComplete(model)
        }
        model
    }

    /**
     * Returns a map of attributes and relations based on the specified default attribute, default relations and build parameters.
     * @param buildParameters The build parameters specified to override default attributes and/or relations.
     * @return A map of attributes which can be used to create a new instance.
     */
    Map attributes(Map buildParameters) {
        def attributes = buildParameters

        getFields().each { String name, Closure factoryMethod ->
            attributes[name] = buildParameters.containsKey(name) ? factoryMethod(buildParameters[name]) : factoryMethod()
        }

        attributes
    }

    /**
     * Returns a map containing possible traits for this factory. A trait is a collection of attributes and relations, meant to put the generated model in a certain
     * state. A trait is identified by a name, while the trait itself is declared as a Function over the generated model. When applied, the trait returns the model in the
     * new state.
     *
     * @return A map from trait name to trait function of possible traits for this factory.
     * @see <a href="http://www.rubydoc.info/gems/factory_girl/file/GETTING_STARTED.md#Traits">Traits applied in ruby factories</a>
     */
    protected Map<String, Closure> getTraits() {
        null
    }

    M applyTraits(M object, List<String> traits) {
        Map<String, Closure> availableTraits = getTraits()
        if (availableTraits != null) {
            traits.forEach { String traitName -> availableTraits.get(traitName)(object) }
        }
        object
    }

    /**
     * Returns a closure which takes buildParameters and build a list of objects depending on these parameters.
     *
     * <ul>
     *     <li>If a list is provided, this list is used as a base for the list of objects (each item applied to #build)</li>
     *     <li>If a map is provided, a number of objects will be build, each with the given map as buildparameters.
     *     The amount of objects equals the default amount of objects. </li>
     * </ul>
     * This method is intended for toMany relations in {@link #getDefaultRelations()}.
     *
     * Example:
     * <pre>
     * class UserFactory extends AbstractModelFactory
     *     defaultRelations = [addresses: useOrBuildList(AdresFactory.instance, 3)]
     *     ...
     * }
     * </pre>
     *
     * <ul>
     * <li>When calling with <code>userFactory.build()</code> a user with 3 addresses is generated.</li>
     * <li>When calling with <code>userFactory.build([addresses: [street: 'foo']])</code> a user with 3 addresses is generated where each address has the value 'foo' for the street property</li>
     * <li>When calling with <code>userFactory.build([addresses: [])</code> a user with 0 addresses is generated.</li>
     * <li>When calling with <code>userFactory.build([addresses: [[:], [:]])</code> a user with 2 addresses is generated.</li>
     * <li>When calling with <code>userFactory.build([addresses: 2)</code> a user with 2 addresses is generated.
     * This is the equal to calling <code>userFactory.build([addresses: [[:], [:]])</code>.</li>
     * </ul>
     *
     * @param factory The factory to use for building the objects
     * @param defaultAmount The default amount of objects to create.
     * @return A list of objects created with the factory.
     */
    static Closure<List> useOrBuildList(AbstractModelFactory factory, int defaultAmount) {
        return { Object args ->
            if (args instanceof List) {
                // For each element in the list, build a new model
                return factory.buildList(args)
            } else if (args instanceof Integer) {
                // Build the given amount of models
                return factory.buildList(args)
            } else {
                // Use arguments to build the default amount of models
                return factory.buildList(defaultAmount, args as Map)
            }
        }
    }

    /**
     * Returns a closure which takes buildParameters and build a list of objects depending on these parameters.
     * <ul>
     *     <li>If a list is provided, this list is used as a base for the list of objects (each item applied to #build)</li>
     *     <li>If a map is provided, this map is merged with each item in the default build parameters and used to build an object.
     *     So, the amount of objects equals the size of the default list</li>
     * </ul>
     *
     * This method is intended for toMany relations in {@link #getDefaultRelations()}.
     *
     * Example:
     * <pre>
     * class UserFactory extends AbstractModelFactory
     *    defaultRelations = [addresses: useOrBuildList(AdresFactory.instance, [[street: 'a', number: 10], [street: 'b']])]
     *    ...
     * }
     * </pre>
     *
     * <ul>
     * <li>When calling with <code>userFactory.build()</code> a user with 2 addresses is generated with the given streets. The second address has a random number (given AdresFactory has a default value for this).</li>
     * <li>When calling with <code>userFactory.build([addresses: [street: 'foo']])</code> a user with 2 addresses is generated where each address has the value 'foo' for the street property. The first address still has number 10.</li>
     * <li>When calling with <code>userFactory.build([addresses: [])</code> a user with 0 addresses is generated.</li>
     * <li>When calling with <code>userFactory.build([addresses: [[:], [:], [:]])</code> a user with 3 addresses is generated.</li>
     * <li>When calling with <code>userFactory.build([addresses: 3])</code> a user with 3 addresses is generated.
     * This is equal to calling <code>userFactory.build([addresses: [[:], [:], [:]])</code></li>
     * </ul>
     *
     * @param factory The factory to use for building the objects
     * @param defaultAmount The default amount of objects to create.
     * @return A list of objects created with the factory.
     */
    static Closure<List> useOrBuildList(AbstractModelFactory factory, List defaultBuildParameters) {
        return { Object args ->
            if (args instanceof List) {
                // A list of build parameters: use each list element to build a new object
                factory.buildList(args)
            } else if (args instanceof Integer) {
                // Build the given amount of models
                return factory.buildList(args)
            } else if (args instanceof Map && !(args as Map).isEmpty()) {
                // A map containing buildParameters: merge with each list item in the default parameters and use these to build objects
                factory.buildList(defaultBuildParameters.collect { it + args })
            } else {
                // No build parameters: build a list of objects with the default buildParameters
                factory.buildList(defaultBuildParameters)
            }
        }
    }
}
