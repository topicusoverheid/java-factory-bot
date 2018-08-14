package nl.topicus.overheid.javafactorybot.factory


import nl.topicus.overheid.javafactorybot.definition.Attribute

trait FactoryAttributes {
    /**
     * Map containing {@link Attribute}s of this factory. An {@link Attribute} is a definition of an attribute in the
     * (generated) object, and minimally implements a function which, given an instance of {@link nl.topicus.overheid.javafactorybot.Evaluator}, yields the
     * value this attribute should have in the generated object.
     *
     * @return A map containing the {@link Attribute}s of this factory.
     */
    Map<String, Attribute> attributes = [:]
}
