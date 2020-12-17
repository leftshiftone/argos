package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.Speech2TextAssertion
import argos.core.assertion.Speech2TextAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.Node
import org.w3c.dom.NodeList

class Speech2TextAssertionParser: AbstractAssertionParser() {
    override fun parse(node: Node): IAssertion {
        val speech = node.findAttr("speech").get()
        val text = node.textContent

        return Speech2TextAssertion(Speech2TextAssertionSpec(speech, text))
    }
}