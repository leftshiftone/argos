package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.IntentAssertion
import argos.core.assertion.IntentAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.Node
import org.w3c.dom.NodeList

class IntentAssertionParser : AbstractAssertionParser() {

    override fun parse(node: Node): IAssertion {
        val text: String = node.textContent
        val intent: String = node.findAttr("name").get()

        return IntentAssertion((IntentAssertionSpec(text, intent)))
    }

}