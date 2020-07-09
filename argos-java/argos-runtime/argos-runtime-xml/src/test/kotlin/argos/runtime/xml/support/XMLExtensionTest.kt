package argos.runtime.xml.support

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.assertj.core.api.Assertions.assertThat

@Tag("unitTest")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class XMLExtensionTest {

    private val intentAssertion = "intentAssertion"

    private val doc = XmlParser(this::class.java.classLoader.getResourceAsStream("argos.xsd"))
            .invoke(this::class.java.classLoader.getResourceAsStream("simpleIntent.xml"))
            .getElementsByTagName("assertions")

    @Test
    fun `test node extension map`() {
        val mapped = doc.map { it.textContent }
        assertThat(mapped).isNotNull().isNotEmpty()
        assertThat(mapped[0].replace("\n", "").trim()).isEqualTo("Wein empfehlen")
    }

    @Test
    fun `test node extension toList`() {
        val listed = doc.toList()
        assertThat(listed).isNotNull().isNotEmpty()
        assertThat(listed.size).isEqualTo(1)
    }

    @Test
    fun `test node extension filter`() {
        val filtered = doc.filter { it.textContent.isNotBlank() }
        assertThat(filtered).isNotNull().isNotEmpty()
    }

    @Test
    fun `test node extension find`() {
        val found = doc.item(0).find(intentAssertion)
        assertThat(found).isNotNull()
        assertThat((found as DeferredElementImpl).tagName).isEqualTo(intentAssertion)
    }

    @Test
    fun `test node extension findAll`() {
        val foundNodes = doc.item(0).findAll("testTagName", intentAssertion)
        assertThat(foundNodes).isNotEmpty()
        assertThat(foundNodes.size).isEqualTo(1)
        assertThat((foundNodes[0] as DeferredElementImpl).tagName).isEqualTo(intentAssertion)

    }

    @Test
    fun `test node extension findAttr`() {
        val foundAttribute = doc.item(0).findAttr("identityId")
        assertThat(foundAttribute).isPresent()
        assertThat(foundAttribute.get()).isEqualTo("13370000-0000-0000-0000-000000000000")
    }

    @Test
    fun`test node extension findText`() {
        val foundText = doc.item(0).findText(intentAssertion)
        assertThat(foundText).isNotBlank()
        assertThat(foundText).isEqualTo("Wein empfehlen")
    }

    @Test
    fun `test node extension toAttributes`() {
        val toAttributes = doc.item(0).toAttributes()
        assertThat(toAttributes).isNotEmpty()
        assertThat(toAttributes.size).isEqualTo(1)
        assertThat(toAttributes["identityId"]).isEqualTo("13370000-0000-0000-0000-000000000000")
    }
}