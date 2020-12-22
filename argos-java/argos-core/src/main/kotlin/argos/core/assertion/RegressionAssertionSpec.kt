package argos.core.assertion

/**
 * This class holds the necessary specifications for a regression assertion to perform.
 *
 * @param text the text which the regression skill gets performed on
 * @param score the threshold score
 */
data class RegressionAssertionSpec(val text: String, val score: Float)