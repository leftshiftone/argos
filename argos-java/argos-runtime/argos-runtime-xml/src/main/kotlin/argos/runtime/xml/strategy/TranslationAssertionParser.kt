package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.TranslationAssertion
import argos.core.assertion.TranslationAssertionSpec
import argos.runtime.xml.support.findAll
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.toList
import org.w3c.dom.NodeList

class TranslationAssertionParser : AbstractAssertionParser() {
    override fun parse(nodeList: NodeList): List<IAssertion> {
        return nodeList.toList().flatMap {
            val threshold = it.findAttr("threshold").get().toFloat()
            val assertionText = it.findAll("text")
            if (assertionText.size == 2) {
                val textList: MutableList<Map<String, String>> = emptyList<Map<String, String>>().toMutableList()
                for (aText in assertionText) {
                    textList.add(mapOf(
                            "text" to aText.textContent,
                            "lang" to aText.attributes.getNamedItem("lang")?.textContent!!))
                }

                listOf(TranslationAssertion(TranslationAssertionSpec(
                        textList[0].get("lang")!!,
                        textList[0].get("text")!!,
                        textList[1].get("lang")!!,
                        textList[1].get("text")!!,
                        threshold)))
            }else {
                emptyList()
            }
        }
    }

}