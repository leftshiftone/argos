package argos.core.assertion

/**
 * This class holds the necessary specifications for a classification assertion to perform.
 *
 * @param text the input text who should be classified
 * @param `class` the intended classification for the input text
 */
data class ClassificationAssertionSpec(val text: String, val `class`: String)