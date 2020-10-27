package argos.core.assertion

class Conversation private constructor() {
    companion object Factory {
        fun create(type: Type, vararg properties: Property): Element {
            return when(type) {
                Type.GAIA ->
                    GaiaInteraction(properties
                            .filter { it is Property.Text
                                    || it is Property.Button
                                    || it is Property.Block
                                    || it is Property.Headline
                                    || it is Property.Link
                                    || it is Property.Break}
                            .toList())
                Type.USER ->
                    UserInteraction(properties
                            .filter { it is Property.Text
                                    || it is Property.Button }
                            .toList())
            }
        }
    }
    enum class Type{
        GAIA, USER;
    }

    interface Element {
        val elementName: String
        fun printElement() { println(this.toString()) }
    }
    data class GaiaInteraction(val properties: List<Property>): Element {
        override val elementName: String = "GAIA"
        override fun printElement() {
            println("GAIA:")
            properties.forEach { println("      $it") }
        }
    }
    data class UserInteraction(val properties: List<Property>): Element {
        override val elementName: String = "User"
        override fun printElement() {
            println("User:")
            properties.forEach { println("      $it") }
        }
    }

    sealed class Property {
        data class Text(
                val textContent: String? = null,
                val id: String? = null,
                val _class: String? = null
        ) : Property() {
            override fun toString(): String {
                return ("[Text"
                        + (if (id?.isEmpty() != false) "" else " (id: $id)")
                        + (if (_class?.isEmpty() != false) "" else " (class: $_class)")
                        + "]" + (if (textContent?.isEmpty() != false) "" else ": $textContent"))
            }
        }

        data class Button(
                val textContent: String? = null,
                val value: String? = null,
                val name: String? = null,
                val position: String? = null,
                val id: String? = null,
                val _class: String? = null
        ) : Property() {
            override fun toString(): String {
                return ("[Button"
                        + (if (value?.isEmpty() != false) "" else " (Value: $value)")
                        + (if (name?.isEmpty() != false) "" else " (Name: $name)")
                        + (if (position?.isEmpty() != false) "" else " (Position: $position)")
                        + (if (id?.isEmpty() != false) "" else " (id: $id)")
                        + (if (_class?.isEmpty() != false) "" else " (class: $_class)")
                        + "]" + (if (textContent?.isEmpty() != false) "" else ": $textContent"))
            }
        }

        data class Block(
                val properties: List<Property>? = null,
                val id: String? = null,
                val _class: String? = null,
                val name: String? = null
        ) : Property() {
            override fun toString(): String {
                return ("[Block"
                        + (if (name?.isEmpty() != false) "" else " (Name: $name)")
                        + (if (id?.isEmpty() != false) "" else " (id: $id)")
                        + (if (_class?.isEmpty() != false) "" else " (class: $_class)")
                        + "]"
                        + (if (properties?.isEmpty() != false) "" else
                            "\n        ${properties.map { it.toString() }}"))
            }
        }

        data class Headline(
                val textContent: String? = null,
                val id: String? = null,
                val _class: String? = null
        ) : Property() {
            override fun toString(): String {
                return ("[Headline"
                        + (if (id?.isEmpty() != false) "" else " (id: $id)")
                        + (if (_class?.isEmpty() != false) "" else " (class: $_class)")
                        + "]" + (if (textContent?.isEmpty() != false) "" else ": $textContent"))
            }
        }

        data class Link(
                val textContent: String? = null,
                val value: String? = null,
                val name: String? = null,
                val id: String? = null,
                val _class: String? = null,
                val _if: String? = null
        ) : Property() {
            override fun toString(): String {
                return ("[Link"
                        + (if (value?.isEmpty() != false) "" else " (Value: $value)")
                        + (if (name?.isEmpty() != false) "" else " (Name: $name)")
                        + (if (id?.isEmpty() != false) "" else " (id: $id)")
                        + (if (_class?.isEmpty() != false) "" else " (class: $_class)")
                        + (if (_if?.isEmpty() != false) "" else " (if: $_if)")
                        + "]" + (if (textContent?.isEmpty() != false) "" else ": $textContent"))
            }
        }

        data class Break(val textContent: String? = null) : Property() {
            override fun toString(): String {
                return ("[Break]"
                        + (if (textContent?.isEmpty() != false) "" else ": $textContent"))
            }
        }
    }
}