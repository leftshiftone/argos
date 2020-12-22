package argos.runtime.xml

import argos.api.ArgosOptions
import argos.api.AssertionGroup
import argos.api.IAssertionResult
import argos.core.assertion.AbstractArgos
import argos.core.listener.JUnitReportAssertionListener
import argos.core.listener.LoggingAssertionListener
import argos.runtime.xml.strategy.*
import argos.runtime.xml.support.XmlParser
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

    /**
     * ArgosXML constructor
     *
     * @param include a folder which holds XML files to include,
     * needed by some assertion test cases e.g. ConversationAssertion
     */
    constructor(include: File?) {
        this.include = include
    }

    /**
     * A Data-Class which holds all assertions and information for an argos test.
     *
     * @param identityId the identity id
     * @param name the name of this argos test
     * @param assertions a list of assertion groups which hold the assertions
     */
     data class ParsedAssertions(
        val identityId: String,
        val name: String,
        val assertions: List<AssertionGroup>) {
        /**
         * Get every assertion from all assertion groups.
         *
         * @return a list which holds every assertion
         */
        fun getAllAssertions() = assertions.flatMap { it.assertions }
     }

    /**
     * Parse argos assertions from XML instructions.
     *
     * @param input the InputStream of the XML instructions
     *
     * @return a <code>ParsedAssertions</code> instance which holds the assertions
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
         * Parse argos assertions from XML instructions.
         *
         * @param input the InputStream of the XML instructions
         *
         * @return a <code>ParsedAssertions</code> instance which holds the assertions
         */
        fun parse(input: InputStream): ParsedAssertions {
            return ArgosXML().parse(input)
        }

        /**
         * Execute an argos test from the given parsed assertions.
         *
         * @param parsedAssertions a ParsedAssertions instance which holds the assertions
         * @param options the required options to connect to a gaia instance
         *
         * @return the result of the assertion test as an instance of a IAssertionResult-Publisher
         */
        fun argos(parsedAssertions: ParsedAssertions, options: ArgosOptions): Publisher<IAssertionResult> {

            return argos(parsedAssertions.name, options, parsedAssertions.assertions)
        }

        /**
         * Execute an argos test from the given parsed assertions.
         *
         * @param parsedAssertions a ParsedAssertions instance which holds the assertions
         *
         * @return the result of the assertion test as an instance of a IAssertionResult-Publisher
         */
        fun argos(parsedAssertions: ParsedAssertions): Publisher<IAssertionResult> {
            return argos(parsedAssertions, ArgosOptions(parsedAssertions.identityId))
        }

        /**
         * Parse a <code>NodeList</code> for assertion tags.
         *
         * @param nodeList the <code>NodeList</code> to parse
         * @param include the include folder for include-tags
         *
         * @return  the assertions divided in assertion groups;
         *          if an assertion is not part of an assertion group,
         *           it will be added as a single item to an assertion group with <code>null</code> as group name
         */
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