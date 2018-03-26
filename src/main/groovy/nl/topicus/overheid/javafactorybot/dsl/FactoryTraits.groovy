package nl.topicus.overheid.javafactorybot.dsl

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import nl.topicus.overheid.javafactorybot.definition.Trait

trait FactoryTraits<M> {
    Map<String, Trait<M>> traits = null

    /**
     * Returns a map containing possible traits for this factory. A trait is a collection of attributes and relations,
     * meant to put the generated object in a certain state. A trait is identified by a name,
     * while the trait itself is declared as a Closure over the generated object.
     *
     * Traits are applied after the object is build from attributes, but before {@link #onAfterBuild(java.lang.Object)}
     * is called.
     *
     * @return A map from trait name to trait function of possible traits for this factory.
     * @see <a href="http://www.rubydoc.info/gems/factory_girl/file/GETTING_STARTED.md#Traits">Traits applied in ruby factories</a>
     */
    Map<String, Trait<M>> getTraits() {
        null
    }

    def 'trait'(String name, @DelegatesTo(value = Trait, strategy = Closure.DELEGATE_FIRST) @ClosureParams(value = SimpleType) Closure closure) {
        traits = traits ?: [:]
        Trait<M> definition = new Trait<>()
        closure.delegate = definition
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure(definition)
        traits.put(name, definition)
    }
}
