package argos.runtime.xml.strategy

import argos.api.IAssertion
import argos.core.assertion.SentimentAssertion
import argos.core.assertion.SentimentAssertionSpec
import argos.runtime.xml.support.findAttr
import argos.runtime.xml.support.toList
import org.w3c.dom.NodeList

class SentimentAssertionParser : AbstractAssertionParser() {
    override fun parse(nodeList: NodeList): List<IAssertion> {
        return nodeList.toList().map {
            val text = it.textContent
            val type = it.findAttr("type").get()

            return@map SentimentAssertion(SentimentAssertionSpec(text, type))
        }
    }

}