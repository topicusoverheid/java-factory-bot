package nl.topicus.overheid.javafactorybot

import com.github.javafaker.Faker

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

    def <M, F extends Faker, T extends BaseFactory<M, F>> T getFactoryInstance(Class<T> factoryClass) {
        if (!factoryInstances.containsKey(factoryClass)) {
            factoryInstances.put(factoryClass, factoryClass.newInstance())
        }
        factoryInstances[factoryClass] as T
    }
}
