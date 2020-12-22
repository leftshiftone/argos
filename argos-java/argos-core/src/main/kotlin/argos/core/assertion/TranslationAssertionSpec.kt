package argos.core.assertion

/**
 * This class holds the necessary specifications for a translation assertion to perform.
 *
 *  @param inLang the input language
 *  @param inText the input text
 *  @param translationLang the language to translate to
 *  @param translatedText the intended translated text
 *  @param threshold the threshold score
 */
data class TranslationAssertionSpec(
    val inLang: String,
    val inText: String,
    val translationLang: String,
    val translatedText: String,
    val threshold: Float = 0.9f
)