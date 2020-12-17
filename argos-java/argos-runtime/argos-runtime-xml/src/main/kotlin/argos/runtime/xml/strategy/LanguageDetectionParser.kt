package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.LanguageDetectionAssertion
import argos.core.assertion.LanguageDetectionAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.Node
import org.w3c.dom.NodeList

class LanguageDetectionParser: AbstractAssertionParser() {

    override fun parse(node: Node): IAssertion {
        val text = node.textContent
        val lang = node.findAttr("lang").get()

        return LanguageDetectionAssertion(LanguageDetectionAssertionSpec(text, lang))
    }
}