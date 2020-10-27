package argos.runtime.xml

import argos.api.IAssertion
import argos.core.assertion.*
import argos.runtime.xml.support.*
import org.w3c.dom.Node
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.Exception

// TODO: javadoc
class ArgosXML {
    data class ParsedAssertions(val identityId: String, val assertionList: List<IAssertion>)

    private val assertions: MutableList<IAssertion> = emptyList<IAssertion>().toMutableList()
    private val scheme = File("./src/main/resources/argos.xsd")

    fun parse(input: InputStream): ParsedAssertions {
        val doc = XmlParser(FileInputStream(scheme)).invoke(input)

        val identityId: String = doc.getElementsByTagName("assertions").item(0)
                .findAttr("identityId").get()
        val intentAssertions = doc.getElementsByTagName("intentAssertion")
        val similarityAssertions = doc.getElementsByTagName("similarityAssertion")
        val nerAssertions = doc.getElementsByTagName("nerAssertion")
        val translationAssertions = doc.getElementsByTagName("translationAssertion")
        val sentimentAssertions = doc.getElementsByTagName("sentimentAssertion")
        val conversationAssertions = doc.getElementsByTagName("conversationAssertion")

        // Parse IntentAssertions
        for (intentAssertion in intentAssertions.toList()) {
            val text: String = intentAssertion.textContent
            val intent: String = intentAssertion.findAttr("name").get()

            assertions.add(IntentAssertion((IntentAssertionSpec(text, intent))))
        }

        // Parse SimilarityAssertions
        for (similarityAssertion in similarityAssertions.toList()) {
            val threshold: Float = similarityAssertion.findAttr("threshold").get().toFloat()
            val texts: List<String> = similarityAssertion.childNodes.map { it.textContent }
            val text1: String = texts.get(0)
            val text2: String = texts.get(1)

            assertions.add(SimilarityAssertion(SimilarityAssertionSpec(text1, text2, threshold)))
        }

        // Parse NERAssertions
        val entityList: MutableList<NERAssertionSpec.Entity> = emptyList<NERAssertionSpec.Entity>().toMutableList()
        for (nerAssertion in nerAssertions.toList()) {
            val text: String = nerAssertion.findAttr("text").get()
            val entities = nerAssertion.findAll("entity")
            for (entity in entities) {
                val entityText: String = entity.textContent
                val label: String = entity.attributes.getNamedItem("label")?.textContent!!
                val index: Int? = entity.attributes.getNamedItem("index")?.textContent?.toIntOrNull()
                val not: Boolean = entity.attributes.getNamedItem("not")?.textContent?.equals("true") ?: false

                entityList.add(NERAssertionSpec.Entity(label, entityText, index, not))
            }
            assertions.add(NERAssertion(NERAssertionSpec(text, entityList)))
        }

        // Parse TranslationAssertions
        for(translationAssertion in translationAssertions.toList()) {
            val threshold = translationAssertion.findAttr("threshold").get().toFloat()
            val assertionText = translationAssertion.findAll("text")
            if (assertionText.size == 2) {
                val textList: MutableList<Map<String, String>> = emptyList<Map<String, String>>().toMutableList()
                for (aText in assertionText) {
                    textList.add(mapOf(
                            "text" to aText.textContent,
                            "lang" to aText.attributes.getNamedItem("lang")?.textContent!!))
                }

                assertions.add(TranslationAssertion(TranslationAssertionSpec(
                        textList[0].get("lang")!!,
                        textList[0].get("text")!!,
                        textList[1].get("lang")!!,
                        textList[1].get("text")!!,
                        threshold)))
            }
        }

        // Parse SentimentAssertions
        for (sentimentAssertion in sentimentAssertions.toList()) {
            val text = sentimentAssertion.textContent
            val type = sentimentAssertion.findAttr("type").get()

            assertions.add(SentimentAssertion(SentimentAssertionSpec(text, type)))
        }

        // Parse ConversationAssertions
        for (conversationAssertion in conversationAssertions.toList()) {
            val conversationList = ArrayList(conversationAssertion.findAll("gaia", "user")
                .map {
                    val propertyList = emptyList<Conversation.Property>().toMutableList()

                    it.findAll("text", "button", "block", "headline", "link", "break")
                            .map { node ->
                                when(node.nodeName) {
                                    "text" -> ConversationPropertyBuilder.createTextFromNode(node)
                                    "button" -> ConversationPropertyBuilder.createButtonFromNode(node)
                                    "block" -> ConversationPropertyBuilder.createBlockFromNode(node)
                                    "headline" -> ConversationPropertyBuilder.createHeadlineFromNode(node)
                                    "link" -> ConversationPropertyBuilder.createLinkFromNode(node)
                                    "break" -> ConversationPropertyBuilder.createBreakFromNode(node)
                                    else -> throw Exception()
                                }
                            }
                            .forEach { propertyList.add(it) }

                    Conversation.create(
                            Conversation.Type.valueOf(it.nodeName.toUpperCase()), *propertyList.toTypedArray()                    )
                })

            assertions.add(ConversationAssertion(ConversationAssertionSpec(conversationList)))
        }

        return ParsedAssertions(identityId, assertions)
    }

    private object ConversationPropertyBuilder {
        fun createTextFromNode(node: Node): Conversation.Property.Text {
            return Conversation.Property.Text(
                    textContent = node.textContent,
                    id = node.findAttr("id").orElse(null),
                    _class = node.findAttr("class").orElse(null))
        }

        fun createButtonFromNode(node: Node): Conversation.Property.Button {
            return Conversation.Property.Button(
                    textContent = node.textContent,
                    value = node.findAttr("value").orElse(null),
                    name = node.findAttr("name").orElse(null),
                    position = node.findAttr("position").orElse(null),
                    id = node.findAttr("id").orElse(null),
                    _class = node.findAttr("class").orElse(null))
        }

        fun createBlockFromNode(node: Node): Conversation.Property.Block {
            val blockPropertyList = emptyList<Conversation.Property>().toMutableList()
            if (node.hasChildNodes()) {
                node.findAll("headline", "text", "link", "break", "block")
                        .map {
                            when (it.nodeName) {
                                "headline" -> createHeadlineFromNode(it)
                                "text" -> createTextFromNode(it)
                                "link" -> createLinkFromNode(it)
                                "break" -> createBreakFromNode(it)
                                "block" -> createBlockFromNode(it)
                                else -> throw Exception()
                            }
                        }
                        .map { blockPropertyList.add(it) }
            }

            return Conversation.Property.Block(
                    properties = if (blockPropertyList.isEmpty()) null else blockPropertyList,
                    id = node.findAttr("id").orElse(null),
                    _class = node.findAttr("class").orElse(null),
                    name = node.findAttr("name").orElse(null))
        }

        fun createHeadlineFromNode(node: Node): Conversation.Property.Headline {
            return Conversation.Property.Headline(
                    textContent = node.textContent,
                    id = node.findAttr("id").orElse(null),
                    _class = node.findAttr("class").orElse(null))
        }

        fun createLinkFromNode(node: Node): Conversation.Property.Link {
            return Conversation.Property.Link(
                    textContent = node.textContent,
                    value = node.findAttr("value").orElse(null),
                    name = node.findAttr("name").orElse(null),
                    id = node.findAttr("id").orElse(null),
                    _class = node.findAttr("class").orElse(null),
                    _if = node.findAttr("if").orElse(null))
        }

        fun createBreakFromNode(node: Node): Conversation.Property.Break {
            return Conversation.Property.Break(
                    textContent = node.textContent)
        }
    }
}