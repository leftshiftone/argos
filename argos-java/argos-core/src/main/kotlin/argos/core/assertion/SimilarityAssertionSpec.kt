package argos.core.assertion

/**
 * This class holds the necessary specifications for a similarity assertion to perform.
 *
 * @param text1 the first input text to compare
 * @param text2 the second input text to compare
 * @param threshold the threshold score
 */
data class SimilarityAssertionSpec(val text1: String, val text2: String, val threshold: Float = 0.9f)