package nl.topicus.overheid.javafactorybot.dsl

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import nl.topicus.overheid.javafactorybot.definition.Definition

trait FactoryTraits<M> {
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
    Map<String, Definition<M>> getTraits() {
        null
    }

    def 'trait'(String name, @DelegatesTo(Definition) @ClosureParams(value=SimpleType) Closure closure){
        Definition<M> definition = new Definition<>()
        closure.delegate = definition
        closure(definition)
    }
}
