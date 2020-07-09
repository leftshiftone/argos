package argos.runtime.xml.support

import argos.core.assertion.IntentAssertion
import com.sun.org.apache.xerces.internal.dom.DeferredAttrImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Tag("unitTest")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class XMLParserTest {

    @Test
    fun `test intent xml parser validation`() {
        val element = XmlParser(this::class.java.classLoader.getResourceAsStream("argos.xsd"))
                .invoke(this::class.java.classLoader.getResourceAsStream("simpleIntent.xml"))
                .getElementsByTagName("assertions")
        assertThat(element).isNotNull()
        val assertion = (element.item(0).attributes.item(0) as DeferredAttrImpl)
        assertThat(assertion.name).isEqualTo("identityId")
        assertThat(assertion.value).isEqualTo("13370000-0000-0000-0000-000000000000")
    }

    @Test
    fun `test intent parse`() {
        val xmlParser = XmlParser(this::class.java.classLoader.getResourceAsStream("argos.xsd"))
        val document = xmlParser.invoke(this::class.java.classLoader.getResourceAsStream("simpleIntent.xml"))
        val intentAssertions = xmlParser.parse(xmlParser.getStartNode(document, "assertions"))

        assertThat(intentAssertions).isNotNull().isNotEmpty()
        assertThat((intentAssertions[0] as IntentAssertion).spec!!.text).isEqualTo("Wein empfehlen")
        assertThat((intentAssertions[0] as IntentAssertion).spec!!.intent).isEqualTo("recommend_wine")
    }



}