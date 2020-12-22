package argos.core.assertion

/**
 * This class holds the necessary specifications for a ner assertion to perform.
 *
 * @param text the text which the ner skill gets performed on
 * @param entities a list of criteria for the assertion to succeed
 */
data class NERAssertionSpec(val text: String, val entities: List<Entity>) {
    /**
     * This class holds the criteria for a ner assertion to succeed
     *
     * @param label the required label for a text
     * @param text the text which should be labeled
     * @param index the index of the text in the input text
     * @param not whether the label is intended for the text;
     *              if <code>true</code>, the assertion will fail if this label gets returned for that text
     */
    data class Entity(val label: String, val text: String, val index: Int? = null, val not: Boolean = false) {

        override fun toString(): String {
            val entityStr = "Entity(text=$text, label=$label" + (if (index!=null) ", index=$index" else "")+ ")"
            if (not)
                return "not($entityStr)"
            return entityStr
        }
    }
}