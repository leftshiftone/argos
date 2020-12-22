package argos.core.assertion

/**
 * This class holds the necessary specifications for a intent assertion to perform.
 *
 * @param text the text which the intent detection skill gets performed on
 * @param intent the intended intent for this text
 * @param score the threshold score
 */
data class IntentAssertionSpec(val text: String, val intent: String, val score: Float = 0.85f)
