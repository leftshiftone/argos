package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.LanguageDetectionAssertion
import argos.core.assertion.LanguageDetectionAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.NodeList

class LanguageDetectionParser: AbstractAssertionParser() {

    override fun parse(nodeList: NodeList): List<IAssertion> {
        return nodeList.map {
            val text = it.textContent
            val lang = it.findAttr("lang").get()

            LanguageDetectionAssertion(LanguageDetectionAssertionSpec(text, lang))
        }
    }
}