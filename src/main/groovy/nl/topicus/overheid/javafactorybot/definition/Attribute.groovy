package nl.topicus.overheid.javafactorybot.definition

import nl.topicus.overheid.javafactorybot.Evaluator

/**
 * Interface of a definition of an attribute of an object. An Attribute is responsible for generating the value of the
 * attribute, taking into account the user specified override and evaluator. The evaluator can be used to determine
 * values of other attributes.
 */
interface Attribute extends GroovyObject {
    /**
     * Get the value of this attribute, possibly using the given evaluator.
     *
     * @param evaluator The evaluator which can be used to determine the value of other attribute.
     * @param owner The owner of the value of the attribute, if the owner is already build.
     * @return The value of this attribute.
     */
    def evaluate(Evaluator evaluator, Object owner)

    /**
     * Get the value of this attribute using the given override, possibly using the given evaluator.
     *
     * @param override The override for this attribute, as given by the user. This can be intentionally null, for
     * instance when the value of the attribute should be null. If no override is given {@link #evaluate(Evaluator, Object)}
     * is used instead.
     * @param evaluator The evaluator which can be used to determine the value of other attribute.
     * @param owner The owner of the value of the attribute, if the owner is already build.
     * @return The value of this attribute.
     */
    def evaluate(Object override, Evaluator evaluator, Object owner)

    /**
     * Determines if this attribute should be evaluated before or after the owner object is build.
     *
     * If the result is {@code false}, the attribute will be evaluated during the init phase (in which attributes are build).
     * If the result is {@code true}, the attribute will be evaluated during the finalize phase (after build, before optional persist).
     * @return If the attribute should be evaluated after the owner object is build.
     */
    boolean isAfterBuild()
}
