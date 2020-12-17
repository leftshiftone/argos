package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.TranslationAssertion
import argos.core.assertion.TranslationAssertionSpec
import argos.runtime.xml.support.findAll
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.toList
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.lang.RuntimeException

class TranslationAssertionParser : AbstractAssertionParser() {
    override fun parse(node: Node): IAssertion {
        val threshold = node.findAttr("threshold").get().toFloat()
        val assertionText = node.findAll("text")

        if (assertionText.size != 2) throw RuntimeException()
        val textList: MutableList<Map<String, String>> = emptyList<Map<String, String>>().toMutableList()
        for (aText in assertionText) {
            textList.add(mapOf(
                    "text" to aText.textContent,
                    "lang" to aText.attributes.getNamedItem("lang")?.textContent!!))
        }

        return TranslationAssertion(TranslationAssertionSpec(
                textList[0].get("lang")!!,
                textList[0].get("text")!!,
                textList[1].get("lang")!!,
                textList[1].get("text")!!,
                threshold))
    }

}