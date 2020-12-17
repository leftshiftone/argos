package argos.core.assertion

data class NERAssertionSpec(val text: String, val entities: List<Entity>) {
    data class Entity(val label: String, val text: String, val index: Int? = null, val not: Boolean = false) {
        override fun toString(): String {
            val entityStr = "Entity(text=$text, label=$label" + (if (index!=null) ", index=$index" else "")+ ")"
            if (not)
                return "not($entityStr)"
            return entityStr
        }
    }
}