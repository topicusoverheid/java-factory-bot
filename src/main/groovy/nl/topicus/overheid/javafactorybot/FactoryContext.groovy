package nl.topicus.overheid.javafactorybot

/**
 * Interface for a context in which a factory should build objects.
 * Can be used to provide implementations for certain hooks in the process, for
 * example to persist the built model to a database.
 *
 * @author dennis
 * @since 04 Jan 2018
 */
interface FactoryContext {
    /**
     * Called when an object is build and should be persisted.
     * @param object The object which was build. Can be null.
     */
    void onCreate(Object object)
}
