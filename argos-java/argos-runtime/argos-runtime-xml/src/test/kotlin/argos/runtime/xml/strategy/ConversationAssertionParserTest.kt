package argos.runtime.xml.strategy

import argos.runtime.xml.support.XmlParser
import argos.runtime.xml.support.toAttributes
import argos.runtime.xml.support.toList
import org.junit.jupiter.api.Test
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileInputStream

class ConversationAssertionParserTest {
    private val scheme = this::class.java.getResourceAsStream("/argos.xsd")!!

    @Test fun `test feature_ambiguous_identity_supervisors`() = printConversationFromFile("feature_ambiguous_identity_supervisors")
    @Test fun `test feature_atreus_gaiaquery`() = printConversationFromFile("feature_atreus_gaiaquery")
    @Test fun `test feature_nested_prompt`() = printConversationFromFile("feature_nested_prompt")

    private fun printConversationFromFile(file: String) {
        val feature = File("../../argos-core/src/test/resources/gaia/feature/$file.xml")
        val doc = XmlParser(scheme).invoke(FileInputStream(feature))
        val conversationAssertions = doc.getElementsByTagName("conversationAssertion").toList()

//        fun printChildren(childNodes: NodeList) {
//            childNodes.toList().forEach { children ->
//                if (children.hasChildNodes())
//                    printChildren(children.childNodes)
//                else {
//                    val attr = children.toAttributes()
//                    print("['${children.nodeName}' (")
//                    attr.forEach { key, value ->
//                        print("'$key': $value")
//                    }
//                    print(")]: ${children.textContent}")
//                }
//            }
//        }

        for (conversationAssertion in conversationAssertions) {
            conversationAssertion.childNodes.toList().forEach {
                println("['${it.nodeName}' (${it.attributes})]: ${it.textContent}")
            }
        }
    }

}