package nl.topicus.overheid.javafactorybot.exception

import nl.topicus.overheid.javafactorybot.Evaluator

class EvaluationException extends IllegalStateException {
    EvaluationException(Evaluator evaluator, String attributeName, Exception cause = null) {
        super("Unable to evaluate attribute '$attributeName' for factory ${evaluator.factory.class.simpleName}: ${cause?.message}", cause)
    }
}
