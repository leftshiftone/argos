package argos.runtime.xml.support

import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

// TODO: javadoc
class XmlParser(private val xsdStream: InputStream) : (InputStream) -> Document {

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

}
