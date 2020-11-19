package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.Text2SpeechAssertion
import argos.core.assertion.Text2SpeechAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.map
import org.w3c.dom.NodeList

class Text2SpeechAssertionParser: AbstractAssertionParser() {
    override fun parse(nodeList: NodeList): List<IAssertion> {
        return nodeList.map {
            val text = it.textContent
            val speech = it.findAttr("speech").get()

            Text2SpeechAssertion(Text2SpeechAssertionSpec(text, speech))
        }
    }
}