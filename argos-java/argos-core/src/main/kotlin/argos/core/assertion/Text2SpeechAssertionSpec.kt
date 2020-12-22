package argos.core.assertion

/**
 * This class holds the necessary specifications for a text to speech assertion to perform.
 *
 * @param text the input text
 * @param speech the url to the intended speech file
 */
data class Text2SpeechAssertionSpec(val text: String, val speech: String)