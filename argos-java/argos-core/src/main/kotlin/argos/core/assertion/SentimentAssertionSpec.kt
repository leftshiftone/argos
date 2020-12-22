package argos.core.assertion

/**
 * This class holds the necessary specifications for a sentiment assertion to perform.
 *
 * @param text the input text
 * @param type the intended type of the input text
 */
data class SentimentAssertionSpec(val text: String, val type: String)