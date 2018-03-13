package nl.topicus.overheid.javafactorybot.exception

import nl.topicus.overheid.javafactorybot.Factory

class TraitNotFoundException extends IllegalStateException {
    TraitNotFoundException(Factory factory, String traitName) {
        super("Trait '$traitName' is not specified on ${factory.class.simpleName}. Specified traits are: ${factory.traits ?: "none"}")
    }
}