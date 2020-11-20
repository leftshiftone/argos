package argos.core.assertion

data class SemanticSearchAssertionSpec(val text: String, val topN: Int, val entries: List<Entry>) {
    data class Entry(val id: String, val score: Float)
}