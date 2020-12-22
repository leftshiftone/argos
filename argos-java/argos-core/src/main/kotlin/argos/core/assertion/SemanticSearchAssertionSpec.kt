package argos.core.assertion

/**
 * This class holds the necessary specifications for a semantic search assertion to perform.
 *
 * @param text the text which the semantic search skill gets performed on
 * @param topN the size of the entries in the response list
 * @param entries a list of the entries for the assertion to look for
 */
data class SemanticSearchAssertionSpec(val text: String, val topN: Int, val entries: List<Entry>) {
    /**
     * This class holds information about a semantic entity for the assertion to look for.
     *
     * @param id the id of the intended resulting document
     * @param score the threshold score for this document
     */
    data class Entry(val id: String, val score: Float)
}