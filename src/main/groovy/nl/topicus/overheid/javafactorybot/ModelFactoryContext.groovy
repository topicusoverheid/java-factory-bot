package nl.topicus.overheid.javafactorybot

/**
 * Interface for a context in which a factory should build models. Can be used to provide implementations for hooks in the process, for
 * example to persist the created model to a database.
 *
 * @author dennis
 * @since 04 Jan 2018
 */
interface ModelFactoryContext {
    /**
     * Called after a model is build (including all relations).
     * @param model The model which was build. Can be null.
     */
    void onBuildComplete(Object model)
}
