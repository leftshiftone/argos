package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.IntentAssertion
import argos.core.assertion.IntentAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.NodeList

class IntentAssertionParser : AbstractAssertionParser() {

    override fun parse(nodeList: NodeList): List<IAssertion> {
        return nodeList.map {
            val text: String = it.textContent
            val intent: String = it.findAttr("name").get()

            IntentAssertion((IntentAssertionSpec(text, intent)))
        }
    }

}