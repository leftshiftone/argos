package argos.runtime.xml

import argos.api.IAssertion
import argos.runtime.xml.strategy.*
import argos.runtime.xml.support.XmlParser
import argos.runtime.xml.support.findAttr
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

// TODO: javadoc
class ArgosXML private constructor() {
    data class ParsedAssertions(val identityId: String, val assertionList: List<IAssertion>)

    private val assertions: MutableList<IAssertion> = emptyList<IAssertion>().toMutableList()
    private val scheme = File("./src/main/resources/argos.xsd")

    companion object {
        fun parse(input: InputStream): ParsedAssertions {
            val xml = ArgosXML()
            val doc = XmlParser(FileInputStream(xml.scheme)).invoke(input)

            val identityId: String = doc.getElementsByTagName("assertions").item(0)
                    .findAttr("identityId").get()
            val intentAssertions = doc.getElementsByTagName("intentAssertion")
            val similarityAssertions = doc.getElementsByTagName("similarityAssertion")
            val nerAssertions = doc.getElementsByTagName("nerAssertion")
            val translationAssertions = doc.getElementsByTagName("translationAssertion")
            val sentimentAssertions = doc.getElementsByTagName("sentimentAssertion")
            val imageSimilarityAssertions = doc.getElementsByTagName("imageSimilarityAssertion")
            val conversationAssertions = doc.getElementsByTagName("conversationAssertion")

            xml.assertions.addAll(TranslationAssertionParser().parse(translationAssertions))
            xml.assertions.addAll(NERAssertionsParser().parse(nerAssertions))
            xml.assertions.addAll(SimilarityAssertionParser().parse(similarityAssertions))
            xml.assertions.addAll(IntentAssertionParser().parse(intentAssertions))
            xml.assertions.addAll(ConversationAssertionParser().parse(conversationAssertions))
            xml.assertions.addAll(SentimentAssertionParser().parse(sentimentAssertions))
            xml.assertions.addAll(ImageSimilarityAssertionParser().parse(imageSimilarityAssertions))

            return ParsedAssertions(identityId, xml.assertions)
        }
    }

}