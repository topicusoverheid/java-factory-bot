package nl.topicus.overheid.javafactorybot

/**
 * Interface for a context in which a factory should build objects.
 * Can be used to provide implementations for certain hooks in the process, for
 * example to persist the built object to a database.
 *
 * @author dennis
 * @since 04 Jan 2018
 */
interface FactoryContext {
    /**
     * Called when an object is build and should be persisted.
     * <p>
     * This method will only be called when the object is non-null.
     *
     * @param object The object which was build.
     * @param The persisted object
     */
    def <M> M persist(M object)
}
