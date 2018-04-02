package nl.topicus.overheid.javafactorybot.definition

import nl.topicus.overheid.javafactorybot.factory.FactoryHooks
import nl.topicus.overheid.javafactorybot.factory.FactoryAttributes
import nl.topicus.overheid.javafactorybot.factory.FactoryTraits

/**
 * A definition is the base class containing the definition of how an object should be created. This base is used by
 * both the factory and traits, to allow to use the same syntax for both.
 */
class Definition<M> implements FactoryHooks<M>, FactoryAttributes, FactoryTraits {

}
