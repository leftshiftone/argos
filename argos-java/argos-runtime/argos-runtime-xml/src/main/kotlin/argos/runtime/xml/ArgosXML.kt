package argos.runtime.xml

import argos.api.ArgosOptions
import argos.api.AssertionGroup
import argos.api.IAssertion
import argos.api.IAssertionResult
import argos.core.assertion.AbstractArgos
import argos.core.conversation.Gaia
import argos.core.listener.JUnitReportAssertionListener
import argos.core.listener.LoggingAssertionListener
import argos.runtime.xml.strategy.*
import argos.runtime.xml.support.XmlParser
import argos.runtime.xml.support.findAll
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.toList
import gaia.sdk.HMACCredentials
import gaia.sdk.core.GaiaConfig
import org.reactivestreams.Publisher
import org.w3c.dom.NodeList
import java.io.File
import java.io.InputStream

/**
 * Class to parse Argos Assertions from XML instructions.
 */
class ArgosXML {
    private val include: File?
    private val assertions = mutableListOf<AssertionGroup>()
    private val scheme = this::class.java.getResourceAsStream("/argos.xsd")!!

    private constructor() {
        this.include = null
    }
    constructor(include: File?) {
        this.include = include
    }

    /**
     * A Data-Class which holds the parsed IAssertions.
     *
     * @param identityId the identity id
     */
     data class ParsedAssertions(
        val identityId: String,
        val name: String,
        val assertions: List<AssertionGroup>) {
         fun getAllAssertions() = assertions.flatMap { it.assertions }
     }


    /**
     * Parse argos assertions from XML with declared include File.
     *
     * @param input the InputStream of the XML instructions
     *
     * @return a ParsedAssertions instance of the Assertions from the XML
     */
    fun parse(input: InputStream): ParsedAssertions {
        val doc = XmlParser(scheme).invoke(input)

        val root = doc.getElementsByTagName("assertions").item(0)
        val identityId: String = root.findAttr("identityId").orElse("")
        val name: String = root.findAttr("name").orElse("")

        assertions.addAll(parseNodeList(root.childNodes, include))

        return ParsedAssertions(identityId, name, assertions)
    }

    companion object: AbstractArgos() {
        /**
         * Parse argos assertions from XML.
         *
         * @param input the InputStream of the XML instructions
         *
         * @return a ParsedAssertions instance of the Assertions from the XML
         */
        fun parse(input: InputStream): ParsedAssertions {
            return ArgosXML().parse(input)
        }

        // TODO: add Listener support
        fun argos(parsedAssertions: ParsedAssertions): Publisher<IAssertionResult> {
            val options = ArgosOptions(
                parsedAssertions.identityId,
                GaiaConfig("", HMACCredentials("key", "secret")))   // Todos

            options.addListener(LoggingAssertionListener())
            options.addListener(JUnitReportAssertionListener())

            return argos(parsedAssertions.name, options, parsedAssertions.assertions)
        }


        internal fun parseNodeList(nodeList: NodeList, include: File? = null): List<AssertionGroup> {
            val assertionGroups = mutableListOf<AssertionGroup>()

            val strategy = mapOf(
                "classificationAssertion" to ClassificationAssertionParser(),
                "conversationAssertion" to ConversationAssertionParser(include),
                "imageAssertion" to ImageAssertionParser(),
                "imageSimilarityAssertion" to ImageSimilarityAssertionParser(),
                "intentAssertion" to IntentAssertionParser(),
                "languageDetectionAssertion" to LanguageDetectionParser(),
                "nerAssertion" to NERAssertionsParser(),
                "ocrAssertion" to OCRAssertionParser(),
                "regressionAssertion" to RegressionAssertionParser(),
                "semanticSearchAssertion" to SemanticSearchAssertionParser(),
                "sentimentAssertion" to SentimentAssertionParser(),
                "similarityAssertion" to SimilarityAssertionParser(),
                "speech2textAssertion" to Speech2TextAssertionParser(),
                "text2speechAssertion" to Text2SpeechAssertionParser(),
                "translationAssertion" to TranslationAssertionParser()
            )

            nodeList.toList().filter { it.nodeName == "assertionGroup" }
                .forEach { assertionGroups.add(AssertionGroupParser().parse(it)) }
            nodeList.toList()
                .filter { strategy.containsKey(it.nodeName) }
                .forEach { assertionGroups.add(
                    AssertionGroup(null, listOf(strategy[it.nodeName]!!.parse(it))))
                }

            return assertionGroups
        }
    }

}