package argos.core.assertion

/**
 * This class holds the necessary specifications for a language detection assertion to perform.
 *
 * @param text the text whose language should be detected
 * @param lang the intended language
 */
data class LanguageDetectionAssertionSpec(val text: String, val lang: String)