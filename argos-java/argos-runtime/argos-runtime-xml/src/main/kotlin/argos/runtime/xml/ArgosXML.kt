package argos.runtime.xml

import argos.core.assertion.IntentAssertion
import argos.core.assertion.IntentAssertionSpec
import argos.runtime.xml.support.XmlParser
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.toList
import java.io.FileInputStream
import java.io.InputStream

// TODO: javadoc
class ArgosXML {
    data class ParsedAssertions(val identityId: String, val intentAssertionList: List<IntentAssertion>)

    fun parse(input: InputStream): ParsedAssertions {
        val intentAssertions: MutableList<IntentAssertion> = emptyList<IntentAssertion>().toMutableList()
        val doc = XmlParser(FileInputStream("./src/main/resources/argos.xsd")).invoke(input)

        val identityId: String = doc.getElementsByTagName("assertions").item(0)
                .findAttr("identityId").get()
        val assertions = doc.getElementsByTagName("intentAssertion")

        for(assertion in assertions.toList()) {
            val text = assertion.textContent
            val intent = assertion.findAttr("name").get()

            intentAssertions.add(IntentAssertion((IntentAssertionSpec(text, intent))))
        }

        return ParsedAssertions(identityId, intentAssertions)
    }

}
