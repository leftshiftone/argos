package argos.core.assertion

/**
 * This class holds the necessary specifications for a ocr assertion to perform.
 *
 * @param image the url of the input image
 * @param texts a list of the detected texts
 */
data class OCRAssertionSpec(val image: String, val texts: List<Text>) {
    /**
     * This class holds the detected texts for a ocr assertion.
     *
     * @param text the detected text
     * @param fuzzy whether the detected text was fuzzy in the input image
     */
    data class Text(val text: String, val fuzzy: Boolean)
}