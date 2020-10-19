package argos.runtime.xml

import argos.api.IAssertion
import argos.core.assertion.*
import argos.runtime.xml.support.*
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

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

        for (intentAssertion in intentAssertions.toList()) {
            val text: String = intentAssertion.textContent
            val intent: String = intentAssertion.findAttr("name").get()

            assertions.add(IntentAssertion((IntentAssertionSpec(text, intent))))
        }

        for (similarityAssertion in similarityAssertions.toList()) {
            val threshold: Float = similarityAssertion.findAttr("threshold").get().toFloat()
            val texts: List<String> = similarityAssertion.childNodes.map { it.textContent }
            val text1: String = texts.get(0)
            val text2: String = texts.get(1)

            assertions.add(SimilarityAssertion(SimilarityAssertionSpec(text1, text2, threshold)))
        }

        val entityList: MutableList<NERAssertionSpec.Entity> = emptyList<NERAssertionSpec.Entity>().toMutableList()
        for (nerAssertion in nerAssertions.toList()) {
            val text: String = nerAssertion.findAttr("text").get()
            val entities = nerAssertion.findAll("entity")
            for (entity in entities) {
                val entityText: String = entity.textContent
                val label: String = entity.attributes.getNamedItem("label")?.textContent!!
                val index: Int? = entity.attributes.getNamedItem("index")?.textContent?.toIntOrNull()
                val not: Boolean = entity.attributes.getNamedItem("not")?.textContent?.equals("true") ?: false

                entityList.add(NERAssertionSpec.Entity(entityText, label, index, not))
            }
            assertions.add(NERAssertion(NERAssertionSpec(text, entityList)))
        }

        return ParsedAssertions(identityId, assertions)
    }
}