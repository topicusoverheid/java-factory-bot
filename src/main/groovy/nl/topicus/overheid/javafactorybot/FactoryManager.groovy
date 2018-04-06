package nl.topicus.overheid.javafactorybot

import com.github.javafaker.Faker

import java.lang.reflect.Method

/**
 * Global manager for the factories which can be used to manage the factories. This manager is a singleton and can contain shared configuration
 * between factories.
 *
 * @author dennis
 * @since 04 Jan 2018
 */
@Singleton
class FactoryManager {
    /**
     * Context which should be used as the current context when {@link BaseFactory#create} is used instead of {@link BaseFactory#build}.
     */
    FactoryContext createContext = null

    /**
     * The context which should be used by the factories in the current execution.
     */
    FactoryContext currentContext = null

    /**
     * Enable the create context. This method is called when one of the {@link BaseFactory#create} methods is used. By
     * setting the context only once, subsequent calls to {@link BaseFactory#build} which are part of the initial create
     * action will also result in presistent relations.
     */
    void enableCreateContext() {
        currentContext = createContext
    }

    /**
     * Disable the create context.
     */
    void disableCreateContext() {
        currentContext = null
    }

    private Map<Class<BaseFactory<?, ? extends Faker>>, ? extends BaseFactory<?, ? extends Faker>> factoryInstances = [:]

    /**
     * Gets or creates an instance of the given factory type.
     *
     * The default implementation keeps a single instance per factory type. A new instance is created
     * by calling {@link #newFactoryInstance(java.lang.Class)}}.
     *
     * @param factoryClass The type of the factory to get or create an instance of.
     * @return An instance of the factory type.
     */
    def <M, F extends Faker, T extends BaseFactory<M, F>> T getFactoryInstance(Class<T> factoryClass) {
        if (!factoryInstances.containsKey(factoryClass)) {
            factoryInstances.put(factoryClass, newFactoryInstance(factoryClass))
        }
        factoryInstances[factoryClass as Class<BaseFactory<?, ? extends Faker>>] as T
    }

    /**
     * Creates a new instance of the given factory type.
     *
     * The default implementation looks if a method called 'getInstance' is defined on the class. If such
     * a method exists, this method is invoked. Otherwise, the default constructor is used.
     *
     * This implementation exists so the annotation {@link Singleton} can be applied to factories.
     *
     * @param factoryClass The type of the factory to create an instance of
     * @return A new instance of the factory type.
     */
    def <M, F extends Faker, T extends BaseFactory<M, F>> T newFactoryInstance(Class<T> factoryClass) {
        T factory = null
        try{
            Method method = factoryClass.getMethod("getInstance")
            if(method != null){
                factory = method.invoke(null) as T
            }
        } catch(NoSuchMethodException ignored) {
            // Do nothing
        }
        factory ?: factoryClass.newInstance()
    }
}
