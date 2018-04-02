package nl.topicus.overheid.javafactorybot.factory

trait FactoryHooks<M> {
    /**
     * Callback which is called after the values of the attributes are evaluated, but before the object itself is built.
     *
     * @param attributes The evaluated attributes
     */
    void onAfterAttributes(Map<String, Object> attributes) {
    }

    /**
     * Callback which is called after the model is built using the evaluated attributes, but before it is returned as
     * result of the build() method. This allows to tweak the model, for example to fix relationships.
     *
     * @param model The model after it was built using the evaluated attributes. Can be null
     */
    void onAfterBuild(M model) {
    }

    /**
     * Callback which is called after the model is created (built and saved) using the evaluated attributes,
     * but before it is returned as result of the create() method. This allows to tweak the model,
     * for example to fix relationships.
     *
     * This callback is only used when models are created using {@link nl.topicus.overheid.javafactorybot.BaseFactory#create}
     *
     * @param model The model after it was created using the evaluated attributes. Can be null
     */
    void onAfterCreate(M model) {
    }
}
