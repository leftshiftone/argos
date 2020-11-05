package argos.core.assertion

data class OCRAssertionSpec(val image: String, val texts: List<Text>) {
    data class Text(val text: String, val fuzzy: Boolean)
}