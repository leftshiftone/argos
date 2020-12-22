package argos.core.assertion

/**
 * This class holds the necessary specifications for a speech to text assertion to perform.
 *
 * @param speech the url to the speech file
 * @param text the intended text for this speech
 */
data class Speech2TextAssertionSpec(val speech: String, val text: String)