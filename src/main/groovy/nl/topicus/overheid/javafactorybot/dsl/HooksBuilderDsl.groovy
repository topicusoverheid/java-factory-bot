package nl.topicus.overheid.javafactorybot.dsl

import java.util.function.Consumer

class HooksBuilderDsl<M> {
    private FactoryHooks<M> hooks

    HooksBuilderDsl(FactoryHooks<M> factoryHooks) {
        this.hooks = factoryHooks
    }

    def attributes(Consumer<Map<String, Object>> closure) {
        hooks.afterAttributesHook = closure
    }

    def build(Consumer<M> closure) {
        hooks.afterBuildHook = closure
    }

    def create(Consumer<M> closure) {
        hooks.afterCreateHook = closure
    }
}