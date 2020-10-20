package argos.core.assertion

data class TranslationAssertionSpec(val inLang: String, val inText: String,
                                    val translationLang: String, val translatedText: String,
                                    val threshold: Float = 0.9f)