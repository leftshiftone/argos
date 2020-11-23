package argos.runtime.xml

import argos.api.IAssertion
import argos.runtime.xml.strategy.*
import argos.runtime.xml.support.XmlParser
import argos.runtime.xml.support.findAttr
import java.io.InputStream

/**
 * Class to parse Argos Assertions from XML instructions.
 */
class ArgosXML private constructor() {

    /**
     * A Data-Class which holds the parsed IAssertions.
     *
     * @param identityId the identity id
     * @param assertionList a list of the parsed IAssertions
     */
    data class ParsedAssertions(val identityId: String, val assertionList: List<IAssertion>)

    private val assertions: MutableList<IAssertion> = emptyList<IAssertion>().toMutableList()
    private val scheme = this::class.java.getResourceAsStream("/argos.xsd")!!

    companion object {
        /**
         * Parse argos assertions from XML.
         *
         * @param input the InputStream of the XML instructions
         *
         * @return a ParsedAssertions instance of the Assertions from the XML
         */
        fun parse(input: InputStream): ParsedAssertions {
            val xml = ArgosXML()
            val doc = XmlParser(xml.scheme).invoke(input)

            val identityId: String = doc.getElementsByTagName("assertions").item(0)
                    .findAttr("identityId").get()
            val intentAssertions = doc.getElementsByTagName("intentAssertion")
            val similarityAssertions = doc.getElementsByTagName("similarityAssertion")
            val nerAssertions = doc.getElementsByTagName("nerAssertion")
            val translationAssertions = doc.getElementsByTagName("translationAssertion")
            val sentimentAssertions = doc.getElementsByTagName("sentimentAssertion")
            val imageSimilarityAssertions = doc.getElementsByTagName("imageSimilarityAssertion")
            val conversationAssertions = doc.getElementsByTagName("conversationAssertion")
            val ocrAssertions = doc.getElementsByTagName("ocrAssertion")
            val languageDetectionAssertions = doc.getElementsByTagName("languageDetectionAssertion")
            val classificationAssertions = doc.getElementsByTagName("classificationAssertion")
            val regressionAssertions = doc.getElementsByTagName("regressionAssertion")
            val imageAssertions = doc.getElementsByTagName("imageAssertion")
            val text2speechAssertions = doc.getElementsByTagName("text2speechAssertion")
            val speech2textAssertions = doc.getElementsByTagName("speech2textAssertion")
            val semanticSearchAssertions = doc.getElementsByTagName("semanticSearchAssertion")

            xml.assertions.addAll(TranslationAssertionParser().parse(translationAssertions))
            xml.assertions.addAll(NERAssertionsParser().parse(nerAssertions))
            xml.assertions.addAll(SimilarityAssertionParser().parse(similarityAssertions))
            xml.assertions.addAll(IntentAssertionParser().parse(intentAssertions))
            xml.assertions.addAll(ConversationAssertionParser().parse(conversationAssertions))
            xml.assertions.addAll(SentimentAssertionParser().parse(sentimentAssertions))
            xml.assertions.addAll(ImageSimilarityAssertionParser().parse(imageSimilarityAssertions))
            xml.assertions.addAll(OCRAssertionParser().parse(ocrAssertions))
            xml.assertions.addAll(LanguageDetectionParser().parse(languageDetectionAssertions))
            xml.assertions.addAll(ClassificationAssertionParser().parse(classificationAssertions))
            xml.assertions.addAll(RegressionAssertionParser().parse(regressionAssertions))
            xml.assertions.addAll(ImageAssertionParser().parse(imageAssertions))
            xml.assertions.addAll(Text2SpeechAssertionParser().parse(text2speechAssertions))
            xml.assertions.addAll(Speech2TextAssertionParser().parse(speech2textAssertions))
            xml.assertions.addAll(SemanticSearchAssertionParser().parse(semanticSearchAssertions))

            return ParsedAssertions(identityId, xml.assertions)
        }
    }

}