package argos.runtime.xml

import argos.api.IAssertion
import argos.core.assertion.IntentAssertion
import argos.core.assertion.IntentAssertionSpec
import argos.core.assertion.SimilarityAssertion
import argos.core.assertion.SimilarityAssertionSpec
import argos.runtime.xml.support.XmlParser
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import argos.runtime.xml.support.toList
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

// TODO: javadoc
class ArgosXML {
    private val assertions: MutableList<IAssertion> = emptyList<IAssertion>().toMutableList()
    private val scheme = File("./src/main/resources/argos.xsd")

    fun parse(input: InputStream): ParsedAssertions {
        val doc = XmlParser(FileInputStream(scheme)).invoke(input)

        val identityId: String = doc.getElementsByTagName("assertions").item(0)
                .findAttr("identityId").get()
        val intentAssertions = doc.getElementsByTagName("intentAssertion")
        val similarityAssertions = doc.getElementsByTagName("similarityAssertion")

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

        return ParsedAssertions(identityId, assertions)
    }
}

data class ParsedAssertions(val identityId: String, val assertionList: List<IAssertion>)