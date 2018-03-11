package nl.topicus.overheid.javafactorybot.exception

import nl.topicus.overheid.javafactorybot.Evaluator

class EvaluationException extends IllegalStateException {
    EvaluationException(Evaluator evaluator, String attributeName, Exception cause = null) {
        super("Unable to evaluate attribute '$attributeName' for type ${evaluator.factory.objectType.name}: ${cause?.message}", cause)
    }
}
