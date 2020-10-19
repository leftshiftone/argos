package argos.core.assertion

data class NERAssertionSpec(val text: String, val entities: List<Entity>) {
    data class Entity(val label: String, val text: String? = null, val index: Int? = null, val not: Boolean = false)
}