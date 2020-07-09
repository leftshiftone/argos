package argos.runtime.xml.support

import argos.api.IAssertion
import argos.core.assertion.IntentAssertion
import argos.core.assertion.IntentAssertionSpec
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

// TODO: javadoc
class XmlParser(private val xsdStream: InputStream) : (InputStream) -> Document {

    private val assertions = listOf<String>("intentAssertion")

    private val parser = ThreadLocal.withInitial {
        val builder = DocumentBuilderFactory.newInstance()
        builder.newDocumentBuilder()
    }!!

    private val validator = ThreadLocal.withInitial {
        val factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        val schema = factory.newSchema(StreamSource(xsdStream))
        schema.newValidator()
    }!!

    override fun invoke(stream: InputStream): Document {
        val documentBuilder = parser.get()
        val validator = validator.get()

        try {
            val bytes = stream.readBytes()
            validator.validate(StreamSource(ByteArrayInputStream(bytes)))
            return documentBuilder.parse(ByteArrayInputStream(bytes))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun parse(node: Node): MutableList<IAssertion> {
        return when (node.nodeName) {
            "assertions" -> determineAssertion(node.findAll(*assertions.toTypedArray()))
            else -> throw NotImplementedError("Parser not yet implemented")
        }
    }

    private fun determineAssertion(assertions: List<Node>): MutableList<IAssertion> {
        var parsedAssertions = mutableListOf<IAssertion>()
        assertions.forEach {
            when (it.nodeName) {
                "intentAssertion" -> parsedAssertions.add(parseIntentAssertion(it))
            }
        }
        return parsedAssertions
    }

    private fun parseIntentAssertion(node: Node) =
         IntentAssertion(IntentAssertionSpec(node.textContent, node.attributes.getNamedItem("name").nodeValue))


    fun getStartNode(document: Document, startNode: String) : Node {
        for (i in 0 until document.childNodes.length) {
            val node = document.childNodes.item(i)
            if (node.nodeName == startNode) {
                return node
            }
        }
        throw RuntimeException("no $startNode tag found")
    }



}
